package com.mi7.guild.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object Repo {
    private val c get() = Supabase.client

    /** Login accepts an email or a phone number (phone is resolved via the same RPC the website uses). */
    suspend fun login(idOrPhone: String, password: String) {
        val id = idOrPhone.trim()
        val email = if ("@" in id) id else
            c.postgrest.rpc("mi7_email_by_phone", buildJsonObject { put("p", id) }).decodeAs<String?>()
                ?: error("No account found for this phone number")
        c.auth.signInWith(Email) { this.email = email; this.password = password }
    }

    /** Returns true if a session exists right away (false = email confirmation is required). */
    suspend fun signUp(name: String, phone: String, email: String, password: String): Boolean {
        c.auth.signUpWith(Email) {
            this.email = email.trim(); this.password = password
            data = buildJsonObject { put("name", name.trim()); put("phone", phone.trim()) }
        }
        return c.auth.currentSessionOrNull() != null
    }

    suspend fun logout() = c.auth.signOut()

    suspend fun myProfile(): Profile {
        val uid = c.auth.currentUserOrNull()?.id ?: error("Not signed in")
        return c.from("mi7_profiles").select { filter { eq("id", uid) } }.decodeSingle()
    }

    suspend fun completeSetup(
        ffUid: String, ign: String, level: Int, phone: String, email: String,
        question: String, answer: String, referral: String
    ) {
        c.postgrest.rpc("mi7_app_register_profile", buildJsonObject {
            put("p_ff_uid", ffUid); put("p_ff_name", ign); put("p_ff_level", level)
            put("p_phone", phone); put("p_email", email)
            put("p_sec_q", question); put("p_sec_a", answer)
            if (referral.isNotBlank()) put("p_ref", referral)
        })
    }

    suspend fun myTransactions(): List<Tx> =
        c.from("mi7_transactions").select {
            order("created_at", Order.DESCENDING); limit(30)
        }.decodeList()

    fun myEmail(): String = c.auth.currentUserOrNull()?.email ?: ""
}

/** Turns backend errors into readable messages. */
fun friendlyError(e: Throwable): String {
    val m = e.message.orEmpty()
    return when {
        "mi7_profiles_ff_uid_uq" in m -> "This Free Fire UID is already registered."
        "mi7_profiles_phone_uq" in m -> "This phone number is already registered."
        "already registered" in m && "User" in m -> "This email is already registered. Please log in."
        "Invalid login credentials" in m -> "Wrong email/phone or password."
        "No account found" in m -> "No account found for this phone number."
        "network" in m.lowercase() || "Unable to resolve" in m -> "No internet connection."
        else -> m.lines().firstOrNull()?.take(140)?.ifBlank { null } ?: "Something went wrong. Try again."
    }
}
