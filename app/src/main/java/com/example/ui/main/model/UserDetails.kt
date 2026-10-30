package com.example.ui.main.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_details")
data class UserDetails(
    val age: Int,
    val birthDate: String,
    val bloodGroup: String,
    val email: String,
    val eyeColor: String,
    val firstName: String,
    val gender: String,
    val height: Double,
    @PrimaryKey
    val id: Int,
    val image: String,
    val lastName: String,
    val maidenName: String,
    val password: String,
    val phone: String,
    val username: String,
    val weight: Double
)
