package com.example.ecobank2

import com.google.firebase.Timestamp


data class TransactionData(
    var id: String = " ",
    var pointsEarned: Int = 0,
    var amount: Double = 0.0,
    val userId: String = "",
    val type: String = "",
    var timestamp: Timestamp = Timestamp.now()
)

