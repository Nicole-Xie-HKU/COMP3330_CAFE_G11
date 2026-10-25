package com.group11.cafe.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.group11.cafe.data.repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.login(
            email,
            password,
            onSuccess,
            onFailure
        )
    }
}