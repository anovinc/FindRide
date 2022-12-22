package com.example.findride.data

interface SharedPrefs {
  fun getUser(): String?
  fun saveUser(user: String)
  fun getUserId(): String?
  fun saveUserId(userId: String)
}