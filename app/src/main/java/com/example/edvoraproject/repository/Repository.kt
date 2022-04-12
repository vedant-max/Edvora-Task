package com.example.edvoraproject.repository

import com.example.edvoraproject.api.RetrofitInstance

class Repository {
    suspend fun getRides() = RetrofitInstance.api.getRides()

    suspend fun getUser() = RetrofitInstance.api.getUser()

}