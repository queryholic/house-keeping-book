package com.queryholic.housekeepingbook.data

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response<T : Any?>(
        val code: String
        , val message: String
        , val result: T? = null
)