package com.altankoc.rotame.feature.profile.data.mapper

import com.altankoc.rotame.feature.profile.data.dto.UserResponse
import com.altankoc.rotame.feature.profile.domain.model.User

fun UserResponse.toDomain() = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    username = username,
    email = email,
    role = role,
    authProvider = authProvider,
    createdAt = createdAt
)