package com.virtualszafa.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualszafa.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _profile = MutableStateFlow(Profile())
    val profile = _profile.asStateFlow()

    fun saveProfile(newProfile: Profile) {
        viewModelScope.launch {
            _profile.value = newProfile
            // TODO: zapis do DataStore / Room / Firebase
        }
    }
}