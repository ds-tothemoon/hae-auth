package com.hyundaiautoever.haeauth.config.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(private val jwtProperties: JwtProperties) {
    fun accessToken(username: String, roles: Array<String>): String {
        return generate(username, jwtProperties.expiresTime, roles, jwtProperties.secret)
    }

    fun decodeAccessToken(accessToken: String): DecodedJWT {
        return decode(jwtProperties.secret, accessToken)
    }

    fun refreshToken(username: String, roles: Array<String>): String {
        return generate(username, jwtProperties.refreshExpiresTime, roles, jwtProperties.refresh)
    }

    fun decodeRefreshToken(refreshToken: String): DecodedJWT {
        return decode(jwtProperties.refresh, refreshToken)
    }

    fun getRoles(decodedJWT: DecodedJWT) = decodedJWT.getClaim("role").asList(String::class.java)
        .map { SimpleGrantedAuthority(it) }

    private fun generate(username: String, expirationInMillis: Long, roles: Array<String>, signature: String): String {
        return JWT.create()
            .withIssuer(jwtProperties.issuer)
            .withSubject(jwtProperties.subject)
            .withClaim("username", username)
            .withArrayClaim("role", roles)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationInMillis * 1000))
            .sign(Algorithm.HMAC512(signature.toByteArray()))
    }

    private fun decode(signature: String, token: String): DecodedJWT {
        return JWT.require(Algorithm.HMAC512(signature.toByteArray()))
            .build()
            .verify(token.replace("Bearer ", ""))
    }
}