package com.example.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.main.database.UserDatabase
import com.example.ui.main.model.EditUser
import com.example.ui.main.model.UserDetails
import com.example.ui.main.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileDetailsViewModel : ViewModel() {
    private val repository = ProfileRepository()
    private val _userDetails = MutableLiveData<UserDetails>()
    val userDetails: MutableLiveData<UserDetails>
        get() = _userDetails

    fun getUserDetail(context: Context, id: Int = 1) {
        viewModelScope.launch {
            try {
                val userDao = UserDatabase.getDatabase(context).userDao()
                val cachedUser = userDao.getUserDetails(id) ?: userDao.getFirstUserDetails()

                if (cachedUser != null) {
                    // Profile data is already saved in Room database; load cached data without redundant API/save calls
                    _userDetails.value = cachedUser
                } else {
                    // Data not in Room database yet; fetch from API and save once
                    val response = repository.getUserDetails(id)
                    if (response?.isSuccessful == true) {
                        val remoteUser = response.body()
                        if (remoteUser != null) {
                            userDao.insertUserDetails(remoteUser)
                            _userDetails.value = remoteUser
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUserDetails(context: Context, id: Int, userEditDetails: EditUser) {
        viewModelScope.launch {
            try {
                val userDao = UserDatabase.getDatabase(context).userDao()
                val response = repository.editUserDetails(id, userEditDetails)
                if (response?.isSuccessful == true) {
                    val current = _userDetails.value
                    if (current != null) {
                        val updatedUser = current.copy(
                            firstName = userEditDetails.firstName,
                            lastName = userEditDetails.lastName,
                            email = userEditDetails.email,
                            phone = userEditDetails.phone,
                            birthDate = userEditDetails.birthDate,
                            gender = userEditDetails.gender,
                            bloodGroup = userEditDetails.bloodGroup,
                            height = userEditDetails.height,
                            weight = userEditDetails.weight
                        )
                        _userDetails.value = updatedUser
                        userDao.insertUserDetails(updatedUser)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
