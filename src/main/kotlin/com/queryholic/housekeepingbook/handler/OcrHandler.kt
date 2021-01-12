package com.queryholic.housekeepingbook.handler

import com.queryholic.housekeepingbook.data.InferenceTarget
import com.queryholic.housekeepingbook.service.OcrService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.util.stream.Collectors

@Component
class OcrHandler(
        private val ocrService: OcrService) {

    suspend fun inferText(request: ServerRequest)
            : ServerResponse {

        val imageUrl: String = request.queryParam("imageUrl").orElseThrow {
            throw IllegalArgumentException("query param is empty")
        }

        val target: InferenceTarget? = InferenceTarget.of(request.pathVariable("target"))

        val inferText: List<String>? = target?.let { getInferenceList(it, imageUrl) }

        return ok()
                .bodyValueAndAwait("inferText" to inferText)
    }

    private suspend fun getInferenceList(target: InferenceTarget, imageUrl: String): List<String> {
        if (target == InferenceTarget.ALL) {
            return ocrService.inferText(imageUrl)
        } else if (target == InferenceTarget.TOTAL_AMOUNT) {
            return ocrService.inferText(imageUrl).stream()
                    .filter { it.contains("합계") }
                    .map { it.split(":")[1] }
                    .collect(Collectors.toList())
        } else if (target == InferenceTarget.MERCHANT) {
            return ocrService.inferText(imageUrl)?.let {
                listOf(it[0])
            }
        } else {
            throw IllegalStateException("no target error")
        }
    }
}