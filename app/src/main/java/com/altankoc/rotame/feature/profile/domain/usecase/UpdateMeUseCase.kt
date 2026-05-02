package com.altankoc.rotame.feature.profile.domain.usecase

import com.altankoc.rotame.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateMeUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(firstName: String?, lastName: String?, username: String?) =
        profileRepository.updateMe(firstName, lastName, username)
}