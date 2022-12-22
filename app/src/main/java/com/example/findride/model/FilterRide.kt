package com.example.findride.model

import com.example.findride.common.EMPTY

data class FilterRide(
  val start: String = EMPTY,
  val end: String = EMPTY,
  var day: Int = 0,
  var month: Int = 0,
  var year: Int = 0,
  val startPrice: Int  = 0,
  val endPrice: Int = 999
)
