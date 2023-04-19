package com.hyundaiautoever.haeauth.config.security.authentication

import com.hyundaiautoever.haeauth.config.HttpExceptionFactory.unauthorized
import com.hyundaiautoever.haeauth.config.security.JwtService
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtServerAuthenticationSuccessHandler(private val jwtService: JwtService) :
    ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(webFilterExchange: WebFilterExchange?, authentication: Authentication?):
        Mono<Void> = mono {
            val principal = authentication?.principal ?: throw unauthorized()

            when (principal) {
                is User -> {
                    val roles = principal.authorities.map { it.authority }.toTypedArray()
                    val accessToken = jwtService.accessToken(principal.username, roles)
                    val refreshToken = jwtService.refreshToken(principal.username, roles)
                    webFilterExchange?.exchange?.response?.headers?.set("Authorization", accessToken)
                    webFilterExchange?.exchange?.response?.headers?.set("Refresh-Token", refreshToken)
                }
            }

            return@mono null
        }
}
