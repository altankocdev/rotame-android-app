package com.altankoc.rotame.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.auth.domain.usecase.LoginUseCase
import com.altankoc.rotame.feature.auth.presentation.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(identifier: String, password: String) {
        if (identifier.isBlank()) {
            _uiState.value = LoginUiState.Error("E-posta veya kullanıcı adı boş bırakılamaz")
            return
        }
        if (password.isBlank()) {
            _uiState.value = LoginUiState.Error("Şifre boş bırakılamaz")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            _uiState.value = when (val result = loginUseCase(identifier, password)) {
                is Resource.Success -> LoginUiState.Success
                is Resource.Error -> LoginUiState.Error(result.message)
                else -> LoginUiState.Idle
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}