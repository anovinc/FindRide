package com.example.findride.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.findride.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setTheme(R.style.Theme_FindRide)
    setContentView(R.layout.activity_main)
    setNavigationGraph()
  }
  
  private fun setNavigationGraph() {
    val isUserLoggedIn = intent.getBooleanExtra("isUserLoggedIn", false)
    val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment
    val navController = navHostFragment.navController
    
    val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
    if (isUserLoggedIn) navGraph.setStartDestination(R.id.mainScreen)
    else navGraph.setStartDestination(R.id.loginFragment)
    navController.graph = navGraph
    
  }
}