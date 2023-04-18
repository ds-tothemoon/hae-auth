package com.hyundaiautoever.haeauth.presentation

import com.hyundaiautoever.haeauth.application.AuthService
import com.hyundaiautoever.haeauth.domain.dto.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signup")
    suspend fun signUp(@RequestBody request: SignUpRequest) {
        authService.signUp(request)
    }

    @PostMapping("/signin")
    suspend fun signIn(@RequestBody signInRequest: SignInRequest): SignInResponse {
        return authService.signIn(signInRequest)
    }

    @GetMapping("/me")
    suspend fun get(
        @AuthToken token: String
    ): MeResponse {
        return MeResponse(authService.getByToken(token))
    }
}