package com.group11.cafe.viewmodel

import androidx.lifecycle.ViewModel
import com.group11.cafe.data.repository.UserRepository

class DashboardViewModel : ViewModel() {

    private val repository =
        UserRepository()

    fun loadRole(
        onResult: (String) -> Unit
    ) {

        repository.getCurrentUserRole(
            onResult
        )
    }
}