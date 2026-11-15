package com.mi7.guild.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val name: String? = null,
    val phone: String? = null,
    @SerialName("ff_uid") val ffUid: String? = null,
    @SerialName("ff_name") val ffName: String? = null,
    @SerialName("ff_level") val ffLevel: Int? = null,
    val bal: Double = 0.0,
    @SerialName("is_admin") val isAdmin: Boolean = false,
    @SerialName("referral_code") val referralCode: String? = null,
    val status: String = "active",
    @SerialName("app_registered") val appRegistered: Boolean = false
)

@Serializable
data class Tx(
    val id: Long,
    val type: String,
    val amount: Double,
    val note: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
