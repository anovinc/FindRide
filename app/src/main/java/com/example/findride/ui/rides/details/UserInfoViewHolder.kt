package com.example.findride.ui.rides.details

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findride.databinding.ItemUsersInfoBinding
import com.example.findride.model.Ride
import com.example.findride.model.User
import com.example.findride.onClick

class UserInfoViewHolder(
  private val binding: ItemUsersInfoBinding, private val onPhoneClick: (User) -> Unit
) : RecyclerView.ViewHolder
  (binding.root)
  {
    fun bind(user: User) {
      with(binding) {
        Glide.with(root.context).load(user.profileImage).centerCrop().into(profileImage)
        name.text = "${user.name} ${user.surname}"
        icPhone.onClick { onPhoneClick(user) }
      }
    }
  }