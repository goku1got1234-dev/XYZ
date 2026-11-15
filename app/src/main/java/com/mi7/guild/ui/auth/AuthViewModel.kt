package com.mi7.guild.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mi7.guild.data.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface Gate {
    data object Loading : Gate
    data object SignedOut : Gate
    data object NeedsSetup : Gate
    data object Blocked : Gate
    data object Ready : Gate
}

class AuthViewModel : ViewModel() {
    private val _gate = MutableStateFlow<Gate>(Gate.Loading)
    val gate = _gate.asStateFlow()
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile = _profile.asStateFlow()
    private val _txs = MutableStateFlow<List<Tx>>(emptyList())
    val txs = _txs.asStateFlow()
    val busy = MutableStateFlow(false)
    val message = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            Supabase.client.auth.sessionStatus.collect { s ->
                when (s) {
                    is SessionStatus.Authenticated -> refresh()
                    is SessionStatus.NotAuthenticated -> { _profile.value = null; _gate.value = Gate.SignedOut }
                    else -> Unit
                }
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        try {
            val p = Repo.myProfile()
            _profile.value = p
            _gate.value = when {
                p.status != "active" -> Gate.Blocked
                !p.appRegistered -> Gate.NeedsSetup
                else -> Gate.Ready
            }
            if (_gate.value == Gate.Ready) _txs.value = runCatching { Repo.myTransactions() }.getOrDefault(emptyList())
        } catch (e: Throwable) { message.value = friendlyError(e); _gate.value = Gate.SignedOut }
    }

    private fun run(block: suspend () -> Unit) = viewModelScope.launch {
        busy.value = true; message.value = null
        try { block() } catch (e: Throwable) { message.value = friendlyError(e) }
        busy.value = false
    }

    fun login(id: String, pw: String) = run { Repo.login(id, pw) }

    fun signUp(name: String, phone: String, email: String, pw: String) = run {
        val hasSession = Repo.signUp(name, phone, email, pw)
        if (!hasSession) message.value = "Account created. Confirm your email, then log in."
    }

    fun completeSetup(ffUid: String, ign: String, level: Int, phone: String, q: String, a: String, ref: String) = run {
        Repo.completeSetup(ffUid.trim(), ign.trim(), level, phone.trim(), Repo.myEmail(), q, a, ref)
        refresh()
    }

    fun logout() = viewModelScope.launch { Repo.logout() }
}
