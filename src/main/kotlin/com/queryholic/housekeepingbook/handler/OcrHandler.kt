package com.queryholic.housekeepingbook.handler

import com.queryholic.housekeepingbook.data.InferenceTarget
import com.queryholic.housekeepingbook.extension.removeSpecialCharacter
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

        return ok()
                .bodyValueAndAwait(mapOf("inferText" to target?.let { getInferenceList(it, imageUrl) }))
    }

    private suspend fun getInferenceList(target: InferenceTarget, imageUrl: String): List<String>? {
        when (target) {
            InferenceTarget.ALL -> {
                return ocrService.inferText(imageUrl, "")
            }
            InferenceTarget.TOTAL_AMOUNT -> {
                return ocrService.inferText(imageUrl, "").stream()
                        .filter { it.contains("합계") }
                        .map { it.split(":")[1].removeSpecialCharacter() }
                        .collect(Collectors.toList())
            }
            InferenceTarget.MERCHANT -> {
                return listOf(ocrService.inferText(imageUrl, "")[0])
            }
            InferenceTarget.ITEMS -> {
                val rawText = ocrService.inferText(imageUrl, "|")
                val itemHeaderLineNumber: Int = rawText.indexOfFirst { it.contains("|단가|") }

                if (itemHeaderLineNumber == -1) {
                    return listOf()
                }

                return getItems(rawText, itemHeaderLineNumber)
            }
            else -> {
                throw IllegalStateException("no target error")
            }
        }
    }

    fun getItems(rawText: List<String>, itemHeaderLineNumber: Int): List<String> {
        // 상품명이 001 로 시작하고, 해당 상품명 아랫줄에 단가 수량 금액 적힌 패턴일때
        if (rawText[itemHeaderLineNumber + 1].contains("001")) {
            val itemStartIndex: Int = itemHeaderLineNumber + 1

            return listOf(rawText[itemHeaderLineNumber]) +
                    rawText.asSequence().drop(itemStartIndex)
                            .mapIndexed { index, s -> index to s }
                            .filter { "[0-9]{3}".toRegex().matches(it.second.split("|")[0]) }
                            .map { it.first + itemStartIndex }
                            .map { itemIndex ->
                                val itemLine1 = rawText[itemIndex]
                                val itemLine2 = "|" +
                                        rawText[itemIndex + 1]
                                                .split("|")
                                                .stream()
                                                .skip(1)
                                                .map {
                                                    it.removeSpecialCharacter()
                                                }
                                                .filter { it.isNotBlank() }
                                                .collect(Collectors.joining("|"))

                                itemLine1 + itemLine2
                            }
        }

        // 상품명과 단가 수량 금액이 한 줄에 적힌 패턴

        return listOf()
    }
}