package com.example.findride.ui.rides.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.findride.R
import com.example.findride.common.formatTime
import com.example.findride.common.getRideEndRouteAddress
import com.example.findride.common.getRideStartRouteAddress
import com.example.findride.common.showDate
import com.example.findride.databinding.ItemRideBinding
import com.example.findride.model.Ride
import com.example.findride.onClick

class RidesViewHolder(
  private val binding: ItemRideBinding, private val context: Context, private val onItemClicked: (Ride) ->
  Unit
) : RecyclerView.ViewHolder(
  binding
    .root
) {
  
  fun bind(ride: Ride) {
    with(binding) {
      if (ride.numOfAvailableSeats == 0) {
        card.setBackgroundColor(
          context.getColor(R.color.light_gray)
        )
        rideName.text = ride.name
        rideSurname.text = ride.surname
        price.text = "${ride.price} kn"
        hideSeats(ride.numOfAvailableSeats)
        startDestination.text = getRideStartRouteAddress(context, ride)
        endDestination.text = getRideEndRouteAddress(context, ride)
        date.text = showDate(ride.day, ride.month, ride.year)
        time.text = formatTime(ride.time.toInt() ?: 0) + " h"
        filled.text = "No available seats."
        hideSeats(ride.numOfAvailableSeats)
      } else {
        root.onClick { onItemClicked(ride) }
        rideName.text = ride.name
        rideSurname.text = ride.surname
        price.text = "${ride.price} kn"
        hideSeats(ride.numOfAvailableSeats)
        startDestination.text = getRideStartRouteAddress(context, ride)
        endDestination.text = getRideEndRouteAddress(context, ride)
        date.text = showDate(ride.day, ride.month, ride.year)
        time.text = formatTime(ride.time.toInt() ?: 0) + " h"
      }
    }
  }
  
  private fun hideSeats(seats: Int) {
    with(binding) {
      if (seats == 0) {
        firstFreeSeat.visibility = View.GONE
        secondFreeSeat.visibility = View.GONE
        thirdFreeSeat.visibility = View.GONE
        fourthFreeSeat.visibility = View.GONE
        fifthFreeSeat.visibility = View.GONE
        sixthFreeSeat.visibility = View.GONE
      }
      if (seats == 1) {
        secondFreeSeat.visibility = View.GONE
        thirdFreeSeat.visibility = View.GONE
        fourthFreeSeat.visibility = View.GONE
        fifthFreeSeat.visibility = View.GONE
        sixthFreeSeat.visibility = View.GONE
      }
      if (seats == 2) {
        thirdFreeSeat.visibility = View.GONE
        fourthFreeSeat.visibility = View.GONE
        fifthFreeSeat.visibility = View.GONE
        sixthFreeSeat.visibility = View.GONE
      }
      if (seats == 3) {
        fourthFreeSeat.visibility = View.GONE
        fifthFreeSeat.visibility = View.GONE
        sixthFreeSeat.visibility = View.GONE
      }
      if (seats == 4) {
        fifthFreeSeat.visibility = View.GONE
        sixthFreeSeat.visibility = View.GONE
      }
      if (seats == 5) {
        sixthFreeSeat.visibility = View.GONE
      }
    }
  }
}