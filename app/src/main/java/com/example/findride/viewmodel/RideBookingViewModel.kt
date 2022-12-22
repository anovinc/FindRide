package com.example.findride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findride.model.RideBooking
import com.example.findride.model.ScreenState
import com.example.findride.interactor.AuthenticationInteractor
import com.example.findride.interactor.RidesInteractorImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RideBookingViewModel @Inject constructor(
  private val ridesUseCaseImpl: RidesInteractorImpl,
  private val authenticationInteractor: AuthenticationInteractor
) : ViewModel() {
  
  private val _bookedRides = MutableLiveData<List<RideBooking>>()
  val bookedRides : LiveData<List<RideBooking>> = _bookedRides
  
  private val _screenState = MutableLiveData<ScreenState>()
  val screenState: LiveData<ScreenState> = _screenState
  
  fun signOut() {
    viewModelScope.launch {
      authenticationInteractor.signOut()
    }
  }
  
  fun getNotRespondedRides() {
    _screenState.value = ScreenState.LOADING
    viewModelScope.launch {
      _bookedRides.value = ridesUseCaseImpl.getRequestedBookingRides()
      _screenState.value = ScreenState.IDLE
  
    }
  }
}