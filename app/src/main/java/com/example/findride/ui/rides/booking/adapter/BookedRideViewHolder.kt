package com.example.findride.ui.rides.booking.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.findride.common.formatTime
import com.example.findride.common.getRideEndRouteAddress
import com.example.findride.common.getRideStartRouteAddress
import com.example.findride.common.showDate
import com.example.findride.databinding.ItemRequestedBookRideBinding
import com.example.findride.model.RideBooking
import com.example.findride.onClick

class BookedRideViewHolder(
  private val context: Context,
  private val binding: ItemRequestedBookRideBinding, private val onAcceptClick: (RideBooking) ->
  Unit, private val onDeclineClick: (RideBooking) ->
  Unit
) : RecyclerView.ViewHolder
  (binding.root) {
  
  fun bind(rideBooking: RideBooking) {
    with(binding) {
      btnAccept.onClick { onAcceptClick(rideBooking) }
      btnDecline.onClick { onDeclineClick(rideBooking) }
      requesterName.text = """${rideBooking.requester.name} ${rideBooking.requester.surname}"""
      price.text = "${rideBooking.ride.price} KN"
      date.text = showDate(rideBooking.ride.day, rideBooking.ride.month, rideBooking.ride.year)
      time.text = formatTime(rideBooking.ride.time.toInt() ?: 0) + " h"
      startDestination.text = getRideStartRouteAddress(context, rideBooking.ride)
      endDestination.text = getRideEndRouteAddress(context, rideBooking.ride)
    }
  }
}