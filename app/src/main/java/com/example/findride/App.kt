package com.example.findride

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
  companion object {
    lateinit var application : App
  }
  
  override fun onCreate(
  ) {
    super.onCreate()
    application = this
  }
}