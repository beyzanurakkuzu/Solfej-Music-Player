package com.beyzaakkuzu.musicplayer.ui.fragments.Album_Home

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyzaakkuzu.musicplayer.R
import com.beyzaakkuzu.musicplayer.adapter.HomeAdapter
import com.beyzaakkuzu.musicplayer.databinding.FragmentAlbumBinding
import com.beyzaakkuzu.musicplayer.model.SongModel
import com.beyzaakkuzu.musicplayer.ui.activity.MainActivity
import com.beyzaakkuzu.musicplayer.util.MusicPlayerRemote
import com.beyzaakkuzu.musicplayer.util.ThemeStore
import com.beyzaakkuzu.musicplayer.util.ThemeStore.Companion.accentColor
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class AlbumFragment : Fragment(R.layout.fragment_album) {
    private var _binding: HomeBinding? = null
    private val binding get() = _binding!!
    val mainActivity: MainActivity
        get() = activity as MainActivity


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeBinding = FragmentAlbumBinding.bind(view)
        _binding = HomeBinding(homeBinding)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.title = null
        setupListeners()

        enterTransition = MaterialFadeThrough().addTarget(binding.contentContainer)
        reenterTransition = MaterialFadeThrough().addTarget(binding.contentContainer)


        loadProfile()
        setupTitle()
        colorButtons()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.appBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(requireContext())
        binding.toolbar.drawNextToNavbar()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            remove()
            requireActivity().onBackPressed()
        }
        view.doOnLayout {
            adjustPlaylistButtons()
        }
    }

    private fun adjustPlaylistButtons() {
        val buttons =
            listOf(binding.history, binding.lastAdded, binding.topPlayed, binding.actionShuffle)
        buttons.maxOf { it.lineCount }.let { maxLineCount ->
            buttons.forEach { button ->
                // Set the highest line count to every button for consistency
                button.setLines(maxLineCount)
            }
        }

    }



    private fun setupTitle() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_search, null, navOptions)
        }
        val hexColor = String.format("#%06X", 0xFFFFFF and accentColor())
        val appName = HtmlCompat.fromHtml(
            "Retro <span  style='color:$hexColor';>Music</span>",
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        binding.appNameText.text = appName
    }

    private fun loadProfile() {
        binding.bannerImage?.let {
            GlideApp.with(requireContext())
                .asBitmap()
                .profileBannerOptions(RetroGlideExtension.getBannerModel())
                .load(RetroGlideExtension.getBannerModel())
                .into(it)
        }
        GlideApp.with(requireActivity()).asBitmap()
            .userProfileOptions(RetroGlideExtension.getUserModel())
            .load(RetroGlideExtension.getUserModel())
            .into(binding.userImage)
    }

    fun colorButtons() {
        binding.history.elevatedAccentColor()
        binding.lastAdded.elevatedAccentColor()
        binding.topPlayed.elevatedAccentColor()
        binding.actionShuffle.elevatedAccentColor()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        menu.removeItem(R.id.action_grid_size)
        menu.removeItem(R.id.action_layout_type)
        menu.removeItem(R.id.action_sort_order)
        menu.findItem(R.id.action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireContext(),
            binding.toolbar,
            menu,
            ATHToolbarActivity.getToolbarBackgroundColor(binding.toolbar)
        )
        //Setting up cast button
        CastButtonFactory.setUpMediaRouteButton(requireContext(), menu, R.id.action_cast)
    }

    fun scrollToTop() {
        binding.container.scrollTo(0, 0)
        binding.appBarLayout.setExpanded(true)
    }

    fun setSharedAxisXTransitions() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            addTarget(binding.root)
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    private fun setSharedAxisYTransitions() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
            addTarget(binding.root)
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    private fun loadSuggestions(songs: List<SongModel>) {
        if (songs.isEmpty()) {
            binding.suggestions.root.isVisible = false
            return
        }
        val images = listOf(
            binding.suggestions.image1,
            binding.suggestions.image2,
            binding.suggestions.image3,
            binding.suggestions.image4,
            binding.suggestions.image5,
            binding.suggestions.image6,
            binding.suggestions.image7,
            binding.suggestions.image8
        )
        val color = ThemeStore.accentColor(requireContext())
        binding.suggestions.message.apply {
            setTextColor(color)
            setOnClickListener {
                it.isClickable = false
                it.postDelayed({ it.isClickable = true }, 500)
                MusicPlayerRemote.playNext(songs.subList(0, 8))
                if (!MusicPlayerRemote.isPlaying) {
                    MusicPlayerRemote.playNextSong()
                }
            }
        }
        binding.suggestions.card6.setCardBackgroundColor(ColorUtil.withAlpha(color, 0.12f))
        images.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                it.isClickable = false
                it.postDelayed({ it.isClickable = true }, 500)
                MusicPlayerRemote.playNext(songs[index])
                if (!MusicPlayerRemote.isPlaying) {
                    MusicPlayerRemote.playNextSong()
                }
            }
            GlideApp.with(this)
                .asBitmap()
                .songCoverOptions(songs[index])
                .load(RetroGlideExtension.getSongModel(songs[index]))
                .into(imageView)
        }
    }

    companion object {

        const val TAG: String = "BannerHomeFragment"

        @JvmStatic
        fun newInstance(): AlbumFragment {
            return AlbumFragment()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> findNavController().navigate(
                R.id.settingsActivity,
                null,
                navOptions
            )
            R.id.action_import_playlist -> ImportPlaylistDialog().show(
                childFragmentManager,
                "ImportPlaylist"
            )
            R.id.action_add_to_playlist -> CreatePlaylistDialog.create(emptyList()).show(
                childFragmentManager,
                "ShowCreatePlaylistDialog"
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(requireActivity(), binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        libraryViewModel.forceReload(ReloadType.HomeSections)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
