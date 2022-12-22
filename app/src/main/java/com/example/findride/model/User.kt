package com.example.findride.model

import com.example.findride.common.EMPTY

data class User(
  val id: String = EMPTY,
  val username: String = EMPTY,
  val name: String = EMPTY,
  val surname: String = EMPTY,
  val phoneNumber: String = EMPTY,
  val profileImage: String = EMPTY
)