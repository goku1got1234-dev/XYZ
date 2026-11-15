package com.mi7.guild.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mi7.guild.ui.auth.AuthViewModel
import com.mi7.guild.ui.theme.*

@Composable
private fun BalanceCard(bal: Double) = Card(colors = CardDefaults.cardColors(containerColor = CharcoalHigh), modifier = Modifier.fillMaxWidth()) {
    Column(Modifier.padding(20.dp)) {
        Text("Balance", color = TextMuted)
        Text("PKR %.2f".format(bal), style = MaterialTheme.typography.headlineLarge, color = Gold)
    }
}

@Composable
fun HomeScreen(vm: AuthViewModel) {
    val p by vm.profile.collectAsState()
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Hey, ${p?.ffName ?: p?.name ?: "Player"}", style = MaterialTheme.typography.headlineMedium)
        BalanceCard(p?.bal ?: 0.0)
    }
}

@Composable
fun WalletScreen(vm: AuthViewModel) {
    val p by vm.profile.collectAsState(); val txs by vm.txs.collectAsState()
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { BalanceCard(p?.bal ?: 0.0); Spacer(Modifier.height(8.dp)); Text("Transactions", style = MaterialTheme.typography.titleMedium) }
        if (txs.isEmpty()) item { Text("No transactions yet.", color = TextMuted) }
        items(txs) { t ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text(t.type.replace('_', ' ').replaceFirstChar { it.uppercase() }); t.note?.let { Text(it, color = TextMuted, style = MaterialTheme.typography.bodySmall) } }
                Text("%+.2f".format(t.amount), color = if (t.amount >= 0) Gold else NeonRed)
            }
        }
    }
}

@Composable
fun ProfileScreen(vm: AuthViewModel) {
    val p by vm.profile.collectAsState()
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(p?.ffName ?: "", style = MaterialTheme.typography.headlineMedium)
        listOf("Free Fire UID" to p?.ffUid, "Level" to p?.ffLevel?.toString(), "Phone" to p?.phone, "Referral code" to p?.referralCode)
            .forEach { (k, v) -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(k, color = TextMuted); Text(v ?: "-") } }
        Spacer(Modifier.weight(1f))
        OutlinedButton({ vm.logout() }, Modifier.fillMaxWidth().height(52.dp)) { Text("Log out") }
    }
}
