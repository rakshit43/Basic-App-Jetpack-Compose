package com.example.ui.main.repository

import com.example.ui.main.interfaces.service
import com.example.ui.main.jetpack.loginModel
import com.example.ui.main.model.ProductList
import com.example.ui.main.network.RetrofitManager
import retrofit2.Response

class LoginRepository {

    private val callRetrofit = RetrofitManager.getRetrofit()?.create(service::class.java)



    suspend fun GetUsers(login: loginModel): Response<Unit>? {
        return callRetrofit?.logUser(login)
    }

    suspend fun getList(skip: Int, limit: Int): Response<ProductList>?
    {
        return callRetrofit?.getProducts(skip,limit)
    }
}