package com.example.edvoraproject.api

import com.example.edvoraproject.models.ride.RideResponse
import com.example.edvoraproject.models.user.User
import retrofit2.Response
import retrofit2.http.GET

interface Api {

    @GET("rides")
    suspend fun getRides(): Response<RideResponse>

    @GET("user")
    suspend fun getUsers(): Response<User>
}