package com.example.edvoraproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.edvoraproject.databinding.ActivityMainBinding
import com.example.edvoraproject.ui.NearestRideFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNearestFragment()

    }

    private fun setNearestFragment(){
        supportFragmentManager.beginTransaction().replace(binding.mainFrame.id, NearestRideFragment()).commit()
    }

}