package com.example.findride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findride.model.RideBooking
import com.example.findride.model.ScreenState
import com.example.findride.interactor.RidesInteractorImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookedRidesViewModel @Inject constructor(
  private val ridesUseCaseImpl: RidesInteractorImpl
) : ViewModel() {
  
  private val _isRideListUpdated = MutableLiveData<Boolean>()
  val isRideListUpdated: LiveData<Boolean> = _isRideListUpdated
  
  private val _bookedRides = MutableLiveData<List<RideBooking>>()
  val bookedRides: LiveData<List<RideBooking>> = _bookedRides
  
  private val _userBookedRides = MutableLiveData<List<RideBooking>>()
  val userBookedRides: LiveData<List<RideBooking>> = _userBookedRides
  
  private val _screenState = MutableLiveData<ScreenState>()
  val screenState: LiveData<ScreenState> = _screenState
  
  fun getRequestedBookings() {
    _screenState.value = ScreenState.LOADING
    viewModelScope.launch {
      _bookedRides.value = ridesUseCaseImpl.getRequestedBookingRides()
      _screenState.value = ScreenState.IDLE
      
    }
  }
  
  fun getUserRequestedRidesBookings() {
    _screenState.value = ScreenState.LOADING
    viewModelScope.launch {
      _userBookedRides.value = ridesUseCaseImpl.getUserBookingRides()
      _screenState.value = ScreenState.IDLE
    }
  }
  
  fun acceptBooking(rideId: String) {
    _screenState.value = ScreenState.LOADING
    viewModelScope.launch {
      ridesUseCaseImpl.acceptBooking(rideId)
      _isRideListUpdated.value = true
      _screenState.value = ScreenState.IDLE
    }
  }
  
  fun declineBooking(rideId: String) {
    _screenState.value = ScreenState.LOADING
    viewModelScope.launch {
      ridesUseCaseImpl.declineBooking(rideId)
      _isRideListUpdated.value = true
      _screenState.value = ScreenState.IDLE
    }
  }
  
  fun cancelBooking(rideId: String) {
    _screenState.value = ScreenState.LOADING
    viewModelScope.launch {
      ridesUseCaseImpl.cancelBookingRide(rideId) { isSuccesful ->
        if (isSuccesful) _isRideListUpdated.value = true
      }
      _screenState.value = ScreenState.IDLE
    }
  }
}