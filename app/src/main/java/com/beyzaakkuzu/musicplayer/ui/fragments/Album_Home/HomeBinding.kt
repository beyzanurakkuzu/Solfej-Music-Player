package com.beyzaakkuzu.musicplayer.ui.fragments.Album_Home

import com.beyzaakkuzu.musicplayer.databinding.FragmentAlbumBinding

class HomeBinding(
    homeBinding: FragmentAlbumBinding
) {
    val root = homeBinding.root
    val container = homeBinding.container
    val appBarLayout = homeBinding.appBarLayout
    val contentContainer= homeBinding.contentContainer
    val toolbar = homeBinding.toolbar
    val lastAdded = homeBinding.homeContent.absPlaylists.lastAdded
    val topPlayed = homeBinding.homeContent.absPlaylists.topPlayed
    val actionShuffle = homeBinding.homeContent.absPlaylists.actionShuffle
    val history = homeBinding.homeContent.absPlaylists.history
    val appNameText = homeBinding.appNameText
}