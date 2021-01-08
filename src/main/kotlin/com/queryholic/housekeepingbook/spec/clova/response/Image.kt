package com.queryholic.housekeepingbook.spec.clova.response

data class Image(
        val uid: String
        , val name: String
        , val inferResult: String
        , val message: String
        , val title: String?
        , val fields: List<Field>
)
