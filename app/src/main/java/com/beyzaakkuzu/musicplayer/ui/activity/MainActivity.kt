package com.beyzaakkuzu.musicplayer.ui.activity

import android.annotation.TargetApi
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.beyzaakkuzu.musicplayer.R
import com.beyzaakkuzu.musicplayer.databinding.ActivityMainBinding
import com.beyzaakkuzu.musicplayer.model.SongModel
import com.beyzaakkuzu.musicplayer.repository.SongRepository
import com.beyzaakkuzu.musicplayer.ui.SongViewModel
import com.beyzaakkuzu.musicplayer.ui.SongViewModelFactory
import com.beyzaakkuzu.musicplayer.ui.fragments.PlayerFragment
import com.beyzaakkuzu.musicplayer.util.Constants.PREF_NAME
import com.beyzaakkuzu.musicplayer.util.MusicPlayerRemote
import com.beyzaakkuzu.musicplayer.util.PlayerHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private val currentSong get() = PlayerHelper.getCurrentSong(sharedPreferences)

    private val panelState: Int
        get() = bottomSheetBehavior.state

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    binding.dimBackground.visibility = View.GONE
                }
                else -> {
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            setMiniPlayerAlpha(slideOffset)
            setBottomNavigationViewTransition(slideOffset)
            binding.dimBackground.visibility = View.VISIBLE
            binding.dimBackground.alpha = slideOffset
        }
    }

    private var serviceToken: MusicPlayerRemote.ServiceToken? = null
    private lateinit var viewModel: SongViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        @TargetApi(29)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //make fullscreen, so we can draw behind status bar
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            //make status bar color transparent
            window.statusBarColor = Color.TRANSPARENT
        }



        //setting up custom status bar height
        statusBar.layoutParams.height = getStatusBarHeight(resources)

        val repository = SongRepository(this)
        val viewModelFactory = SongViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SongViewModel::class.java]

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        serviceToken = MusicPlayerRemote.bindToService(this, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                reloadPlayerFragment()
                if (MusicPlayerRemote.playerService?.mediaPlayer == null && currentSong != null) {
                    MusicPlayerRemote.playerService?.initMediaPlayer(currentSong!!.path)
                    viewModel.songLiveData.observe(this@MainActivity) {
                        if (it.isNotEmpty()) {
                            MusicPlayerRemote.sendAllSong(it as MutableList<SongModel>, -1)

                        } else {
                            MusicPlayerRemote.sendAllSong(
                                emptyList<SongModel>() as MutableList<SongModel>,
                                -1
                            )
                        }
                    }
                }

            }

            override fun onServiceDisconnected(name: ComponentName) {
            }
        })

        setUpBottomNavigationNavController()

        bottomSheetBehavior = BottomSheetBehavior.from(slidingPanel)
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        miniPlayerFragment?.view?.setOnClickListener {
            expandPanel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
        //MusicPlayerRemote.unbindFromService(serviceToken)
    }

    override fun onBackPressed() {
        if (!handleBackPress()) super.onBackPressed()
    }

    open fun handleBackPress(): Boolean {
        if (panelState == BottomSheetBehavior.STATE_EXPANDED) {
            collapsePanel()
            return true
        }
        return false
    }

    private fun collapsePanel() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        setMiniPlayerAlpha(0f)
    }

    private fun expandPanel() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        setMiniPlayerAlpha(1f)
    }

    fun reloadPlayerFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.playerFragmentContainer, PlayerFragment())
            .commit()
    }

    private fun getStatusBarHeight(r: Resources): Int {
        var result = 0
        val resourceId: Int = r.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = r.getDimensionPixelSize(resourceId)
        }
        return result
    }


    private fun setMiniPlayerAlpha(slideOffset: Float) {
        val alpha = 1 - slideOffset
        miniPlayerFragment?.view?.alpha = alpha
        miniPlayerFragment?.view?.visibility = if (alpha == 0f) View.GONE else View.VISIBLE
    }

    private fun setBottomNavigationViewTransition(slideOffset: Float) {
        bottomNavigationView.translationY = slideOffset * 500
    }

    private fun setUpBottomNavigationNavController() {
        bottomNavigationView.setupWithNavController(musicNavHostFragment.findNavController())
    }
}