package com.example.walletwise.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.walletwise.ui.screens.extras.AboutScreenDestination
import com.example.walletwise.ui.screens.extras.SettingScreenDestination
import com.example.walletwise.ui.screens.transaction.HistoryDestination

data class DrawerItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val DrawerListItem = listOf(
    DrawerItem(
        "Accounts",
        Icons.Filled.AccountBalanceWallet,
        "${HistoryDestination.route}/AccountType/0"
    ),
    DrawerItem(
        "Tags",
        Icons.Filled.Sell,
        "${HistoryDestination.route}/TagType/0"
    ),
    DrawerItem(
        "Time",
        Icons.Filled.Schedule,
        "${HistoryDestination.route}/Time/0"
    )
)

val GeneralDrawerItems = listOf(
    DrawerItem(
        "Settings",
        Icons.Filled.Settings,
        SettingScreenDestination.route
    ),
    DrawerItem(
        "About",
        Icons.Filled.Info,
        AboutScreenDestination.route
    )
)
