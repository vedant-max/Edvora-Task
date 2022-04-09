package com.example.edvoraproject.api

import com.example.edvoraproject.models.rides.RidesResponse
import com.example.edvoraproject.models.users.UsersResponse
import retrofit2.Response
import retrofit2.http.GET

interface Api {

    @GET("rides")
    suspend fun getRides(): Response<RidesResponse>

    @GET("user")
    suspend fun getUsers(): Response<UsersResponse>
}