package com.example.ui.main.jetpack


import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Profile : NavKey

@Serializable
data object UserDetails : NavKey

@Serializable
data object Settings : NavKey

@Serializable
data object HelpUs : NavKey

@Serializable
data object Appearance : NavKey
