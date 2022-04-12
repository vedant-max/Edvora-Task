package com.example.edvoraproject.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.edvoraproject.databinding.ActivityMainBinding
import com.example.edvoraproject.databinding.FragmentNearestRideBinding
import com.example.edvoraproject.models.ride.RideResponse
import com.example.edvoraproject.ui.MainActivity
import com.example.edvoraproject.ui.MainViewModel
import com.example.edvoraproject.ui.adapter.RidesAdapter

class NearestRideFragment : Fragment() {

    private lateinit var binding: FragmentNearestRideBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var ridesAdapter: RidesAdapter
    private lateinit var bindingMainActivity: ActivityMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNearestRideBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).viewModel
        bindingMainActivity = (activity as MainActivity).binding
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingMainActivity.stateFilter.onItemClickListener = object : AdapterView.OnItemClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val state = adapter?.getItemAtPosition(position).toString()
                if(state == "None"){
                    viewModel.nearestRides.postValue(viewModel.sortWithDistance(viewModel.rides.value?.data!!))
                }
                else{
                    val rides = viewModel.sortByState(viewModel.sortWithDistance(viewModel.rides.value?.data!!),state)
                    viewModel.nearestRides.postValue(rides)
                }
            }
        }

        bindingMainActivity.cityFilter.onItemClickListener = object : AdapterView.OnItemClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val city = adapter?.getItemAtPosition(position).toString()
                if(city == "None"){
                    viewModel.nearestRides.postValue(viewModel.sortWithDistance(viewModel.rides.value?.data!!))
                }
                else{
                    val rides = viewModel.sortByCity(viewModel.sortWithDistance(viewModel.rides.value?.data!!),city)
                    viewModel.nearestRides.postValue(rides)
                }
            }
        }

        viewModel.nearestRides.observe(viewLifecycleOwner) { res ->
            setupRecycler(res)
        }
    }

    private fun setupRecycler(ridesResponse: RideResponse) {
        ridesAdapter = RidesAdapter(ridesResponse, viewModel.user.value!!.data, requireContext())
        binding.ridesRecycler.adapter = ridesAdapter
    }

    override fun onResume() {
        super.onResume()

        if (!viewModel.nearestRides.value.isNullOrEmpty()) {
            setupRecycler(viewModel.nearestRides.value!!)
        }

    }

}