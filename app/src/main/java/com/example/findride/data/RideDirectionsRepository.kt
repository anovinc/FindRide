package com.example.findride.data

import com.example.findride.model.NewRideState
import com.example.findride.model.RideRouteLocationModel
import com.google.android.gms.maps.model.LatLng

class RideDirectionsRepository {
  private var rideRouteLocationModel: RideRouteLocationModel = RideRouteLocationModel(LatLng(0.0, 0.0), LatLng(0.0, 0.0))
  private var newRideState = NewRideState()
  
  fun resetValues ()
  {
    rideRouteLocationModel = RideRouteLocationModel(LatLng(0.0, 0.0), LatLng(0.0, 0.0))
    newRideState.timeInSec = "0"
  }
  fun setStartLocation(startDestination: LatLng) {
    rideRouteLocationModel!!.startDestination = startDestination
  }
  
  fun setEndLocation(endDestination: LatLng) {
    rideRouteLocationModel!!.endLocation = endDestination
  }
  
  fun getStartLocation() = rideRouteLocationModel!!.startDestination
  
  fun getEndLocation() = rideRouteLocationModel!!.endLocation
  
  fun setRideTime(timeInSec: String) {
    newRideState.timeInSec = timeInSec
  }
  
  fun getRideTime() = newRideState.timeInSec
}