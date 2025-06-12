package com.ycompany.ui.events

sealed class SignInState {
    data object Idle : SignInState()
    data object Loading : SignInState()
    data class Success(val message: String? = null) : SignInState()
    data class Error(val message: String) : SignInState()
    data object Proceed : SignInState()
}