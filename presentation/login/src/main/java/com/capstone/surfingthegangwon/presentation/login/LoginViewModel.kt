package com.capstone.surfingthegangwon.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.surfingthegangwon.domain.login.model.TokenPair
import com.capstone.surfingthegangwon.domain.login.usecase.AuthUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val token: TokenPair) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun handleKakaoRedirectCode(code: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
//                val token = authUsecase(code)
//                _uiState.value = LoginUiState.Success(token)
                // TODO: 여기서 token 저장(DataStore 등)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "로그인 실패")
            }
        }
    }
}