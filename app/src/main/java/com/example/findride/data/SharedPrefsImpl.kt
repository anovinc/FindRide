package com.example.findride.data

import android.content.Context
import javax.inject.Inject

class SharedPrefsImpl @Inject constructor(context: Context) : SharedPrefs {
  
  private var preference = context.getSharedPreferences("FindRidePrefs", Context.MODE_PRIVATE)
  private var editor = preference.edit()
  
  override fun getUser() = preference.getString("user", "")
  
  
  override fun saveUser(user: String) {
    editor.putString("user", user)
    editor.apply()
  }
  
  override fun getUserId(): String? = preference.getString("userId", "")
  
  override fun saveUserId(userId: String) {
    editor.putString("userId", userId)
    editor.apply()
  }
  
}