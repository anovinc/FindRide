package com.example.findride.ui.rides.booking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.findride.databinding.ItemUserBookedRideBinding
import com.example.findride.model.Ride
import com.example.findride.model.RideBooking

class UserRequestedAdapter(private val context: Context, private val onItemClicked: (RideBooking) -> Unit, private val
onPhoneCallClicked: (RideBooking) -> Unit, private val onCancelRideClicked: (RideBooking) -> Unit) :
  RecyclerView.Adapter<UserRequestedRideBookingViewHolder>() {
  
  private val rides = mutableListOf<RideBooking>()
  
  fun addRides(data: List<RideBooking>) {
    rides.clear()
    rides.addAll(data)
    notifyDataSetChanged()
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRequestedRideBookingViewHolder {
    return UserRequestedRideBookingViewHolder(
      context,
      ItemUserBookedRideBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      ),
      onItemClicked,
      onPhoneCallClicked,
      onCancelRideClicked
    )
  }
  
  override fun onBindViewHolder(responseHolder: UserRequestedRideBookingViewHolder, position: Int) {
    responseHolder.bind(rides[position])
  }
  
  override fun getItemCount() = rides.size
}