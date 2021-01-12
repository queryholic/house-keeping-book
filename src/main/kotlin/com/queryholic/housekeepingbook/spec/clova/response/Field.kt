package com.queryholic.housekeepingbook.spec.clova.response

data class Field(
        val name: String? = "name"
        , val valueType: String? = "ALL"
        , val inferText: String
        , val inferConfidence: Double
        , val boundingPoly: Vertices
        , val type: String? = "NORMAL"
        , val checked: Boolean?
) {
    constructor(inferText: String, boundingPoly: Vertices, inferConfidence: Double) :
            this(name = "name", valueType = "ALL", inferText = inferText, inferConfidence = inferConfidence, boundingPoly = boundingPoly, type = "NORMAL", checked = null)
}
