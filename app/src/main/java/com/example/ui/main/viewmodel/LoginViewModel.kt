package com.example.ui.main.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.main.jetpack.loginModel
import com.example.ui.main.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    val loginRepository = LoginRepository()
    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val selectedItem = mutableIntStateOf(0)
    val isDarkTheme = mutableStateOf(true)

    fun login(login: loginModel, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = loginRepository.GetUsers(login)
                if (response?.isSuccessful == true) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
