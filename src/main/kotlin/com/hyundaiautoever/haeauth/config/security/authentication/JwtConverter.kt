package com.hyundaiautoever.haeauth.config.security.authentication

import com.hyundaiautoever.haeauth.config.HttpExceptionFactory.badRequest
import com.hyundaiautoever.haeauth.domain.dto.SignInRequest
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.core.ResolvableType
import org.springframework.http.MediaType
import org.springframework.http.codec.json.AbstractJackson2Decoder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.validation.Validator
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtConverter(private val jacksonDecoder: AbstractJackson2Decoder,
                   private val validator: Validator
) : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> = mono {
        val signInRequest = getUsernameAndPassword(exchange!!) ?: throw badRequest()
        return@mono UsernamePasswordAuthenticationToken(signInRequest.email, signInRequest.password)
    }

    private suspend fun getUsernameAndPassword(exchange: ServerWebExchange): SignInRequest? {
        val dataBuffer = exchange.request.body
        val type = ResolvableType.forClass(SignInRequest::class.java)
        return jacksonDecoder
            .decodeToMono(dataBuffer, type, MediaType.APPLICATION_JSON, mapOf())
            .onErrorResume { Mono.empty<SignInRequest>() }
            .cast(SignInRequest::class.java)
            .awaitFirstOrNull()
    }
}
