package com.example.findride.ui.rides.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.findride.databinding.ItemRideBinding
import com.example.findride.model.Ride

class RidesAdapter(private val context: Context, private val onItemClicked: (Ride) -> Unit) :
  RecyclerView.Adapter<RidesViewHolder>
    () {
  
  private val rides = mutableListOf<Ride>()
  
  fun addRides(data: List<Ride>) {
    rides.clear()
    rides.addAll(data)
    notifyDataSetChanged()
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RidesViewHolder {
    return RidesViewHolder(
      ItemRideBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      ),
      context,
      onItemClicked
    )
  }
  
  override fun onBindViewHolder(responseHolder: RidesViewHolder, position: Int) {
    responseHolder.bind(rides[position])
  }
  
  override fun getItemCount() = rides.size
}