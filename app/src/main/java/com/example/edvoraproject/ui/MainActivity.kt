package com.example.edvoraproject.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.edvoraproject.R
import com.example.edvoraproject.databinding.ActivityMainBinding
import com.example.edvoraproject.repository.Repository
import com.example.edvoraproject.ui.fragments.NearestRideFragment
import com.example.edvoraproject.ui.fragments.PastFragment
import com.example.edvoraproject.ui.fragments.UpcomingRideFragment
import com.example.edvoraproject.util.Resource

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var cities: List<String>
    lateinit var states: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        window.statusBarColor = resources.getColor(R.color.app_bar_color)
        setContentView(binding.root)

        val repository = Repository()
        val viewModelProviderFactory = ViewModelProviderFactory(application,repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[MainViewModel::class.java]


        binding.filter.setOnClickListener {
            if (binding.cardFilter.visibility == View.VISIBLE){
                hideFilterCard()
            }
            else{
                showFilterCard()
            }
        }

        setNearestFragment()

        viewModel.getUser()
        viewModel.user.observe(this, Observer { response ->
            when(response){
                is Resource.Success -> {
                    binding.userName.text = response.data?.name
                    Glide.with(this).load(response.data?.url).into(binding.userDp)
                    viewModel.getRides()
                }
                is Resource.Error -> {
                    Toast.makeText(this,"Error occurred: ${response.message}", Toast.LENGTH_LONG).show()
                    hideProgressBar()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        viewModel.rides.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    viewModel.nearestRides.postValue(viewModel.sortWithDistance(response.data!!))
                    cities= viewModel.getAllCities(response.data)
                    states= viewModel.getAllStates(response.data)
                    cities += listOf("None")
                    states += listOf("None")
                    val citiesAdapter = ArrayAdapter(this,R.layout.dropdown_item,cities)
                    val statesAdapter = ArrayAdapter(this,R.layout.dropdown_item,states)
                    binding.stateFilter.setAdapter(statesAdapter)
                    binding.cityFilter.setAdapter(citiesAdapter)
                    hideProgressBar()
                }
                is Resource.Error -> {
                    Toast.makeText(this,"Error occurred: ${response.message}", Toast.LENGTH_LONG).show()
                    hideProgressBar()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        setTopNavigation()

    }

    private fun setTopNavigation() {

        binding.nearest.setOnClickListener {
            binding.nearest.setTextColor(resources.getColor(R.color.white))
            binding.upcoming.setTextColor(resources.getColor(R.color.not_selected_text_color))
            binding.past.setTextColor(resources.getColor(R.color.not_selected_text_color))
            supportFragmentManager.beginTransaction().replace(binding.mainFrame.id, NearestRideFragment()).commit()

        }
        binding.upcoming.setOnClickListener {
            binding.upcoming.setTextColor(resources.getColor(R.color.white))
            binding.nearest.setTextColor(resources.getColor(R.color.not_selected_text_color))
            binding.past.setTextColor(resources.getColor(R.color.not_selected_text_color))
            supportFragmentManager.beginTransaction().replace(binding.mainFrame.id, UpcomingRideFragment()).commit()
        }
        binding.past.setOnClickListener {
            binding.past.setTextColor(resources.getColor(R.color.white))
            binding.nearest.setTextColor(resources.getColor(R.color.not_selected_text_color))
            binding.upcoming.setTextColor(resources.getColor(R.color.not_selected_text_color))
            supportFragmentManager.beginTransaction().replace(binding.mainFrame.id, PastFragment()).commit()
        }
    }


    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun showFilterCard(){
        binding.cardFilter.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }
    private fun hideFilterCard() {
        binding.cardFilter.visibility = View.INVISIBLE
    }


    private fun setNearestFragment(){
        binding.nearest.setTextColor(resources.getColor(R.color.white))
        supportFragmentManager.beginTransaction().replace(binding.mainFrame.id, NearestRideFragment()).commit()
    }

}