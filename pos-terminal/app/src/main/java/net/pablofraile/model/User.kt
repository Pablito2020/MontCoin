package net.pablofraile.model

data class Id(val value: String) {
    fun toByteArray(): ByteArray = value.toByteArray()
}

data class User(val id: Id, val name: String, val amount: Amount, val numberOfOperations: Int = 0)