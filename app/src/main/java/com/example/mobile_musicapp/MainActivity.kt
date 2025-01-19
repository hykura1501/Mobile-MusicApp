package com.example.mobile_musicapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.mobile_musicapp.helpers.NavigationHelper.setupWithNavControllerCustom
import com.example.mobile_musicapp.models.SongListWithIndex
import com.example.mobile_musicapp.services.PlayerManager
import com.example.mobile_musicapp.services.SongDao
import com.example.mobile_musicapp.services.UserManager
import com.example.mobile_musicapp.singletons.Favorite
import com.example.mobile_musicapp.singletons.Queue
import com.example.mobile_musicapp.viewModels.FavoritesViewModel
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import com.facebook.FacebookSdk
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPaySDK

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val favoritesViewModel: FavoritesViewModel by viewModels()
    private val playerBarViewModel: PlayerBarViewModel by viewModels()
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var previousMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle deep link if the activity is launched via a link
        handleIntent(intent)

        // Fetch favorite songs on app start
        Favorite.fetchFavoriteSongs(favoritesViewModel)

        // Set up navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        previousMenuItem = bottomNavigationView.menu.findItem(R.id.homeFragment)
        bottomNavigationView.setupWithNavControllerCustom(navController)

        // Ensure the correct item is selected and toggle visibility when navigating
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val playerBarFragmentContainer = findViewById<View>(R.id.player_bar_container)
            when (destination.id) {
                R.id.playMusicFragment, R.id.newPlaylistFragment, R.id.login, R.id.register -> {
                    bottomNavigationView.visibility = View.GONE
                    playerBarFragmentContainer?.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    playerBarFragmentContainer?.visibility = View.VISIBLE
                    previousMenuItem.isChecked = false
                    val menuItem = bottomNavigationView.menu.findItem(destination.id)
                    if (menuItem != null) {
                        menuItem.isChecked = true
                        previousMenuItem = menuItem
                    }
                }
            }
        }

        // Add back pressed callback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.navigateUp()) {
                    finish()
                }
            }
        })

        PlayerManager.initialize(playerBarViewModel)
        Queue.initialize(playerBarViewModel)

        FacebookSdk.sdkInitialize(this)

        ZaloPaySDK.init(553, Environment.SANDBOX)

        // Create notification channel
        createNotificationChannel()

        lifecycleScope.launch {
            UserManager.fetchCurrentUser()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
//        handleIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }


    private fun handleIntent(intent: Intent) {
        val action = intent.action
        val data: Uri? = intent.data

        if (Intent.ACTION_VIEW == action && data != null) {
            val songId = data.lastPathSegment
            Log.d("MainActivity", "Action: $action, Data: $data, Song ID: $songId")
            if (songId != null) {
                fetchAndOpenSong(songId)
            }
        }
    }

    private fun fetchAndOpenSong(songId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val song = SongDao.getSongById(songId)
                withContext(Dispatchers.Main) {
                    if (song != null) {
                        Toast.makeText(this@MainActivity, "Opening song: ${song.title} by ${song.artistName}", Toast.LENGTH_LONG).show()
                        // Create the SongListWithIndex object
                        val songListWithIndex = SongListWithIndex(songs = listOf(song), selectedIndex = 0)
                        // Navigate to PlayMusicFragment
                        navigateToPlayMusicFragment(songListWithIndex)
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to fetch song", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToPlayMusicFragment(songListWithIndex: SongListWithIndex) {
        val navController = findNavController(R.id.nav_host_fragment)
        val bundle = Bundle().apply {
            putParcelable("songListWithIndex", songListWithIndex)
        }
        navController.navigate(R.id.playMusicFragment, bundle)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerManager.stop()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "media_playback_channel",
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media playback controls"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
