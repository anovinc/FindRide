package com.example.findride.interactor

import android.content.Context
import com.example.findride.model.*


interface RidesInteractor {
  
  suspend fun createNewRide(newRideState: NewRideState, onResult: (Boolean) -> Unit)
  
  suspend fun getAllRides(): List<Ride>
  
  suspend fun getRideDetails(id: String): Ride
  
  suspend fun getUserRides(): List<Ride>
  
  suspend fun deleteRide(rideId: String)
  
  suspend fun bookRide(rideId: String, onResult: (Boolean) -> Unit)
  
  suspend fun getBookedRides(): List<RideBooking>
  
  suspend fun isRideBooked(rideId: String): Boolean
  
  suspend fun getRequestedBookingRides(): List<RideBooking>
  
  suspend fun getUserBookingRides(): List<RideBooking>
  
  suspend fun declineBooking(bookingRideId: String)
  
  suspend fun acceptBooking(bookingRideId: String)
  
  suspend fun getPassengers(rideId: String): List<User>
  
  suspend fun filterRides(context: Context, filterRide: FilterRide): List<Ride>
  
  suspend fun cancelBookingRide(rideId: String, onResult: (Boolean) -> Unit)
  
}