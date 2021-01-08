package com.queryholic.housekeepingbook.service

import com.queryholic.housekeepingbook.spec.clova.request.Image
import com.queryholic.housekeepingbook.spec.clova.request.OcrRequest
import com.queryholic.housekeepingbook.spec.clova.response.OcrResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Service
class OcrService(
        private val ocrWebClient: WebClient
) {

    suspend fun inferText(imageUrl: String): Mono<String> = ocrWebClient.post()
            .bodyValue(OcrRequest(
                    requestId = "String", images = listOf(Image(url = imageUrl, name = "name"))
            ))
            .retrieve()
            .bodyToMono(OcrResponse::class.java)
            .map {
                it.images.last().fields.map { field ->
                    field.inferText
                }
            }
            .map {
                it.stream().collect(Collectors.joining())
            }
            .doOnError {
                throw it
            }
}