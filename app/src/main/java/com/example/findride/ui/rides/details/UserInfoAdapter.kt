package com.example.findride.ui.rides.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.findride.databinding.ItemUsersInfoBinding
import com.example.findride.model.Ride
import com.example.findride.model.User


class UserInfoAdapter(private val onPhoneClick: (User) -> Unit) :
  RecyclerView.Adapter<UserInfoViewHolder>() {
  
  private val users = mutableListOf<User>()
  
  fun addData(data: List<User>) {
    users.clear()
    users.addAll(data)
    notifyDataSetChanged()
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoViewHolder {
    return UserInfoViewHolder(
      ItemUsersInfoBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      ), onPhoneClick
    )
  }
  
  override fun onBindViewHolder(holder: UserInfoViewHolder, position: Int) {
    holder.bind(users[position])
  }
  
  override fun getItemCount() = users.size
}