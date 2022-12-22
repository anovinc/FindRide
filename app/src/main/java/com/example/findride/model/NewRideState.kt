package com.example.findride.model

import com.example.findride.common.EMPTY

data class NewRideState(
  val day: Int = 0,
  val month: Int = 0,
  val year: Int = 0,
  var timeInSec: String = "0",
  val timeFormatted: String = EMPTY,
  val startDestinationLat: String = EMPTY,
  val startDestinationLong: String = EMPTY,
  val endDestinationLat: String = EMPTY,
  val endDestinationLong: String = EMPTY,
  val numOfAvailableSeats: Int = 0,
  val desc: String = EMPTY,
  var price: Int = 0,
  val screenState: ScreenState = ScreenState.IDLE
)

enum class ScreenState {
  LOADING, IDLE, ERROR, NO_INTERNET
}