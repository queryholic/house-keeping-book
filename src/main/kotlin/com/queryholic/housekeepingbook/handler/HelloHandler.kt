package com.queryholic.housekeepingbook.handler

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class HelloHandler {

    suspend fun hello(request: ServerRequest): ServerResponse {
        return ServerResponse.ok()
                .bodyValueAndAwait("world")
    }
}