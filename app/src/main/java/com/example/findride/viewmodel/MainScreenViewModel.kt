package com.example.findride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findride.data.FilterRidesRepository
import com.example.findride.interactor.AuthenticationInteractor
import com.example.findride.model.Ride
import com.example.findride.model.RideBooking
import com.example.findride.interactor.RidesInteractorImpl
import com.example.findride.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
  private val authenticationInteractor: AuthenticationInteractor,
  private val ridesUseCaseImpl: RidesInteractorImpl
) :
  ViewModel
    () {
  
  private val _isUserSignedOut = MutableLiveData<Boolean>()
  val isUserSignedOut: LiveData<Boolean> = _isUserSignedOut
  
  private val _screenState = MutableLiveData<ScreenState>()
  val screenState: LiveData<ScreenState> = _screenState
  
  private val _rides = MutableLiveData<List<Ride>>()
  val rides: LiveData<List<Ride>> = _rides
  
  private val _notRespondedRides = MutableLiveData<List<RideBooking>>()
  val notRespondedRides: LiveData<List<RideBooking>> = _notRespondedRides
  fun signOut() {
    viewModelScope.launch {
      authenticationInteractor.signOut()
      _isUserSignedOut.value = true
    }
  }
  
  fun getAllRides(isNetworkAvailable: Boolean) {
    if (isNetworkAvailable) {
      _screenState.value = ScreenState.LOADING
      viewModelScope.launch {
        _rides.value = ridesUseCaseImpl.getAllRides()
        _screenState.value = ScreenState.IDLE
      }
    } else _screenState.value = ScreenState.NO_INTERNET
  }
  
  fun getNotRespondedRides() {
    viewModelScope.launch {
      _notRespondedRides.value = ridesUseCaseImpl.getRequestedBookingRides()
    }
  }
}