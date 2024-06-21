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
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

const val PRIVATE_KEY = """
-----BEGIN PRIVATE KEY-----
MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCp8yFIXkbJvmvt
wX327uU16Ij/KJHtF9zFHJxowN8Up+fqd0A2gucXDs9CicosxJO5cDV5IB3Pz58g
Ec6ZD5CWo8ybSjW1hmPCYl28Y6+KGDrpkiK9lGpzLS0h3p9M7oHA5IhpyQLcrrt5
sKwqakFhZpecCE2YAv3cJAoWdOd0B7y33Hve+BbZ+5TuJHj00FAKr6+2K6GQAPTm
1YZtcnnv1O0GopsRE59AVnlDfHzFQUlI0e9NKp+iZXsmOdI5qjIrsC6Bv2Ryti0R
qfTE3W/sOdAkLFTAyH/lGAKLdJ4pRdmcTYj50tpl8uWAJR/UTl5ac0UTvFvd+G/w
j4aLzFiZAgMBAAECggEADZlCxIOmrmXqRPACoEulU5bzv1cDtjD+nVcYvNkSAJke
2YgJP1NiIloxA2X7I3HnUCNGZBbJy+MVHH/QRDcWnrcknBhoC8phGSC912Eynp4p
uJ7U4M6LZgPrZJZnsMQ05bZjo/NNo1Ln9WjJnes6qAyJfRa8/rjej0ri88EO9B24
/eqLIKJMQu1E5gnvTXTaFT7Zhfh562GxQSJKknqndIgSvMzK1MO/I74Rd2NONmSw
5djyY+1UQ4aNuaRnfy1+TX7YeKqmEHkWKEcNSG2uEJfvQxnfFqkEYeynH/ls7e9s
Kd44k7r7kL07aDWt9Rr6z/JbMtL0OjHi0ZiPe7IjMQKBgQDTUZ724pDN22EiVQ9X
8WiOJ/yFrFl6+h4rEKri3y442OUWA4jdLiIg6yA3dSP3waX6XV7ylej0ozxfCq4k
HKJKJTMLVBQBcLghFRkCCWe3mBvjhYjCgtjGX6Ggv3nYLTxal7ntREch7082vS8p
Is/bnZSIoRU60GqCAZN2uComTwKBgQDN4kKLqlH09+OuxnE7NZUVKe2cO1Jy8Xyh
eQto79eW2m0LTVOPVgb6WtDaPc7GismfP1NXeV6PCAFopOmMvhS40bWrgE/d4ECB
eRcF5Ohq4+8Q/LRB9a5bD9vfIUk6ZalSCud0j0tQyckq4ias8LdWqLM7gvNUvl3X
5KpizeRAlwKBgCjemVMAYKcfrbqZHt2QV/team1j9u5c32bO024LYgImmK/YQSkd
2gXp05JGOo2ZS0OPuWRLcAGYbH2pMCLV0uFqLmQbf78DkOj9DpghP4j3hzVKxzdv
XQOIzVaskaEopoqS26ey09sRUI2yqyNWk0LTSD3ggZ2dX2wDaATG+4frAoGASoG4
PVz83d8buXM9JQVGF2ud/q0FMG2uAa2RcH9jkMcpMS7hr3ydd9qHvIMjonw1bPj4
yy40ByUJhXowvutHCn9x9EeyH4R5M6HDsDFf3tbv/EDHZxtm5rN9iWk9W/HQbc1e
Z7M3uVvc/jTrdCiE4MqaWk4Qizl9MpUhCDKkiK8CgYBwbbj8RZGwINerZ5wieF/A
7+2/6ve0mVDy0Gyvufmq3ozXQPQNfj+4TP4URP9AcSqbd3ap4bYNeCwTl7XcPSWK
h+TdJhleFM7Uc3dXecFU7dN0lScCwEZIhnL4evv0pe2u4tpwgTzDpK0XNVgjJLTL
ELJMF8YjF4JVXKD+KDHNxw==
-----END PRIVATE KEY-----
"""

fun getPrivateKey(): PrivateKey? {
    val pkcs8Lines = StringBuilder()
    val rdr = BufferedReader(StringReader(PRIVATE_KEY))
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

suspend fun getSignatureForMap(values: Map<String, Any>): Pair<Int, String> {
    val newValues: MutableMap<String, Any> = values.toMutableMap()
    val date = 1234567890
    newValues["date"] = date
    var token = JWT.create()
    for (key in newValues.keys) {
        if (newValues[key] is Int)
            token = token.withClaim(key, newValues[key] as Int)
        else if (newValues[key] is String)
            token = token.withClaim(key, newValues[key] as String)
        else if (newValues[key] is Boolean)
            token = token.withClaim(key, newValues[key] as Boolean)
        else
            throw IllegalArgumentException("Unsupported type")
    }
    return Pair(date, token.sign(Algorithm.RSA256(getPrivateKey() as RSAPrivateKey)))
}

private interface Signed {
    val signature: String
    val date: Int
}

class SignedWriteOperation(
    amount: Int,
    should_fail_if_not_enough_money: Boolean,
    with_credit_card: Boolean,
    override val signature: String,
    override val date: Int
) : WriteOperation(amount, should_fail_if_not_enough_money, with_credit_card), Signed

suspend inline fun <T : Any, reified R> T.sign(): R where R : Signed {
    val originalClass = this::class
    val originalProperties = originalClass.memberProperties
    val valuesMap = originalProperties.associate { prop ->
        prop.name to prop.getter.call(this) as Any
    }
    val (date, signature) = getSignatureForMap(valuesMap)
    val constructor = R::class.primaryConstructor!!
    val args = constructor.parameters.associateWith { parameter ->
        when (parameter.name) {
            "signature" -> signature
            "date" -> date
            else -> originalProperties.find { it.name == parameter.name }?.getter?.call(this)
        }
    }
    return constructor.callBy(args)
}