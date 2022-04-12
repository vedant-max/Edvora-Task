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
import com.example.edvoraproject.databinding.ActivityMainBinding.bind
import com.example.edvoraproject.databinding.ActivityMainBinding.inflate
import com.example.edvoraproject.databinding.FragmentNearestRideBinding
import com.example.edvoraproject.databinding.FragmentNearestRideBinding.inflate
import com.example.edvoraproject.databinding.FragmentPastRideBinding
import com.example.edvoraproject.models.ride.RideResponse
import com.example.edvoraproject.ui.MainActivity
import com.example.edvoraproject.ui.MainViewModel
import com.example.edvoraproject.ui.adapter.RidesAdapter

class PastFragment : Fragment() {

    private lateinit var binding: FragmentPastRideBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var ridesAdapter: RidesAdapter
    private lateinit var bindingMainActivity: ActivityMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPastRideBinding.inflate(inflater,container,false)
        viewModel = (activity as MainActivity).viewModel
        bindingMainActivity = (activity as MainActivity).binding
        return (binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.pastRides.postValue(viewModel.getPastRides())
        bindingMainActivity.stateFilter.onItemClickListener = object : AdapterView.OnItemClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val state = adapter?.getItemAtPosition(position).toString()
                if(adapter?.getItemAtPosition(position).toString() == "None"){
                    viewModel.pastRides.postValue(viewModel.getPastRides())
                }
                else{
                    val pastRides = viewModel.sortByState(viewModel.sortWithDistance(viewModel.rides.value?.data!!),state)
                    viewModel.pastRides.postValue(pastRides)
                }
            }
        }

        bindingMainActivity.cityFilter.onItemClickListener = object : AdapterView.OnItemClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val city = adapter?.getItemAtPosition(position).toString()
                if(city == "None"){
                    viewModel.pastRides.postValue(viewModel.sortWithDistance(viewModel.getPastRides()))
                }
                else{
                    val rides = viewModel.sortByCity(viewModel.sortWithDistance(viewModel.getPastRides()),city)
                    viewModel.pastRides.postValue(rides)
                }
            }
        }

        viewModel.pastRides.observe(viewLifecycleOwner, Observer { res ->
            setUpRecycler(res)
        })
    }

    private fun setUpRecycler(ridesResponse: RideResponse){
        ridesAdapter = RidesAdapter(ridesResponse, viewModel.user.value!!.data, requireContext())
        binding.ridesRecycler.adapter = ridesAdapter
    }

    override fun onResume() {
        super.onResume()

        if (!viewModel.pastRides.value.isNullOrEmpty()) {
            setUpRecycler(viewModel.pastRides.value!!)
        }
    }
}