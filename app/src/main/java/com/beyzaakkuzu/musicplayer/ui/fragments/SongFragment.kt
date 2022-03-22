package com.beyzaakkuzu.musicplayer.ui.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyzaakkuzu.musicplayer.R
import com.beyzaakkuzu.musicplayer.adapter.SongsAdapter
import com.beyzaakkuzu.musicplayer.databinding.FragmentSongBinding
import com.beyzaakkuzu.musicplayer.repository.SongRepository
import com.beyzaakkuzu.musicplayer.ui.SongViewModel
import com.beyzaakkuzu.musicplayer.ui.SongViewModelFactory
import com.beyzaakkuzu.musicplayer.view.ScrollingViewOnApplyWindow
import me.zhanghai.android.fastscroll.FastScroller
import me.zhanghai.android.fastscroll.FastScrollerBuilder


class SongFragment : Fragment(R.layout.fragment_song) {


    private lateinit var viewModel: SongViewModel
    private lateinit var adapter: SongsAdapter
    private lateinit var binding:FragmentSongBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = SongRepository(requireContext())
        val viewModelFactory = SongViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SongViewModel::class.java]


    }

    override fun onResume() {
        super.onResume()
        viewModel.forceReload()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongBinding.inflate(inflater, container, false)

        initLayoutManager()
        initAdapter()
        // inflate the layout and bind to the _binding
        viewModel.songLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.updateSongList(it)
            } else {
                adapter.updateSongList(emptyList())
            }
        }
        val fastScroller = createFastScroller(binding.rvSongs)

        binding.rvSongs.setOnApplyWindowInsetsListener(
            ScrollingViewOnApplyWindow(binding.rvSongs, fastScroller)
        )
        return binding.root
    }


    private fun createFastScroller(recyclerView: RecyclerView): FastScroller? {
        return FastScrollerBuilder(recyclerView).useMd2Style().build()
    }

    private fun initAdapter() {
        adapter = SongsAdapter(requireContext(), mutableListOf())
        binding.rvSongs.adapter = adapter
    }

    private fun initLayoutManager() {
        binding.rvSongs.layoutManager = LinearLayoutManager(activity as Context)
    }
}