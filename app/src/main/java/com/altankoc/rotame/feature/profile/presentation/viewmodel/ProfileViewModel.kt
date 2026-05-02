package com.altankoc.rotame.feature.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.rotame.core.datastore.TokenDataStore
import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.auth.domain.usecase.LogoutUseCase
import com.altankoc.rotame.feature.profile.domain.usecase.DeleteMeUseCase
import com.altankoc.rotame.feature.profile.domain.usecase.GetMeUseCase
import com.altankoc.rotame.feature.profile.domain.usecase.UpdateMeUseCase
import com.altankoc.rotame.feature.profile.presentation.state.ProfileActionUiState
import com.altankoc.rotame.feature.profile.presentation.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMeUseCase: GetMeUseCase,
    private val updateMeUseCase: UpdateMeUseCase,
    private val deleteMeUseCase: DeleteMeUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<ProfileActionUiState>(ProfileActionUiState.Idle)
    val actionState: StateFlow<ProfileActionUiState> = _actionState.asStateFlow()

    init {
        getMe()
    }

    fun getMe() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            _uiState.value = when (val result = getMeUseCase()) {
                is Resource.Success -> ProfileUiState.Success(result.data)
                is Resource.Error -> ProfileUiState.Error(result.message)
                else -> ProfileUiState.Idle
            }
        }
    }

    fun updateMe(firstName: String?, lastName: String?, username: String?) {
        viewModelScope.launch {
            _actionState.value = ProfileActionUiState.Loading
            _actionState.value = when (val result = updateMeUseCase(firstName, lastName, username)) {
                is Resource.Success -> {
                    getMe()
                    ProfileActionUiState.Success
                }
                is Resource.Error -> ProfileActionUiState.Error(result.message)
                else -> ProfileActionUiState.Idle
            }
        }
    }

    fun deleteMe() {
        viewModelScope.launch {
            _actionState.value = ProfileActionUiState.Loading
            _actionState.value = when (val result = deleteMeUseCase()) {
                is Resource.Success -> {
                    tokenDataStore.clear()
                    ProfileActionUiState.Success
                }
                is Resource.Error -> ProfileActionUiState.Error(result.message)
                else -> ProfileActionUiState.Idle
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val refreshToken = tokenDataStore.getRefreshTokenOnce() ?: return@launch
            logoutUseCase(refreshToken)
            tokenDataStore.clear()
            _actionState.value = ProfileActionUiState.Success
        }
    }

    fun resetActionState() {
        _actionState.value = ProfileActionUiState.Idle
    }
}