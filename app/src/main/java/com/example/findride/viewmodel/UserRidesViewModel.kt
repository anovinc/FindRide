package com.example.findride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findride.model.Ride
import com.example.findride.model.RideBooking
import com.example.findride.model.ScreenState
import com.example.findride.interactor.AuthenticationInteractor
import com.example.findride.interactor.RidesInteractorImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserRidesViewModel @Inject constructor(
  private val ridesUseCaseImpl: RidesInteractorImpl,
  private val authenticationInteractor: AuthenticationInteractor
) : ViewModel() {
  
  private val _userRides = MutableLiveData<List<Ride>>()
  val userRides: LiveData<List<Ride>> = _userRides
  
  private val _isUserSignedOut = MutableLiveData<Boolean>()
  val isUserSignedOut: LiveData<Boolean> = _isUserSignedOut
  
  private val _screenState = MutableLiveData<ScreenState>()
  val screenState: LiveData<ScreenState> = _screenState
  
  private val _notRespondedRides = MutableLiveData<List<RideBooking>>()
  val notRespondedRides: LiveData<List<RideBooking>> = _notRespondedRides
  
  fun getUserRides(isNetworkAvailable: Boolean) {
    if (isNetworkAvailable) {
      _screenState.value = ScreenState.LOADING
      viewModelScope.launch {
        _userRides.value = ridesUseCaseImpl.getUserRides()
        _screenState.value = ScreenState.IDLE
      }
    }
    _screenState.value = ScreenState.NO_INTERNET
  }
  
  fun signOut(isNetworkAvailable: Boolean) {
    if (isNetworkAvailable) {
      viewModelScope.launch {
        authenticationInteractor.signOut()
        _isUserSignedOut.value = true
      }
    } else _screenState.value = ScreenState.NO_INTERNET
  }
  
  fun getNotRespondedRides() {
    viewModelScope.launch {
      _notRespondedRides.value = ridesUseCaseImpl.getRequestedBookingRides()
    }
  }
}