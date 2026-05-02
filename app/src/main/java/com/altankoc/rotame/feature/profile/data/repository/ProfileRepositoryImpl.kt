package com.altankoc.rotame.feature.profile.data.repository

import com.altankoc.rotame.core.network.safeApiCall
import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.profile.data.dto.UpdateUserRequest
import com.altankoc.rotame.feature.profile.data.mapper.toDomain
import com.altankoc.rotame.feature.profile.data.remote.UserApiService
import com.altankoc.rotame.feature.profile.domain.model.User
import com.altankoc.rotame.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService
) : ProfileRepository {

    override suspend fun getMe(): Resource<User> = safeApiCall {
        userApiService.getMe().toDomain()
    }

    override suspend fun updateMe(
        firstName: String?,
        lastName: String?,
        username: String?
    ): Resource<User> = safeApiCall {
        userApiService.updateMe(
            UpdateUserRequest(
                firstName = firstName,
                lastName = lastName,
                username = username
            )
        ).toDomain()
    }

    override suspend fun deleteMe(): Resource<Unit> = safeApiCall {
        userApiService.deleteMe()
    }
}