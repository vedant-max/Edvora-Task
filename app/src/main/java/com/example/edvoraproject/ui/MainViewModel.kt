package com.example.edvoraproject.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edvoraproject.AppApplication
import com.example.edvoraproject.models.ride.Ride
import com.example.edvoraproject.models.ride.RideResponse
import com.example.edvoraproject.models.user.User
import com.example.edvoraproject.repository.Repository
import com.example.edvoraproject.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainViewModel(app: Application,private val repository: Repository): AndroidViewModel(app) {
    val rides : MutableLiveData<Resource<RideResponse>> = MutableLiveData()
    val nearestRides : MutableLiveData<RideResponse> = MutableLiveData()
    val pastRides : MutableLiveData<RideResponse> = MutableLiveData()
    val upcomingRides : MutableLiveData<RideResponse> = MutableLiveData()

    fun getRides() = viewModelScope.launch {
        rides.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = repository.getRides()
                rides.postValue(handleRidesResponse(response!!))
            }
            else{
                rides.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> rides.postValue(Resource.Error("Network Failure"))
                else -> rides.postValue(Resource.Error("Conversion Error"))
            }
        }

    }

    private fun handleRidesResponse(response: Response<RideResponse>): Resource<RideResponse>{
        if(response.isSuccessful){
            response.body()?.let {resultResponse ->
                for (ride in resultResponse){
                    ride.station_path = listOf(ride.origin_station_code) + ride.station_path + listOf(ride.destination_station_code)
                }
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    val user: MutableLiveData<Resource<User>> = MutableLiveData()
    fun getUser() = viewModelScope.launch {
        user.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = repository.getUser()
                user.postValue(handleUserResponse(response!!))
            }
            else{
                user.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> user.postValue(Resource.Error("Network Failure"))
                else -> user.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleUserResponse(response: Response<User>): Resource<User>{
        if(response.isSuccessful){
            response.body()?.let {resultResponse ->
                return  Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun sortByCity(rides: RideResponse,city: String): RideResponse {
//        val rides: RideResponse = rides.value?.data!!
        var temp = RideResponse()
        for(ride in rides){
            if(ride.city==city){
                temp.add(ride)
            }
        }
        return temp
    }

    fun sortByState(rides: RideResponse,state: String): RideResponse {
//        val rides: RideResponse = rides.value?.data!!
        var stateSpecific = RideResponse()
//        stateSpecific = sortWithDistance(rides)
        for(ride in rides){
            if(ride.state==state){
                stateSpecific.add(ride)
            }
        }
        return stateSpecific
    }

    fun getAllCities(rides: RideResponse): List<String>{
        var list: List<String> = ArrayList()
        for(ride in rides){
            list += listOf(ride.city)
        }
        return list
    }

    fun getAllStates(rides: RideResponse): List<String>{
        var list: List<String> = ArrayList()
        for(ride in rides){
            list += listOf(ride.state)
        }
        return list
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getUpcomingRides(): RideResponse {
        val allRides: RideResponse = sortWithDistance(rides.value?.data!!)
        val _upcomingRides = RideResponse()
        val sdf = SimpleDateFormat("M/dd/yyyy")
        val currentDate = sdf.format(Date())
        val formatter = DateTimeFormatter.ofPattern("M/dd/yyyy")
        for(ride in allRides){
            val rideDate: String = ride.date.subSequence(0,10).toString()
            val date = LocalDate.parse(rideDate, formatter)
            val today = LocalDate.parse(currentDate, formatter)
            if(!date.isBefore(today)){
                _upcomingRides.add(ride)
            }
        }
        return _upcomingRides
    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getPastRides(): RideResponse {
        val allRides: RideResponse = sortWithDistance(rides.value?.data!!)
        val _pastRides = RideResponse()
        val sdf = SimpleDateFormat("M/dd/yyyy")
        val formatter = DateTimeFormatter.ofPattern("M/dd/yyyy")
        val currentDate = sdf.format(Date())
        for(ride in allRides){
            val rideDate : String = ride.date.subSequence(0,10).toString()
            val date = LocalDate.parse(rideDate,formatter)
            val today = LocalDate.parse(currentDate,formatter)
            if(date.isBefore(today)){
                _pastRides.add(ride)
            }
        }
        return _pastRides
    }

    private fun getDistance(station_path: List<Int>, stationId: Int) : Int{
        var value: Int = Int.MAX_VALUE
        for(station in station_path){
            value = kotlin.math.min(value, kotlin.math.abs(station - stationId))
        }
        return value
    }
    fun sortWithDistance(rides : RideResponse): RideResponse {
        val sortedRides = RideResponse()
        val userStationCode = user.value!!.data!!.station_code
        val list = rides.sortedWith(Comparator<Ride>{a,b ->
            when{
                getDistance(a.station_path, userStationCode) < getDistance(b.station_path,userStationCode) -> 1
                getDistance(a.station_path,userStationCode) > getDistance(b.station_path,userStationCode) -> -1
                else -> 0
            }
        })
        for(ride in list.reversed()){
            sortedRides.add(ride)
        }
        return sortedRides
    }

    private fun  hasInternetConnection() : Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }






}