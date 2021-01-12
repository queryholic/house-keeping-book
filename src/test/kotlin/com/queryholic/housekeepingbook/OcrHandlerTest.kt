package com.queryholic.housekeepingbook

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
                Arguments.of("https://user-images.githubusercontent.com/10183131/103668956-1b464200-4fbb-11eb-9083-622ceccc4e4e.png", "/합계:49,220"), Arguments.of("https://user-images.githubusercontent.com/10183131/104035815-eaae1480-5215-11eb-9a00-253d3bc6c912.png", "합계:33,760")
        )
    }

    @DisplayName("test image")
    @ParameterizedTest
    @MethodSource("inputAndExpected")
    fun `test image`(imageUrl: String, expected: String) {
        webTestClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                            .path("/ocr/text")
                            .queryParam("imageUrl", imageUrl)
                            .build()
                }
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.inferText").isEqualTo(expected)

    }
}