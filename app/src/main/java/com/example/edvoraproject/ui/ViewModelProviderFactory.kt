package com.example.edvoraproject.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.edvoraproject.repository.Repository

class ViewModelProviderFactory(val app: Application,val repository: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(app,repository) as T
    }
}