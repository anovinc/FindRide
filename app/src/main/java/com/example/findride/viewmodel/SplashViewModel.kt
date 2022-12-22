package com.example.findride.viewmodel

import androidx.lifecycle.ViewModel
import com.example.findride.data.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val sharedPrefs: SharedPrefs): ViewModel() {
  
  fun isUserLoggedIn(): Boolean {
    return !sharedPrefs.getUser().isNullOrEmpty()
  }
}