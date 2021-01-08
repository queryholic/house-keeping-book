package com.queryholic.housekeepingbook.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig(
        @Value("\${webclient.connectTimeoutMillis:3000}") private val defaultConnectTimeoutMillis: Int
        , @Value("\${webclient.readTimeoutMillis:10000}") private val defaultReadTimeoutMillis: Int
) {

    companion object {
        private const val OCR_SECRET_KEY_HEADER = "X-OCR-SECRET"
    }

    @Bean
    fun ocrWebClient(
            @Value("\${clova.ocr.url}") ocrUrl: String
            , @Value("\${clova.ocr.secretKey}") secretKey: String
    ): WebClient {

        val httpClient = HttpClient.create()
                .tcpConfiguration {
                    it.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, defaultConnectTimeoutMillis)
                    it.doOnConnected { conn ->
                        conn.addHandlerLast(ReadTimeoutHandler(defaultReadTimeoutMillis))
                    }
                }
        return WebClient.builder()
                .baseUrl(ocrUrl)
                .defaultHeaders { httpHeaders ->
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    httpHeaders.add(OCR_SECRET_KEY_HEADER, secretKey)
                }
                .clientConnector(ReactorClientHttpConnector(httpClient))
                .build()
    }
}