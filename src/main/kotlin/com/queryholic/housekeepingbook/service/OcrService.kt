package com.queryholic.housekeepingbook.service

import com.queryholic.housekeepingbook.spec.clova.request.Image
import com.queryholic.housekeepingbook.spec.clova.request.OcrRequest
import com.queryholic.housekeepingbook.spec.clova.response.Field
import com.queryholic.housekeepingbook.spec.clova.response.OcrResponse
import javafx.geometry.Rectangle2D
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.stream.Collectors

@Service
class OcrService(
        private val ocrWebClient: WebClient
) {

    suspend fun inferText(imageUrl: String, delimiter: String): List<String> {
        val rawFields = ocrWebClient.post()
                .bodyValue(OcrRequest(
                        requestId = "String", images = listOf(Image(url = imageUrl, name = "name"))
                ))
                .retrieve()
                .bodyToMono(OcrResponse::class.java)
                .map { it.images.last().fields }
                .doOnError { throw it }
                .awaitSingle()

        val rectangleAdded: List<Pair<Field, Rectangle2D>> = rawFields
                .map {
                    val boundingPoly = it.boundingPoly
                    val leftUpperCorner = boundingPoly.getLeftUpperCorner()
                    it to Rectangle2D(leftUpperCorner.x, leftUpperCorner.y, boundingPoly.getWidth(), boundingPoly.getHeight())
                }

        // 각 라인별 첫번째 노드를 찾는다.
        val imageWidth = 3000.0
        val firstNodes: List<Pair<Field, Rectangle2D>> = getFirstNodes(rectangleAdded, imageWidth)

        // 라인 별로 교차 하는 것들을 모두 찾는다 output: Map<Int, List<Field>>
        var lineNumber = 0
        val resultMap: MutableMap<Int, List<Field>> = mutableMapOf()

        firstNodes.forEach { line ->
            resultMap[lineNumber++] = rectangleAdded.stream()
                    .filter {
                        line.second.intersects(it.second)
                    }
                    .map { it.first }
                    .collect(Collectors.toList())
        }

        // 문자 인식 결과만 리턴
        return resultMap.entries.stream()
                .map { it ->
                    it.value.stream()
                            .map { it.inferText }
                            .collect(Collectors.joining(delimiter))
                }
                .collect(Collectors.toList())
    }

    fun getFirstNodes(refinedField: List<Pair<Field, Rectangle2D>>, imageWidth: Double): List<Pair<Field, Rectangle2D>> {
        return refinedField.stream()
                .filter {
                    val x = it.second.minX
                    val y = it.second.minY
                    val h = it.second.height
                    val leftLine = Rectangle2D(x - imageWidth - 1, (y + h / 2), imageWidth, 1.0)

                    refinedField.stream().noneMatch { node -> node.second.intersects(leftLine) }
                }
                .map {
                    val y = it.second.minY
                    val h = it.second.height
                    it.first to Rectangle2D(0.0, (y + h / 2), imageWidth, 1.0)
                }
                .collect(Collectors.toList())
    }
}
