package com.queryholic.housekeepingbook

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.ApplicationPidFileWriter

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    SpringApplicationBuilder()
            .sources(Application::class.java)
            .listeners(ApplicationPidFileWriter("api.pid"))
            .build()
            .run(*args)
}