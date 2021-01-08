package com.queryholic.housekeepingbook.spec.clova.response

data class Field(
        val name: String?
        , val valueType: String?
        , val inferText: String
        , val inferConfidence: Double
//        , val boundingPoly: String
        , val type: String?
//        , val subFields: String
        , val checked: Boolean?
)
