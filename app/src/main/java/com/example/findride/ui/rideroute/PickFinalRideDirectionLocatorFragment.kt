package com.example.findride.ui.rideroute

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.findride.R
import com.example.findride.databinding.FragmentPickRideRouteLocatorBinding
import com.example.findride.onClick
import com.example.findride.ui.base.BaseFragment
import com.example.findride.viewmodel.AddNewRideViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PickFinalRideDirectionLocatorFragment : BaseFragment<FragmentPickRideRouteLocatorBinding>(), OnMapReadyCallback {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPickRideRouteLocatorBinding
    get() = FragmentPickRideRouteLocatorBinding::inflate
  var currentLocation: Location? = null
  var fusedLocationProviderClient: FusedLocationProviderClient? = null
  val REQUEST_CODE = 101
  private var geocoder: Geocoder? = null
  
  private val viewModel: AddNewRideViewModel by viewModels()
  override fun onPostViewCreated() {
    fusedLocationProviderClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }
    fetchLocation()
    binding.infoTitle.text = "Please select your ending point."
    binding.btnConfirm.onClick {
      viewModel.setRideRoute()
      findNavController().navigate(R.id.action_pickFinalRideDirectionLocatorFragment_to_addNewRideFragment)
    }
  }
  
  private fun fetchLocation() {
    if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) }
      != PackageManager.PERMISSION_GRANTED && context?.let {
        ActivityCompat.checkSelfPermission(
          it,
          Manifest.permission.ACCESS_COARSE_LOCATION
        )
      }
      != PackageManager.PERMISSION_GRANTED) {
      activity?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE) }
      return
    }
    
    val task = fusedLocationProviderClient!!.lastLocation
    task.addOnSuccessListener { location ->
      if (location != null) {
        currentLocation = location
        val mapFragment = childFragmentManager.findFragmentById(R.id.googleMapsView) as SupportMapFragment
        mapFragment.getMapAsync(this)
      }
    }
  }
  
  override fun onMapReady(googleMap: GoogleMap) {
    geocoder = Geocoder(activity, Locale.getDefault())
    val startAddress: MutableList<Address>? = geocoder?.getFromLocation(
      viewModel.getStartDestination()!!.latitude, viewModel.getStartDestination()!!
        .longitude, 1
    )
    val startLocation = LatLng(viewModel.getStartDestination().latitude, viewModel.getStartDestination().longitude)
    googleMap.addMarker(MarkerOptions().position(startLocation).title("${startAddress!![0].getAddressLine(0)}").icon(BitmapDescriptorFactory
      .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
    googleMap.animateCamera(CameraUpdateFactory.newLatLng(viewModel.getStartDestination()))
    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getStartDestination(), 10f))
    binding.startDestination.text = "Start destination:  ${startAddress!![0].getAddressLine(0)}"
    googleMap.setOnMapClickListener {
      val endAddress: MutableList<Address>? = geocoder?.getFromLocation(
        it!!.latitude, it!!
          .longitude, 1
      )
      viewModel.setEndDestination(it)
      googleMap.clear()
      googleMap.addMarker(MarkerOptions().position(startLocation).title("${startAddress!![0].getAddressLine(0)}").icon(
        BitmapDescriptorFactory
        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
      googleMap.animateCamera(CameraUpdateFactory.newLatLng(it))
      val location = LatLng(it.latitude, it.longitude)
      googleMap.addMarker(
        MarkerOptions().position(location).title("${endAddress!![0].getAddressLine(0)}")
      )
      binding.endDestination.text = "End destination:  ${endAddress!![0].getAddressLine(0)}"
      
    }
  }
  
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      REQUEST_CODE -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          fetchLocation()
        }
      }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }
}