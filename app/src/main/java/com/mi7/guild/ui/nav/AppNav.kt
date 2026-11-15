package com.mi7.guild.ui.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.mi7.guild.ui.theme.*

enum class Tab(val route: String, val label: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Filled.SportsEsports),
    Tournaments("tournaments", "Matches", Icons.Filled.EmojiEvents),
    Wallet("wallet", "Wallet", Icons.Filled.AccountBalanceWallet),
    Market("market", "Market", Icons.Filled.VerifiedUser),
    Profile("profile", "Profile", Icons.Filled.Person)
}

@Composable
fun AppNav(vm: com.mi7.guild.ui.auth.AuthViewModel) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    Scaffold(
        containerColor = OledBlack,
        bottomBar = {
            NavigationBar(containerColor = Charcoal) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = current == tab.route,
                        onClick = {
                            nav.navigate(tab.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonRed, selectedTextColor = NeonRed,
                            indicatorColor = CharcoalHigh,
                            unselectedIconColor = TextMuted, unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = Tab.Home.route, modifier = Modifier.padding(pad)) {
            composable(Tab.Home.route) { com.mi7.guild.ui.screens.HomeScreen(vm) }
            composable(Tab.Wallet.route) { com.mi7.guild.ui.screens.WalletScreen(vm) }
            composable(Tab.Profile.route) { com.mi7.guild.ui.screens.ProfileScreen(vm) }
            composable(Tab.Tournaments.route) { Placeholder(Tab.Tournaments) }
            composable(Tab.Market.route) { Placeholder(Tab.Market) }
        }
    }
}

// Replaced screen by screen in later phases.
@Composable
private fun Placeholder(tab: Tab) = Box(Modifier.fillMaxSize(), Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(tab.icon, null, tint = Gold, modifier = Modifier.size(48.dp))
        Text(tab.label, style = MaterialTheme.typography.titleLarge)
    }
}
