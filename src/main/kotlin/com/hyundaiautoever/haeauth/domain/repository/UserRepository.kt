package com.hyundaiautoever.haeauth.domain.repository

import com.hyundaiautoever.haeauth.domain.entity.AppUser
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<AppUser, Long> {
    suspend fun findByEmail(email: String): AppUser?
}
