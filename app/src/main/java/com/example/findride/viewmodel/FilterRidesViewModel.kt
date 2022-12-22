package com.example.findride.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findride.data.FilterRidesRepository
import com.example.findride.interactor.RidesInteractor
import com.example.findride.model.FilterRide
import com.example.findride.model.Ride
import com.example.findride.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterRidesViewModel @Inject constructor(private val ridesInteractor: RidesInteractor, private val filterRidesRepository: FilterRidesRepository) : ViewModel() {
  
  private val _filterRide = MutableLiveData(FilterRide())
  val filterRide: LiveData<FilterRide> = _filterRide
  
  private val _ridesList = MutableLiveData<List<Ride>>()
  val ridesList: LiveData<List<Ride>> = _ridesList
  
  private val _screenState = MutableLiveData<ScreenState>()
  val screenState: LiveData<ScreenState> = _screenState
  
  fun setRideDate(day: Int, month: Int, year: Int) {
    _filterRide.value = _filterRide.value?.copy(day = day)
    _filterRide.value = _filterRide.value?.copy(month = month+1)
    _filterRide.value = _filterRide.value?.copy(year = year)
    filterRidesRepository.setDate(day, month, year)
  }
  
  fun setPriceRange(start: Int, end: Int) {
    _filterRide.value = _filterRide.value?.copy(startPrice = start)
    _filterRide.value = _filterRide.value?.copy(endPrice = end)
  }
  
  fun filterRides(context: Context) {
    _screenState.value = ScreenState.LOADING
    viewModelScope.launch {
      _ridesList.value = _filterRide.value?.let { ridesInteractor.filterRides(context, it) }
      _ridesList.value?.let { filterRidesRepository.setFilterRides(it) }
      _screenState.value = ScreenState.IDLE
    }
  }
  
  fun getRides() {
    _ridesList.value = filterRidesRepository.getFilterRides()
  }
  fun setRideRoute(start: String, end: String) {
    _filterRide.value = _filterRide.value?.copy(start = start)
    _filterRide.value = _filterRide.value?.copy(end = end)
  }
  
  fun getDate() = filterRidesRepository.getDate()
}