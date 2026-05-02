package com.altankoc.rotame.feature.auth.domain.usecase

import com.altankoc.rotame.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(refreshToken: String) =
        authRepository.logout(refreshToken)
}