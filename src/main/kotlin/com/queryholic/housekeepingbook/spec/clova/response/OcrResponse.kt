package com.queryholic.housekeepingbook.spec.clova.response

data class OcrResponse(
        val version: String
        , val requestId: String
        , val timestamp: Long
        , val images: List<Image>
)
