package com.example.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.main.model.UserDetails
import com.example.ui.main.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileDetailsViewModel : ViewModel() {
    private val  repository = ProfileRepository()
    private val _userDetails = MutableLiveData<UserDetails>()

    val userDetails : MutableLiveData<UserDetails>
        get() = _userDetails

    init {
        getUserDetail(1)
    }

    fun getUserDetail(id : Int)
    {
        viewModelScope.launch {
            try{
                val response = repository.getUserDetails(id)
                if(response?.isSuccessful == true)
                {
                    _userDetails.value = response.body()
                }

            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

        }
    }
}