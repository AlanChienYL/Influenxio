package com.influenxio.alan

data class Counter(
    var name: String? = null,
    var processing: String? = "idle",
    var processed: String = ""
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Counter -> other.name == name
            else -> super.equals(other)
        }
    }
}