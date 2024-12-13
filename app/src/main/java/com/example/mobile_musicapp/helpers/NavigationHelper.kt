package com.example.mobile_musicapp.helpers

import android.view.MenuItem
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView

object NavigationHelper {

    fun BottomNavigationView.setupWithNavControllerCustom(navController: NavController) {
        setOnItemSelectedListener { menuItem ->
            onNavDestinationSelectedCustom(menuItem, navController)
            true
        }
    }

    private fun onNavDestinationSelectedCustom(
        item: MenuItem,
        navController: NavController
    ): Boolean {
        return try {
            navController.navigate(item.itemId)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
