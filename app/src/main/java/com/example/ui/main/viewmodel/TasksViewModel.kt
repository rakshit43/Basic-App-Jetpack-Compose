package com.example.ui.main.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.main.model.Product
import com.example.ui.main.repository.LoginRepository
import kotlinx.coroutines.launch

class TasksViewModel : ViewModel(){

    val loginRepository = LoginRepository()
    val selectedIndex = mutableStateOf(0)
    val productList = mutableStateOf<List<Product>>(emptyList())

    init {
        fetchProducts(0, 10)
    }

    fun fetchProducts(skip: Int = 0, limit: Int = 10)
    {
        viewModelScope.launch {
            try{
                val response = loginRepository.getList(skip, limit)
                if (response?.isSuccessful == true) {
                    productList.value = response.body()?.products ?: emptyList()
                }
            }
            catch(e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
}