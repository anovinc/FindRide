package com.example.findride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findride.interactor.RidesInteractorImpl
import com.example.findride.model.Ride
import com.example.findride.model.ScreenState
import com.example.findride.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RideDetailsViewModel @Inject constructor(
  private val ridesUseCaseImpl: RidesInteractorImpl
) : ViewModel() {
  
  private val _ride = MutableLiveData<Ride>()
  val ride: LiveData<Ride> = _ride
  
  private val _isRideDeleted = MutableLiveData<Boolean>()
  val isRideDeleted: LiveData<Boolean> = _isRideDeleted
  
  private val _navigateFromDetails = MutableLiveData<Boolean>()
  val navigateFromDetails: LiveData<Boolean> = _navigateFromDetails
  
  private val _isRideBooked = MutableLiveData<Boolean>()
  val isRideBooked: LiveData<Boolean> = _isRideBooked
  
  private val _screenState = MutableLiveData<ScreenState>()
  val screenState: LiveData<ScreenState> = _screenState
  
  private val _passengersList = MutableLiveData<List<User>>()
  val passengersList : LiveData<List<User>> = _passengersList
  
  fun getRide(id: String) {
    _screenState.value = ScreenState.LOADING
    viewModelScope.launch {
      _ride.postValue(ridesUseCaseImpl.getRideDetails(id))
      _screenState.value = ScreenState.IDLE
    }
  }
  
  fun deleteRide(id: String) {
    _screenState.value = ScreenState.LOADING
    viewModelScope.launch {
      ridesUseCaseImpl.deleteRide(id)
      _isRideDeleted.value = true
      _screenState.value = ScreenState.IDLE
    }
  }
  
  fun bookRide(rideId: String) {
    _screenState.value = ScreenState.LOADING
    viewModelScope.launch {
      ridesUseCaseImpl.bookRide(rideId) { isSuccessful ->
        if (isSuccessful) {
          _screenState.value = ScreenState.IDLE
          _navigateFromDetails.value = true
        }
      }
    }
  }
  
  fun isRideBooked(rideId: String) {
    viewModelScope.launch {
      _isRideBooked.value = ridesUseCaseImpl.isRideBooked(rideId)
    }
  }
  
  fun getPassengers(rideId: String) {
    viewModelScope.launch {
      _passengersList.value = ridesUseCaseImpl.getPassengers(rideId)
    }
  }
}