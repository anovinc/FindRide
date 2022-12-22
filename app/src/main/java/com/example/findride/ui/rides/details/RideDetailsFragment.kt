package com.example.findride.ui.rides.details

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.findride.R
import com.example.findride.common.getRideEndRouteAddress
import com.example.findride.common.getRideStartRouteAddress
import com.example.findride.databinding.FragmentRideDetailsBinding
import com.example.findride.model.ScreenState
import com.example.findride.model.User
import com.example.findride.onClick
import com.example.findride.subscribe
import com.example.findride.ui.base.BaseFragment
import com.example.findride.viewmodel.RideDetailsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class RideDetailsFragment : BaseFragment<FragmentRideDetailsBinding>(), OnMapReadyCallback {
  private lateinit var mMap: GoogleMap
  
  private val args: RideDetailsFragmentArgs by navArgs()
  private var geocoder: Geocoder? = null
  private val REQUEST_CODE = 101
  
  var fusedLocationProviderClient: FusedLocationProviderClient? = null
  private val viewModel: RideDetailsViewModel by viewModels()
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRideDetailsBinding
    get() = FragmentRideDetailsBinding::inflate
  
  private val userInfoAdapter by lazy {
    UserInfoAdapter(onPhoneClick = { user -> callPerson(user) })
  }
  
  private fun callPerson(user: User) {
    val hasPermission = activity?.let {
      ContextCompat.checkSelfPermission(
        it,
        Manifest.permission.CALL_PHONE
      )
    } == PackageManager.PERMISSION_GRANTED
    if (!hasPermission) {
      requestPermission()
    } else {
      val intent = Intent(Intent.ACTION_CALL)
      intent.data = Uri.parse("tel:${user.phoneNumber}")
      startActivity(intent)
    }
  }
  
  private fun requestPermission() {
    activity?.let {
      ActivityCompat.requestPermissions(
        it,
        arrayOf(Manifest.permission.CALL_PHONE),
        200
      )
    }
  }
  
  override fun onPostViewCreated() {
    initUI()
    initToolbar()
    fusedLocationProviderClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }
    viewModel.getRide(args.rideId)
    if (args.fromWhichFragment == 1) viewModel.isRideBooked(args.rideId)
    viewModel.ride.observe(viewLifecycleOwner) {
      setupRecycler()
      observePassengers()
      fetchLocation()
      with(binding) {
        name.text = it.name
        surname.text = it.surname
        date.text = """${it.day}.${it.month}.${it.year}."""
        directionValueStart.text = context?.let { it1 -> getRideStartRouteAddress(it1, it) }
        directionValueEnd.text = context?.let { it1 -> getRideEndRouteAddress(it1, it) }
        availableSeatsValue.text = it.numOfAvailableSeats.toString()
        priceValue.text = "${it.price} KN"
        if (it.desc.isNullOrEmpty()) desc.text = "User did not entered additional informations about ride, car and etc."
        else desc.text = it.desc
        context?.let { context -> Glide.with(context).load(Uri.parse(it.profilePicture)).centerCrop().into(profileImage) }
      }
      
      viewModel.isRideDeleted.observe(viewLifecycleOwner) {
        if (it) {
          navigateToMainScreen()
        }
      }
      viewModel.isRideBooked.observe(viewLifecycleOwner) {
        if (it) binding.bookBtn.visibility = View.GONE
      }
      viewModel.screenState.subscribe(viewLifecycleOwner, ::observeScreenState)
    }
    initListeners()
  }
  
  private fun initToolbar() {
    activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
    (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    binding.toolbar.titleToolbar.text = "Ride details"
    binding.toolbar.backBtn.onClick {
      navigateToMainScreenFromBackBtn()
    }
  }
  
  private fun navigateToMainScreenFromBackBtn() {
    if (args.fromWhichFragment == 2) findNavController().navigate(R.id.action_rideDetailsFragment_to_userRidesListFragment)
    else if (args.fromWhichFragment == 3) findNavController().navigate(R.id.action_rideDetailsFragment_to_fragmentBookings)
    else if (args.fromWhichFragment == 4) findNavController().navigate(R.id.action_rideDetailsFragment_to_filteredRidesListFragment)
    else findNavController().navigate(R.id.action_rideDetailsFragment_to_mainScreen)
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.googleMapsView) as SupportMapFragment
        mapFragment.getMapAsync(this)
      }
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
  
  private fun initUI() {
    with(binding) {
      if (args.fromWhichFragment == 2) {
        passengers.visibility = View.VISIBLE
        recycler.visibility = View.VISIBLE
        thirdSeparator.visibility = View.VISIBLE
        bookBtn.visibility = View.GONE
        deleteBtn.visibility = View.VISIBLE
      } else if (args.fromWhichFragment == 3) {
        bookBtn.visibility = View.GONE
        deleteBtn.visibility = View.GONE
      } else {
        bookBtn.visibility = View.VISIBLE
        deleteBtn.visibility = View.GONE
      }
    }
  }
  
  private fun initListeners() {
    with(binding) {
      deleteBtn.onClick {
        viewModel.deleteRide(args.rideId)
      }
      bookBtn.onClick {
        viewModel.bookRide(args.rideId)
        viewModel.navigateFromDetails.observe(viewLifecycleOwner) {
          if (it) navigateToMainScreen()
        }
      }
    }
  }
  
  private fun setupRecycler() {
    with(binding.recycler) {
      layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
      adapter = userInfoAdapter
    }
    
  }
  
  private fun observePassengers() {
    viewModel.getPassengers(args.rideId)
    viewModel.passengersList.observe(viewLifecycleOwner, ::showUserInfo)
  }
  
  private fun showUserInfo(list: List<User>) {
    userInfoAdapter.addData(list)
  }
  
  private fun observeScreenState(screenState: ScreenState) {
    when (screenState) {
      ScreenState.LOADING -> {
        binding.progress.root.visibility = View.VISIBLE
      }
      ScreenState.IDLE -> {
        binding.progress.root.visibility = View.GONE
      }
    }
  }
  
  private fun navigateToMainScreen() {
    if (args.fromWhichFragment == 2) findNavController().navigate(R.id.action_rideDetailsFragment_to_userRidesListFragment)
    else if (args.fromWhichFragment == 3) findNavController().navigate(R.id.action_rideDetailsFragment_to_fragmentBookings)
    else findNavController().navigate(R.id.action_rideDetailsFragment_to_mainScreen)
  }
  
  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    geocoder = Geocoder(activity, Locale.getDefault())
    val startAddress: MutableList<Address>? = geocoder?.getFromLocation(
      viewModel.ride.value!!.startDestinationLat.toDouble(), viewModel.ride.value!!.startDestinationLong.toDouble(), 1
    )
    val startLocation = LatLng(
      viewModel.ride.value!!.startDestinationLat.toDouble(), (viewModel.ride.value!!.startDestinationLong
        .toDouble())
    )
    mMap.addMarker(
      MarkerOptions().position(startLocation).title("${startAddress!![0].getAddressLine(0)}").icon(
        BitmapDescriptorFactory
          .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
      )
    )
    mMap.animateCamera(
      CameraUpdateFactory.newLatLng(
        LatLng(
          viewModel.ride.value!!.startDestinationLat.toDouble(), (viewModel.ride.value!!.startDestinationLong
            .toDouble())
        )
      )
    )
    mMap.animateCamera(
      CameraUpdateFactory.newLatLngZoom(
        LatLng(
          viewModel.ride.value!!.startDestinationLat.toDouble(), (viewModel.ride.value!!.startDestinationLong
            .toDouble())
        ), 8f
      )
    )
    val endAddress: MutableList<Address>? = geocoder?.getFromLocation(
      viewModel.ride.value!!.endDestinationLat.toDouble(), viewModel.ride.value!!.endDestinationLong.toDouble(), 1
    )
    val endLocation = LatLng(
      viewModel.ride.value!!.endDestinationLat.toDouble(), (viewModel.ride.value!!.endDestinationLong
        .toDouble())
    )
    mMap.addMarker(MarkerOptions().position(endLocation).title("${endAddress!![0].getAddressLine(0)}"))
  }
  
}