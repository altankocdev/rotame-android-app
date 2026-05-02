package com.altankoc.rotame.feature.auth.domain.usecase

import com.altankoc.rotame.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String
    ) = authRepository.register(firstName, lastName, username, email, password)
}