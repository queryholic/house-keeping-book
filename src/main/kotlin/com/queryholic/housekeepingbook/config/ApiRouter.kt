package com.queryholic.housekeepingbook.config

import com.queryholic.housekeepingbook.handler.HelloHandler
import com.queryholic.housekeepingbook.handler.OcrHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ApiRouter(
        private val ocrHandler: OcrHandler
        , private val helloHandler: HelloHandler
) {

    @Bean
    fun ocrRouter() = coRouter {
        GET("/ocr/text", ocrHandler::inferText)
    }

    @Bean
    fun helloRouter() = coRouter {
        GET("/hello", helloHandler::hello)
    }

}