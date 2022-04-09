package com.example.edvoraproject.models.rides

data class RidesResponse(
    val city: String,
    val date: String,
    val destination_station_code: Int,
    val id: Int,
    val map_url: String,
    val origin_station_code: Int,
    val state: String,
    val station_path: List<Int>
)