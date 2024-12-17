package com.example.mobile_musicapp.models

import java.util.Date


data class CommentResponse(
    val code : Int,
    val data : List<CommentModel>
)

data class CommentModel(
    var userInfo : UserInfo,
    var content : String,
    var createdAt : Date = Date()
)

data class UserInfo(
    var _id: String,
    var fullName : String,
)