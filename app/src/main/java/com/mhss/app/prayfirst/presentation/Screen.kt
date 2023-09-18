package com.mhss.app.prayfirst.presentation

import com.mhss.app.prayfirst.R

sealed class Screen(val route: String, val titleRes: Int, val iconRes: Int, val iconSelectedres: Int) {
    // TODO(): Add screen icons
    data object Main: Screen(
        "main_screen",
        R.string.main_screen_title,
        R.drawable.home,
        R.drawable.home_fill
    )
    data object Settings: Screen(
        "settings_screen",
        R.string.settings_screen_title,
        R.drawable.settings,
        R.drawable.settings_fill
    )
}
