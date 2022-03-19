package com.beyzaakkuzu.musicplayer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beyzaakkuzu.musicplayer.repository.SongRepository

class SongViewModelFactory(private val repository: SongRepository):
    ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SongViewModel(repository) as T
        }
}