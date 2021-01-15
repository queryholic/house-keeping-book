package com.queryholic.housekeepingbook.extension

val REGEX_SPECIAL_CHARACTER = Regex("[^\uAC00-\uD7A3xfe0-9a-zA-Z|\\s]")
val REGEX_ONLY_NUMBER = Regex("[^0-9\\s]")

fun String.removeSpecialCharacter(): String =
        this.replace(REGEX_SPECIAL_CHARACTER, "")

fun String.onlyNumber(): String =
        this.replace(REGEX_ONLY_NUMBER, "")