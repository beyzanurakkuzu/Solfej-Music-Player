package com.beyzaakkuzu.musicplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyzaakkuzu.musicplayer.model.SongModel
import com.beyzaakkuzu.musicplayer.repository.SongRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.NullPointerException

class SongViewModel(private val repository:SongRepository):ViewModel() {
    private val songs=MutableLiveData<List<SongModel>>()
    val songLiveData:LiveData<List<SongModel>> = songs

    init {
        loadLibraryContent()
    }

    private fun loadLibraryContent() = viewModelScope.launch {
        songs.value = loadSongs.await()
    }

    private val loadSongs: Deferred<List<SongModel>>
    get() = viewModelScope.async(Dispatchers.IO){
    repository.getAllSongs()

}
    fun forceReload()= viewModelScope.launch{
        val list=loadSongs.await()
        try {
            if (songs.value!!.size != list.size){
                songs.value=list
            }

        }catch (e:NullPointerException){
            e.printStackTrace()
        }
    }
}