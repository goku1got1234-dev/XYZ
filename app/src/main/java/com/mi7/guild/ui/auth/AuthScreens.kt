package com.mi7.guild.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.mi7.guild.ui.theme.*

@Composable
private fun Field(v: String, on: (String) -> Unit, label: String, kb: KeyboardType = KeyboardType.Text, pw: Boolean = false) =
    OutlinedTextField(
        value = v, onValueChange = on, label = { Text(label) }, singleLine = true,
        visualTransformation = if (pw) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = kb), modifier = Modifier.fillMaxWidth()
    )

@Composable
private fun Shell(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) =
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().navigationBarsPadding().imePadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("MI7 GUILD", color = NeonRed, style = MaterialTheme.typography.labelLarge)
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(subtitle, color = TextMuted)
        Spacer(Modifier.height(8.dp))
        content()
    }

@Composable
private fun Msg(vm: AuthViewModel) {
    val m by vm.message.collectAsState()
    m?.let { Text(it, color = if (it.startsWith("Account created")) Gold else NeonRed) }
}

@Composable
private fun Go(text: String, busy: Boolean, enabled: Boolean, onClick: () -> Unit) =
    Button(onClick, enabled = enabled && !busy, modifier = Modifier.fillMaxWidth().height(52.dp)) {
        if (busy) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White) else Text(text)
    }
private typealias Color = androidx.compose.ui.graphics.Color

@Composable
fun AuthScreen(vm: AuthViewModel) {
    var signup by remember { mutableStateOf(false) }
    var id by remember { mutableStateOf("") }; var pw by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }; var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val busy by vm.busy.collectAsState()

    Shell(if (signup) "Create account" else "Welcome back", if (signup) "Join tournaments, earn and play." else "Log in with your email or phone.") {
        if (signup) {
            Field(name, { name = it }, "Full name")
            Field(phone, { phone = it }, "Phone number", KeyboardType.Phone)
            Field(email, { email = it }, "Email", KeyboardType.Email)
        } else Field(id, { id = it }, "Email or phone", KeyboardType.Email)
        Field(pw, { pw = it }, "Password", pw = true)
        Msg(vm)
        if (signup) Go("Sign up", busy, name.isNotBlank() && phone.length >= 10 && "@" in email && pw.length >= 6) { vm.signUp(name, phone, email, pw) }
        else Go("Log in", busy, id.isNotBlank() && pw.isNotBlank()) { vm.login(id, pw) }
        TextButton({ signup = !signup }, Modifier.align(Alignment.CenterHorizontally)) {
            Text(if (signup) "Already have an account? Log in" else "New here? Create account", color = Gold)
        }
    }
}

@Composable
fun SetupScreen(vm: AuthViewModel) {
    val p by vm.profile.collectAsState()
    var uid by remember(p) { mutableStateOf(p?.ffUid.orEmpty()) }
    var ign by remember(p) { mutableStateOf(p?.ffName.orEmpty()) }
    var lvl by remember(p) { mutableStateOf(p?.ffLevel?.toString().orEmpty()) }
    var phone by remember(p) { mutableStateOf(p?.phone.orEmpty()) }
    var q by remember { mutableStateOf("What is your first school's name?") }
    var a by remember { mutableStateOf("") }; var ref by remember { mutableStateOf("") }
    val busy by vm.busy.collectAsState()

    Shell("Game setup", "Link your Free Fire account. This can't be used on another account.") {
        Field(uid, { uid = it }, "Free Fire UID", KeyboardType.Number)
        Field(ign, { ign = it }, "In-game name (IGN)")
        Field(lvl, { lvl = it.filter(Char::isDigit) }, "Level", KeyboardType.Number)
        Field(phone, { phone = it }, "Phone number", KeyboardType.Phone)
        Field(q, { q = it }, "Security question (for password recovery)")
        Field(a, { a = it }, "Your answer")
        Field(ref, { ref = it.uppercase() }, "Referral code (optional)")
        Msg(vm)
        Go("Finish setup", busy, uid.length >= 6 && ign.isNotBlank() && lvl.isNotBlank() && phone.length >= 10 && a.isNotBlank()) {
            vm.completeSetup(uid, ign, lvl.toIntOrNull() ?: 1, phone, q, a, ref)
        }
        TextButton({ vm.logout() }, Modifier.align(Alignment.CenterHorizontally)) { Text("Log out", color = TextMuted) }
    }
}

@Composable
fun BlockedScreen(vm: AuthViewModel) = Shell("Account restricted", "Your account is suspended or blocked. Contact MI7 admin support.") {
    Button({ vm.logout() }, Modifier.fillMaxWidth().height(52.dp)) { Text("Log out") }
}
