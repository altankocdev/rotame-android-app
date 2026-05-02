package com.altankoc.rotame.feature.auth.domain.repository

import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.auth.domain.model.AuthUser

interface AuthRepository {
    suspend fun register(firstName: String, lastName: String, username: String, email: String, password: String): Resource<AuthUser>
    suspend fun login(identifier: String, password: String): Resource<AuthUser>
    suspend fun logout(refreshToken: String): Resource<Unit>
}