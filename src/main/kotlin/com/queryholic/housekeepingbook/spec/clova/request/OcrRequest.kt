package com.queryholic.housekeepingbook.spec.clova.request

data class OcrRequest(
        val version: String? = "V2"
        , val requestId: String
        , val timestamp: Long? = 0
        , val lang: String? = "ko"
        , val images: List<Image>
)