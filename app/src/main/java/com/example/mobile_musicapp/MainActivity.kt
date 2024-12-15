package com.example.mobile_musicapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.mobile_musicapp.helpers.NavigationHelper.setupWithNavControllerCustom
import com.example.mobile_musicapp.services.PlayerManager
import com.example.mobile_musicapp.singletons.Favorite
import com.example.mobile_musicapp.viewModels.FavoritesViewModel
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

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
            val playerBarFragmentContainer = findViewById<View>(R.id.player_bar_container) // Ensure correct ID
            when (destination.id) {
                R.id.playMusicFragment, R.id.newPlaylistFragment -> {
                    bottomNavigationView.visibility = View.GONE
                    playerBarFragmentContainer?.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    playerBarFragmentContainer?.visibility = View.VISIBLE
                    // Ensure previousMenuItem is checked
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
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerManager.stop()
    }
}
