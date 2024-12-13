package com.example.mobile_musicapp.models

data class CategoryModel(
    val id : String,
    val name : String ,
    val url : String ,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
