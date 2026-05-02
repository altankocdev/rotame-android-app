package com.altankoc.rotame.feature.profile.presentation.state

import com.altankoc.rotame.feature.profile.domain.model.User

sealed class ProfileUiState {
    data object Idle : ProfileUiState()
    data object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}