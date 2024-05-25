package com.pablofraile.montcoin.ui.operation

data class Amount(val value: String) {

    override fun toString(): String {
        return value
    }

    fun isValid(): Boolean {
        try {
            value.toInt()
            return !isEmpty()
        } catch (e: NumberFormatException) {
            return false
        }
    }

    fun isEmpty(): Boolean {
        return value.isEmpty()
    }

    fun toInt(): Int {
        if (!isValid())
            throw IllegalStateException("Can't convert amount if it has an invalid number")
        return value.toInt()
    }

}