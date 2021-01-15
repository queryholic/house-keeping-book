package com.queryholic.housekeepingbook.handler

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.stream.Stream

@SpringBootTest
@AutoConfigureWebTestClient
class OcrHandlerTest(
        @Autowired val webTestClient: WebTestClient
) {

    companion object {
        @JvmStatic
        fun inputAndExpected(): Stream<Arguments> = Stream.of(
                Arguments.of("total-amount", "https://user-images.githubusercontent.com/10183131/103668956-1b464200-4fbb-11eb-9083-622ceccc4e4e.png", listOf("49220")),
                Arguments.of("total-amount", "https://user-images.githubusercontent.com/10183131/104035815-eaae1480-5215-11eb-9a00-253d3bc6c912.png", listOf("33760")),
                Arguments.of("total-amount", "https://user-images.githubusercontent.com/10183131/104323086-19cfc900-5529-11eb-89f9-5bd610207002.png", listOf("9000")),
                Arguments.of("items", "https://user-images.githubusercontent.com/10183131/104323086-19cfc900-5529-11eb-89f9-5bd610207002.png", listOf(
                        "NO.|상품명|단가|수량|금액",
                        "001|감자|2500|2500|1",
                        "002|솔잎란15구|2800|2800|1",
                        "003|풀무원소찌개두부|1500|1500|1",
                        "004|야채|2200|2200|1"
                )),
                Arguments.of("total-amount", "https://github.com/queryholic/images/raw/master/IMG_0023.png", "18000"),
                Arguments.of("items", "https://github.com/queryholic/images/raw/master/IMG_0023.png", listOf(
                        "메뉴명|수량|금액",
                        "양푼동태탕(2인분)|1|16000",
                        "배달팁|2000"
                ))
        )
    }

    @DisplayName("test image")
    @ParameterizedTest
    @MethodSource("inputAndExpected")
    fun `test image`(target: String, imageUrl: String, expected: List<String>) {
        webTestClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                            .path("/ocr/text/$target")
                            .queryParam("imageUrl", imageUrl)
                            .build()
                }
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.inferText").value<List<String>> {
                    Assertions.assertIterableEquals(expected, it)
                }
    }
}