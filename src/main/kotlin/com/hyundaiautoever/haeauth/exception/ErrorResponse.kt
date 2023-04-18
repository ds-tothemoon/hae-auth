package com.hyundaiautoever.haeauth.exception

data class ErrorResponse(
    val code: Int,
    val message: String,
)