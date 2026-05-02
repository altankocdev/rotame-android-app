package com.altankoc.rotame.feature.auth.data.repository

import com.altankoc.rotame.core.network.safeApiCall
import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.auth.data.dto.LoginRequest
import com.altankoc.rotame.feature.auth.data.dto.RegisterRequest
import com.altankoc.rotame.feature.auth.data.mapper.toDomain
import com.altankoc.rotame.feature.auth.data.remote.AuthApiService
import com.altankoc.rotame.feature.auth.domain.model.AuthUser
import com.altankoc.rotame.feature.auth.domain.repository.AuthRepository
import com.altankoc.rotame.core.datastore.TokenDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String
    ): Resource<AuthUser> {
        val result = safeApiCall {
            authApiService.register(
                RegisterRequest(
                    firstName = firstName,
                    lastName = lastName,
                    username = username,
                    email = email,
                    password = password
                )
            )
        }
        if (result is Resource.Success) {
            return Resource.Success(result.data.toDomain())
        }
        return result as Resource.Error
    }

    override suspend fun login(identifier: String, password: String): Resource<AuthUser> {
        val result = safeApiCall {
            authApiService.login(LoginRequest(identifier = identifier, password = password))
        }
        if (result is Resource.Success) {
            tokenDataStore.saveAuthData(
                accessToken = result.data.accessToken,
                refreshToken = result.data.refreshToken,
                userId = result.data.id,
                email = result.data.email,
                firstName = result.data.firstName,
                lastName = result.data.lastName,
                username = result.data.username
            )
            return Resource.Success(result.data.toDomain())
        }
        return result as Resource.Error
    }

    override suspend fun logout(refreshToken: String): Resource<Unit> {
        val result = safeApiCall { authApiService.logout(refreshToken) }
        if (result is Resource.Success) {
            tokenDataStore.clear()
        }
        return result
    }
}