package com.example.ecobank2

import com.google.firebase.Timestamp

data class VoucherData(
    val id: String = "",
    val userId: String = "",
    val rewardTitle: String = "",
    val rewardPoints: Long = 0,
    val used: Boolean = false,
    var timestamp: Timestamp = Timestamp.now()
)