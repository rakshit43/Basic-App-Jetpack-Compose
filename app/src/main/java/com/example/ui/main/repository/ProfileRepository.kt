package com.example.ui.main.repository

import com.example.ui.main.interfaces.service
import com.example.ui.main.model.UserDetails
import com.example.ui.main.network.RetrofitManager
import retrofit2.Response
import kotlin.jvm.java

class ProfileRepository {

    private val services = RetrofitManager.getRetrofit()?.create(service::class.java)


    suspend fun getUserDetails(int: Int): Response<UserDetails>?
    {
        return services?.getUser(int)
    }
}