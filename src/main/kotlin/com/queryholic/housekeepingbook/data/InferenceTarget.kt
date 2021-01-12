package com.queryholic.housekeepingbook.data

enum class InferenceTarget(
        val input: String
) {
    ALL("all"), TOTAL_AMOUNT("total-amount"), ITEMS("items"), MERCHANT("merchant");

    companion object {
        @JvmStatic
        fun of(input: String) = values().find { it.input == input }
    }
}