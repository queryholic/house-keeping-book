package com.queryholic.housekeepingbook.extension

val REGEX_SPECIAL_CHARACTER = Regex("[^\uAC00-\uD7A3xfe0-9a-zA-Z|\\s]")

fun String.removeSpecialCharacter(): String =
        this.replace(REGEX_SPECIAL_CHARACTER, "")