package com.example.findride.model

import com.example.findride.common.EMPTY

data class RideBooking(
  val id: String = EMPTY,
  val ride: Ride = Ride(),
  val requester: User = User(),
  val inProcess: Boolean = true,
  val accepted: Boolean = false
)