package com.hyundaiautoever.haeauth.application

import com.hyundaiautoever.haeauth.config.JwtProperties
import com.hyundaiautoever.haeauth.domain.dto.SignInRequest
import com.hyundaiautoever.haeauth.domain.dto.SignInResponse
import com.hyundaiautoever.haeauth.domain.dto.SignUpRequest
import com.hyundaiautoever.haeauth.domain.entity.User
import com.hyundaiautoever.haeauth.domain.repository.UserRepository
import com.hyundaiautoever.haeauth.exception.InvalidJwtTokenException
import com.hyundaiautoever.haeauth.exception.PasswordNotMatchedException
import com.hyundaiautoever.haeauth.exception.UserExistsException
import com.hyundaiautoever.haeauth.exception.UserNotFoundException
import com.hyundaiautoever.haeauth.util.BCryptUtils
import com.hyundaiautoever.haeauth.util.JwtClaim
import com.hyundaiautoever.haeauth.util.JwtUtils
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtProperties: JwtProperties,
) {
    suspend fun signUp(signUpRequest: SignUpRequest) {
        with(signUpRequest) {
            userRepository.findByEmail(email)?.let {
                throw UserExistsException()
            }
            val user = User(
                email = email,
                password = BCryptUtils.hash(password),
                username = username,
            )
            userRepository.save(user)
        }
    }

    suspend fun signIn(signInRequest: SignInRequest): SignInResponse {
        return with(userRepository.findByEmail(signInRequest.email) ?: throw UserNotFoundException()) {
            val verified = BCryptUtils.verify(signInRequest.password, password)
            if (!verified) {
                throw PasswordNotMatchedException()
            }

            val jwtClaim = JwtClaim(
                userId = id!!,
                email = email,
                profileUrl = profileUrl,
                username = username
            )

            val token = JwtUtils.createToken(jwtClaim, jwtProperties)

            SignInResponse(
                email = email,
                username = username,
                token = token
            )
        }
    }

    suspend fun getByToken(token: String): User {
            // 캐시가 유효하지 않은 경우 동작
            val decodedJWT = JwtUtils.decode(token, jwtProperties.secret, jwtProperties.issuer)
            val userId = decodedJWT.claims["userId"]?.asLong() ?: throw InvalidJwtTokenException()
            return getUser(userId)
    }

    suspend fun getUser(userId: Long): User {
        return userRepository.findById(userId) ?: throw UserNotFoundException()
    }

}