package com.beyzaakkuzu.musicplayer.model

data class SongModel(
    val id: String,
    val name: String,
    val path: String,
    val artistName: String?,
    val albumName: String?,
    val duration: Long
)
