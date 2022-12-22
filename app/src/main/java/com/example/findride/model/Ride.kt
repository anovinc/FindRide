package com.example.findride.model

import com.example.findride.common.EMPTY

data class Ride(
  val id: String = EMPTY,
  val authorId: String = EMPTY,
  val name: String = EMPTY,
  val surname: String = EMPTY,
  val phoneNumber: String = EMPTY,
  val profilePicture: String = EMPTY,
  val day: Int = 0,
  val month: Int = 0,
  val year: Int = 0,
  val time: String = EMPTY,
  val startDestinationLat: String = EMPTY,
  val startDestinationLong: String = EMPTY,
  val endDestinationLat: String = EMPTY,
  val endDestinationLong: String = EMPTY,
  val numOfAvailableSeats: Int = 0,
  val desc: String = EMPTY,
  val price: Int = 0
)