package com.altankoc.rotame.feature.profile.data.remote

import com.altankoc.rotame.feature.profile.data.dto.UpdateUserRequest
import com.altankoc.rotame.feature.profile.data.dto.UserResponse
import retrofit2.http.*

interface UserApiService {

    @GET("api/v1/users/me")
    suspend fun getMe(): UserResponse

    @PATCH("api/v1/users/me")
    suspend fun updateMe(@Body request: UpdateUserRequest): UserResponse

    @DELETE("api/v1/users/me")
    suspend fun deleteMe()
}