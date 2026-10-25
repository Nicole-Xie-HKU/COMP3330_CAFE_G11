package com.group11.cafe.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.group11.cafe.data.repository.AuthRepository

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        Log.d("REGISTER", "ViewModel reached")

        repository.register(
            name,
            email,
            password,
            role,
            onSuccess,
            onFailure
        )
    }
}