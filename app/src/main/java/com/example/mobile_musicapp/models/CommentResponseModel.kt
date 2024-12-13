package com.example.mobile_musicapp.models

import java.util.Date


data class CommentResponse(
    val code : Int,
    val data : List<CommentModel>
)

data class CommentModel(
    val userInfo : UserInfo,
    val content : String,
    val createdAt : Date = Date()
)

data class UserInfo(
    val _id: String,
    val fullName : String,
)