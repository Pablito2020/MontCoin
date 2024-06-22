package com.pablofraile.montcoin.data.api

import android.util.Base64
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.io.BufferedReader
import java.io.StringReader
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

data class Credentials(val privateKey: String) {

    private fun getPrivateKey(): PrivateKey? {
        val pkcs8Lines = StringBuilder()
        val rdr = BufferedReader(StringReader(privateKey))
        var line: String?
        while (rdr.readLine().also { line = it } != null) {
            pkcs8Lines.append(line)
        }
        var pkcs8Pem = pkcs8Lines.toString()
        pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "")
        pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "")
        pkcs8Pem = pkcs8Pem.replace("\\s+".toRegex(), "")
        val pkcs8EncodedBytes = Base64.decode(pkcs8Pem, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(pkcs8EncodedBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(keySpec)
    }

    fun getSignatureForMap(values: Map<String, Any>): String {
        val newValues: MutableMap<String, Any> = values.toMutableMap()
        val date = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        newValues["date"] = date
        var token = JWT.create()
        for (key in newValues.keys) {
            if (newValues[key] is Int)
                token = token.withClaim(key, newValues[key] as Int)
            else if (newValues[key] is String)
                token = token.withClaim(key, newValues[key] as String)
            else if (newValues[key] is Boolean)
                token = token.withClaim(key, newValues[key] as Boolean)
            else if (newValues[key] is Long)
                token = token.withClaim(key, newValues[key] as Long)
            else
                throw IllegalArgumentException("Unsupported type")
        }
        return token.sign(Algorithm.RSA256(getPrivateKey() as RSAPrivateKey))
    }

}

interface Signed {
    val signature: String
}

inline fun <T : Any, reified R> T.sign(credentials: Credentials): R where R : Signed {
    val originalClass = this::class
    val originalProperties = originalClass.memberProperties
    val valuesMap = originalProperties.associate { prop ->
        prop.name to prop.getter.call(this) as Any
    }
    val signature = credentials.getSignatureForMap(valuesMap)
    val constructor = R::class.primaryConstructor!!
    val args = constructor.parameters.associateWith { parameter ->
        when (parameter.name) {
            "signature" -> signature
            else -> originalProperties.find { it.name == parameter.name }?.getter?.call(this)
        }
    }
    return constructor.callBy(args)
}