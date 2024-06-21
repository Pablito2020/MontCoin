package com.pablofraile.montcoin.data.api

import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

fun getSignatureForMap(values: Map<String, String>): String {
    return "fakeSignature"
}

private interface Signed {
    val signature: String
}

class SignedWriteOperation(
    val amount: Int,
    val should_fail_if_not_enough_money: Boolean,
    val with_credit_card: Boolean,
    override val signature: String
) : Signed

inline fun <T : Any, reified R> createSignedInstance(original: T): R where R : Signed {
    val originalClass = original::class
    val originalProperties = originalClass.memberProperties
    val valuesMap = originalProperties.associate { prop ->
        prop.name to prop.getter.call(original).toString()
    }
    val signature = getSignatureForMap(valuesMap)
    val constructor = R::class.primaryConstructor!!
    val args = constructor.parameters.associateWith { parameter ->
        when (parameter.name) {
            "signature" -> signature
            else -> originalProperties.find { it.name == parameter.name }?.getter?.call(original)
        }
    }
    return constructor.callBy(args)
}