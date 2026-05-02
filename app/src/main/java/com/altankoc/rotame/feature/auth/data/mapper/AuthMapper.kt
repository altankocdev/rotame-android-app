package com.altankoc.rotame.feature.auth.data.mapper

import com.altankoc.rotame.feature.auth.data.dto.AuthResponse
import com.altankoc.rotame.feature.auth.domain.model.AuthUser

fun AuthResponse.toDomain() = AuthUser(
    id = id,
    firstName = firstName,
    lastName = lastName,
    username = username,
    email = email,
    role = role,
    accessToken = accessToken,
    refreshToken = refreshToken
)