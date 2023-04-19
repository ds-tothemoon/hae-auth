package com.hyundaiautoever.haeauth.config.security.authentication

import com.hyundaiautoever.haeauth.domain.repository.UserRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomReactiveUserDetailsService(private val userRepository: UserRepository) :
    ReactiveUserDetailsService {

    override fun findByUsername(email: String?): Mono<UserDetails> = mono {
        val appUser = userRepository.findByEmail(email!!) ?: throw BadCredentialsException("Invalid Credentials")
        return@mono User(appUser.email, appUser.password, listOf())
    }
}
