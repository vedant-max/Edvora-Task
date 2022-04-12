package com.example.edvoraproject.models.ride

data class Ride(
    val city: String,
    val date: String,
    val destination_station_code: Int,
    val id: Int,
    val map_url: String,
    val origin_station_code: Int,
    val state: String,
    var station_path: List<Int>
)