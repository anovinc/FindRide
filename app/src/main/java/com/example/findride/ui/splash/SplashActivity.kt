package com.example.findride.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.findride.ui.main.MainActivity
import com.example.findride.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
  
  private val viewModel: SplashViewModel by viewModels()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val intent = Intent(applicationContext, MainActivity::class.java)
    intent.putExtra("isUserLoggedIn", isUserLoggedIn())
    startActivity(intent)
    finish()
  
  }
  
  private fun isUserLoggedIn(): Boolean = viewModel.isUserLoggedIn()
}