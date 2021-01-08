package com.queryholic.housekeepingbook

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
class OcrHandlerTest(
        @Autowired val webTestClient: WebTestClient
) {

    @Test
    fun `test image`() {
        webTestClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                            .path("/ocr/text")
                            .queryParam("imageUrl", "https://user-images.githubusercontent.com/10183131/103668956-1b464200-4fbb-11eb-9083-622ceccc4e4e.png")
                            .build()
                }
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.inferText").isEqualTo("/합계:49,220")

    }
}