package com.example.ui.main.interfaces

import com.example.ui.main.jetpack.loginModel
import com.example.ui.main.model.ProductList
import com.example.ui.main.model.UserDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface service {

    @POST("user/login")
    suspend fun logUser(
        @Body login: loginModel
    ): Response<Unit>

    @GET("products")
    suspend fun getProducts(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int
    ): Response<ProductList>

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id: Int
    ): Response<UserDetails>
}