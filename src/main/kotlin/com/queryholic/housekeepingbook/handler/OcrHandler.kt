package com.queryholic.housekeepingbook.handler

import com.queryholic.housekeepingbook.data.InferenceTarget
import com.queryholic.housekeepingbook.extension.onlyNumber
import com.queryholic.housekeepingbook.extension.removeSpecialCharacter
import com.queryholic.housekeepingbook.service.OcrService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.util.stream.Collectors
import java.util.stream.Collectors.joining

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
                        .map { it.onlyNumber() }
                        .collect(Collectors.toList())
            }
            InferenceTarget.MERCHANT -> {
                return listOf(ocrService.inferText(imageUrl, "")[0])
            }
            InferenceTarget.ITEMS -> {
                val rawText = ocrService.inferText(imageUrl, "|")
                val itemHeaderLineNumber: Int = rawText.indexOfFirst { it.contains("단가|수량|금액".toRegex()) }

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
        val itemStartIndex: Int = itemHeaderLineNumber + 1

        // 상품명이 001 로 시작하고, 해당 상품명 아랫줄에 단가 수량 금액 적힌 패턴일때
        if (rawText[itemHeaderLineNumber + 1].contains("001")) {
            return listOf(rawText[itemHeaderLineNumber]) +
                    rawText.asSequence()
                            .drop(itemStartIndex)
                            .mapIndexed { index, s -> index to s }
                            .filter { "[0-9]{3}".toRegex().matches(it.second.split("|")[0]) }
                            .map { it.first + itemStartIndex }
                            .map { itemIndex ->
                                val itemLine1 = rawText[itemIndex]
                                val itemLine2 = "|" +
                                        rawText[itemIndex + 1]
                                                .split("|")
                                                .stream()
                                                .skip(1) // 상품 코드 같아 보이는 column 은 스킵
                                                .map {
                                                    when {
                                                        it.matches(Regex("[\u3131-\u3163\uAC00-\uD7A3a-zA-Z]")) -> {
                                                            it
                                                        }
                                                        else -> {
                                                            it.removeSpecialCharacter()
                                                        }
                                                    }
                                                }
                                                .filter { it.isNotBlank() }
                                                .collect(joining("|"))
                                itemLine1 + itemLine2
                            }
        }

        // 상품명과 단가 수량 금액이 한 줄에 적힌 패턴
        return listOf(rawText[itemHeaderLineNumber]) +
                rawText.drop(itemStartIndex)
                        .takeWhile {
                            it.matches("^[\uAC00-\uD7A3]+.*[0-9]+$".toRegex())
                        }
                        .map { line ->
                            line.split("|")
                                    .stream()
                                    .map {
                                        when {
                                            // 품목명인 경우 그대로 출력, 금액 필드는 "," 와 같은 특수문자 제거
                                            it.contains(Regex("[\uAC00-\uD7A3]+|[a-zA-z]+")) -> {
                                                it
                                            }
                                            else -> {
                                                it.removeSpecialCharacter()
                                            }
                                        }
                                    }
                                    .collect(joining("|"))
                        }
    }
}