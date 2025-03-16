package com.example.ecobank2

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlin.math.max

class AdminActivity : AppCompatActivity() {

    private lateinit var userIdSpinner: Spinner
    private lateinit var amountInput: EditText
    private lateinit var transactionTypeSpinner: Spinner
    private lateinit var submitButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val userMap = mutableMapOf<String, String>() // Maps username -> userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        userIdSpinner = findViewById(R.id.user_id_spinner)
        amountInput = findViewById(R.id.input_amount)
        transactionTypeSpinner = findViewById(R.id.input_type)
        submitButton = findViewById(R.id.btn_submit_transaction)

        fetchUsernames() // Fetch usernames dynamically from Firestore
        setupTransactionTypeSpinner()

        submitButton.setOnClickListener { submitTransaction() }
    }

    // Fetch usernames from Firestore and store their names & userIds
    private fun fetchUsernames() {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                userMap.clear()
                val userNames = mutableListOf<String>()

                for (document in documents) {
                    val userName = document.getString("username") ?: document.id // Get username field or use UID
                    val userId = document.id
                    userMap[userName] = userId
                    userNames.add(userName)
                }
                updateUserIdSpinner(userNames)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load users: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Populate the User ID Spinner with usernames
    private fun updateUserIdSpinner(userNames: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userIdSpinner.adapter = adapter
    }

    // Setup Transaction Type Spinner (Earned or Used)
    private fun setupTransactionTypeSpinner() {
        val transactionTypes = arrayOf("Earned", "Used")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, transactionTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        transactionTypeSpinner.adapter = adapter
    }

    // Handle Submit Button Click
    private fun submitTransaction() {
        val selectedUserName = userIdSpinner.selectedItem?.toString()
        val amount = amountInput.text.toString().toDoubleOrNull()
        val transactionType = transactionTypeSpinner.selectedItem?.toString()

        if (selectedUserName.isNullOrEmpty() || amount == null || transactionType.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = userMap[selectedUserName]
        if (userId != null) {
            processTransaction(userId, amount, transactionType, selectedUserName)
        } else {
            Toast.makeText(this, "User ID not found for this username", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processTransaction(userId: String, amount: Double, transactionType: String, selectedUserName: String) {
        val pointsEarned = calculatePoints(amount)
        val userRef = db.collection("users").document(userId)

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            if (userSnapshot.exists()) {
                val currentPoints = userSnapshot.getLong("points") ?: 0

                val newPoints = when (transactionType) {
                    "Earned" -> currentPoints + pointsEarned
                    "Used" -> max(currentPoints - pointsEarned, 0) // Ensure points never go negative
                    else -> currentPoints
                }

                transaction.update(userRef, "points", newPoints)

                val transactionData = hashMapOf(
                    "userId" to userId,
                    "userName" to selectedUserName,
                    "amount" to amount,
                    "pointsEarned" to pointsEarned,
                    "type" to transactionType,
                    "timestamp" to Timestamp.now()
                )

                db.collection("transactions").add(transactionData)
            } else {
                throw Exception("User not found!")
            }
        }.addOnSuccessListener {
            Toast.makeText(this, "Transaction added! Points Updated.", Toast.LENGTH_SHORT).show()
            amountInput.text.clear()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error updating points: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Calculate Points Based on Transaction Type
    private fun calculatePoints(amount: Double): Int {
        return (amount * 0.1).toInt() // 10% of the amount as points
    }
}
