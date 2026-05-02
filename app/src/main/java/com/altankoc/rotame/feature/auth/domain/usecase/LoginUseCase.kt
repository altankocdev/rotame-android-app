package com.altankoc.rotame.feature.auth.domain.usecase

import com.altankoc.rotame.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(identifier: String, password: String) =
        authRepository.login(identifier, password)
}