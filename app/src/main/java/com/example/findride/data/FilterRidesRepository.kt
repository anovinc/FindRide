package com.example.findride.data

import com.example.findride.model.FilterRide
import com.example.findride.model.Ride
import java.time.Month

class FilterRidesRepository {
  
  private var filterRidesList = mutableListOf<Ride>()
  private val filterRide = FilterRide()
  
  fun setFilterRides(list: List<Ride>) {
    filterRidesList.clear()
    filterRidesList.addAll(list)
  }
  
  fun getFilterRides() = filterRidesList
  
  fun resetData() {
    filterRidesList.clear()
  }
  
  fun setDate(day:Int, month: Int, year: Int) {
    filterRide.day = day
    filterRide.month = month
    filterRide.year = year
  }
  
  fun getDate() = "${filterRide.day}.${filterRide.month}.${filterRide.year}."
}