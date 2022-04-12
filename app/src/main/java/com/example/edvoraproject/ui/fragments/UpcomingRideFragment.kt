package com.example.edvoraproject.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.example.edvoraproject.R
import com.example.edvoraproject.databinding.ActivityMainBinding
import com.example.edvoraproject.databinding.FragmentPastRideBinding
import com.example.edvoraproject.databinding.FragmentUpcomingRideBinding
import com.example.edvoraproject.models.ride.RideResponse
import com.example.edvoraproject.ui.MainActivity
import com.example.edvoraproject.ui.MainViewModel
import com.example.edvoraproject.ui.adapter.RidesAdapter

class UpcomingRideFragment : Fragment() {

    private lateinit var binding: FragmentUpcomingRideBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var ridesAdapter: RidesAdapter
    private lateinit var bindingMainActivity: ActivityMainBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpcomingRideBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).viewModel
        bindingMainActivity = (activity as MainActivity).binding
        return (binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.upcomingRides.postValue(viewModel.getUpcomingRides())
        bindingMainActivity.stateFilter.onItemClickListener = object : AdapterView.OnItemClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val state = adapter?.getItemAtPosition(position).toString()
                if(adapter?.getItemAtPosition(position).toString() == "None"){
                    viewModel.upcomingRides.postValue(viewModel.getUpcomingRides())
                }
                else{
                    val upcomingRides = viewModel.sortByState(viewModel.sortWithDistance(viewModel.getUpcomingRides()),state)
                    viewModel.upcomingRides.postValue(upcomingRides)
                }
            }
        }

        bindingMainActivity.cityFilter.onItemClickListener = object : AdapterView.OnItemClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val city = adapter?.getItemAtPosition(position).toString()
                if(city == "None"){
                    viewModel.upcomingRides.postValue(viewModel.sortWithDistance(viewModel.getUpcomingRides()))
                }
                else{
                    val rides = viewModel.sortByCity(viewModel.sortWithDistance(viewModel.getUpcomingRides()),city)
                    viewModel.upcomingRides.postValue(rides)
                }
            }
        }
        viewModel.upcomingRides.observe(viewLifecycleOwner, Observer { res ->
            setUpRecyclerView(res)
        })
    }

    fun setUpRecyclerView(rideResponse: RideResponse) {
        ridesAdapter = RidesAdapter(rideResponse, viewModel.user.value!!.data, requireContext())
        binding.ridesRecycler.adapter = ridesAdapter
    }

    override fun onResume() {
        super.onResume()

        if (!viewModel.upcomingRides.value.isNullOrEmpty()) {
            setUpRecyclerView(viewModel.upcomingRides.value!!)
        }
    }
}