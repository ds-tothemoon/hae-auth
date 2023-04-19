package com.hyundaiautoever.haeauth.application

import com.hyundaiautoever.haeauth.config.security.JwtService
import com.hyundaiautoever.haeauth.domain.dto.SignInRequest
import com.hyundaiautoever.haeauth.domain.dto.SignInResponse
import com.hyundaiautoever.haeauth.domain.dto.SignUpRequest
import com.hyundaiautoever.haeauth.domain.entity.AppUser
import com.hyundaiautoever.haeauth.domain.entity.vo.Role
import com.hyundaiautoever.haeauth.domain.repository.UserRepository
import com.hyundaiautoever.haeauth.exception.PasswordNotMatchedException
import com.hyundaiautoever.haeauth.exception.UserExistsException
import com.hyundaiautoever.haeauth.exception.UserNotFoundException
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    suspend fun signUp(signUpRequest: SignUpRequest) {
        with(signUpRequest) {
            userRepository.findByEmail(email)?.let {
                throw UserExistsException()
            }
            val user = AppUser(
                email = email,
                password = passwordEncoder.encode(password),
                role = Role.USER.name,
                username = username,
            )
            userRepository.save(user)
        }
    }

    suspend fun signIn(signInRequest: SignInRequest): SignInResponse {
        return with(userRepository.findByEmail(signInRequest.email) ?: throw UserNotFoundException()) {
            val verified = passwordEncoder.matches(signInRequest.password, password)
            if (!verified) {
                throw PasswordNotMatchedException()
            }

            val token =  jwtService.accessToken(email, arrayOf(role))

            SignInResponse(
                email = email,
                username = username,
                token = token
            )
        }
    }

    suspend fun getCurrentUserInfo(): AppUser? {
        val authentication = ReactiveSecurityContextHolder.getContext()
            .mapNotNull { context -> context.authentication as? UsernamePasswordAuthenticationToken }
            .awaitSingleOrNull()

        return authentication?.let { getUser(it.name) }
    }

    suspend fun getUser(email: String): AppUser = userRepository.findByEmail(email) ?: throw UserNotFoundException()

}