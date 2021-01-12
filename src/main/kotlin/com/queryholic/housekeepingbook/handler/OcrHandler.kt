package com.queryholic.housekeepingbook.handler

import com.queryholic.housekeepingbook.service.OcrService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class OcrHandler(
        private val ocrService: OcrService) {

    suspend fun inferText(request: ServerRequest)
            : ServerResponse {

        val imageUrl: String = request.queryParam("imageUrl").orElseThrow {
            throw IllegalArgumentException("query param is empty")
        }
        val inferText = ocrService.inferText(imageUrl)

        return ok()
                .bodyValueAndAwait("inferText" to inferText)
    }
}