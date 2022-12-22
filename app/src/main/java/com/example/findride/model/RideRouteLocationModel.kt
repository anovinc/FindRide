package com.example.findride.model

import com.google.android.gms.maps.model.LatLng

data class RideRouteLocationModel(
  var startDestination: LatLng,
  var endLocation: LatLng
)
