package com.example.findride.ui.rides.booking.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.findride.R
import com.example.findride.common.formatTime
import com.example.findride.common.getRideEndRouteAddress
import com.example.findride.common.getRideStartRouteAddress
import com.example.findride.common.showDate
import com.example.findride.databinding.ItemUserBookedRideBinding
import com.example.findride.model.RideBooking
import com.example.findride.onClick

class UserRequestedRideBookingViewHolder(
  private val context: Context,
  private val binding: ItemUserBookedRideBinding,
  private val onItemClicked: (RideBooking) -> Unit,
  private val onPhoneCallClicked: (RideBooking) -> Unit,
  private val onCancelRideClicked: (RideBooking) -> Unit
) : RecyclerView.ViewHolder
  (binding.root) {
  
  fun bind(rideBooking: RideBooking) {
    with(binding) {
      requesterName.text = """${rideBooking.ride.name} ${rideBooking.ride.surname}"""
      price.text = "${rideBooking.ride.price} KN"
      date.text = showDate(rideBooking.ride.day, rideBooking.ride.month, rideBooking.ride.year)
      time.text = formatTime(rideBooking.ride.time.toInt() ?: 0) + " h"
      startDestination.text = getRideStartRouteAddress(context, rideBooking.ride)
      endDestination.text = getRideEndRouteAddress(context, rideBooking.ride)
      root.onClick { onItemClicked(rideBooking) }
      phone.onClick { onPhoneCallClicked(rideBooking) }
      if (rideBooking.inProcess) statusImage.setBackgroundResource(R.drawable.sand_hour_in_progress)
      else if (!rideBooking.accepted) statusImage.setBackgroundResource(R.drawable.ic_declined)
      else {
        statusImage.setBackgroundResource(R.drawable.ic_accepted)
        phone.visibility = View.VISIBLE
      }
      if (!rideBooking.accepted && !rideBooking.inProcess) cancelButton.visibility = View.INVISIBLE
      else cancelButton.onClick { onCancelRideClicked(rideBooking) }
    }
  }
}