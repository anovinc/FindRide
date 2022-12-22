package com.example.findride.ui.rides.booking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.findride.databinding.ItemRequestedBookRideBinding
import com.example.findride.model.RideBooking

class BookedRideAdapter(private val context: Context, private val onAcceptClick: (RideBooking) -> Unit, private val
onDeclineClick: (RideBooking) -> Unit) :
  RecyclerView.Adapter<BookedRideViewHolder>() {
  
  private val rides = mutableListOf<RideBooking>()
  
  fun addRides(data: List<RideBooking>) {
    rides.clear()
    rides.addAll(data)
    notifyDataSetChanged()
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedRideViewHolder {
    return BookedRideViewHolder(context,
      ItemRequestedBookRideBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      ),
      onAcceptClick,
      onDeclineClick
    )
  }
  
  override fun onBindViewHolder(responseHolder: BookedRideViewHolder, position: Int) {
    responseHolder.bind(rides[position])
  }
  
  override fun getItemCount() = rides.size
}