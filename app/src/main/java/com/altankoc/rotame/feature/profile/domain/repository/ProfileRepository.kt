package com.altankoc.rotame.feature.profile.domain.repository

import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.profile.domain.model.User

interface ProfileRepository {
    suspend fun getMe(): Resource<User>
    suspend fun updateMe(firstName: String?, lastName: String?, username: String?): Resource<User>
    suspend fun deleteMe(): Resource<Unit>
}