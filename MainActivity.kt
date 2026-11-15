package com.mi7.guild

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mi7.guild.ui.auth.*
import com.mi7.guild.ui.nav.AppNav
import com.mi7.guild.ui.theme.MI7Theme

class MainActivity : ComponentActivity() {
    private val vm: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MI7Theme {
                val gate by vm.gate.collectAsState()
                when (gate) {
                    Gate.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                    Gate.SignedOut -> AuthScreen(vm)
                    Gate.NeedsSetup -> SetupScreen(vm)
                    Gate.Blocked -> BlockedScreen(vm)
                    Gate.Ready -> AppNav(vm)
                }
            }
        }
    }
}
