package com.altankoc.rotame.feature.profile.domain.usecase

import com.altankoc.rotame.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke() = profileRepository.getMe()
}