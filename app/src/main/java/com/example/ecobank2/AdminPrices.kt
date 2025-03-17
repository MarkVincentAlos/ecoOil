package com.example.ecobank2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminPrices : AppCompatActivity() {

    private lateinit var editTextDieselChange: EditText
    private lateinit var editTextGasolineChange: EditText
    private lateinit var spinnerDieselChange: Spinner
    private lateinit var spinnerGasolineChange: Spinner
    private lateinit var buttonSubmit: Button
    private lateinit var goBack: Button
    private lateinit var dieselIcon: ImageView
    private lateinit var gasolineIcon: ImageView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_prices)

        // Initialize UI elements
        editTextDieselChange = findViewById(R.id.editTextDieselChange)
        editTextGasolineChange = findViewById(R.id.editTextGasolineChange)
        spinnerDieselChange = findViewById(R.id.spinnerDieselChange)
        spinnerGasolineChange = findViewById(R.id.spinnerGasolineChange)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        goBack = findViewById(R.id.back)
        dieselIcon = findViewById(R.id.dieselIcon)
        gasolineIcon = findViewById(R.id.gasolineIcon)

        // Set up dropdown menus (spinners)
        setupSpinners()

        buttonSubmit.setOnClickListener {
            submitPrices()
        }

        goBack.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupSpinners() {
        val options = arrayOf("Select Change", "Increase", "Decrease")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerDieselChange.adapter = adapter
        spinnerGasolineChange.adapter = adapter
    }

    private fun submitPrices() {
        val dieselChangeAmount = editTextDieselChange.text.toString().toDoubleOrNull()
        val gasolineChangeAmount = editTextGasolineChange.text.toString().toDoubleOrNull()

        val dieselChangeType = spinnerDieselChange.selectedItem.toString()
        val gasolineChangeType = spinnerGasolineChange.selectedItem.toString()

        if (dieselChangeAmount != null && gasolineChangeAmount != null &&
            dieselChangeType != "Select Change" && gasolineChangeType != "Select Change") {

            val timestamp = System.currentTimeMillis() // Get current time

            db.collection("prices").document("current_prices")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val currentDieselPrice = document.getDouble("diesel_price") ?: 0.0
                        val currentGasolinePrice = document.getDouble("gasoline_price") ?: 0.0

                        // Determine price changes (negative for decreases)
                        val dieselChangeValue = if (dieselChangeType == "Increase") dieselChangeAmount else -dieselChangeAmount
                        val gasolineChangeValue = if (gasolineChangeType == "Increase") gasolineChangeAmount else -gasolineChangeAmount

                        // Calculate new prices
                        val newDieselPrice = currentDieselPrice + dieselChangeValue
                        val newGasolinePrice = currentGasolinePrice + gasolineChangeValue

                        // Prepare updated price data
                        val pricesData = hashMapOf(
                            "diesel_price" to newDieselPrice,
                            "gasoline_price" to newGasolinePrice,
                            "diesel_change" to dieselChangeValue,  // Shows + for increase, - for decrease
                            "gasoline_change" to gasolineChangeValue,
                            "timestamp" to timestamp
                        )

                        // Update Firestore (overwrite current prices)
                        db.collection("prices").document("current_prices")
                            .set(pricesData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Prices updated successfully", Toast.LENGTH_SHORT).show()
                                updateIcons(dieselChangeType, gasolineChangeType)
                                clearInputs()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error updating prices: ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        Toast.makeText(this, "Current prices not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching current prices: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please enter valid changes and select options", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateIcons(dieselChange: String, gasolineChange: String) {
        println("Diesel Change: $dieselChange")  // Debug log
        println("Gasoline Change: $gasolineChange")  // Debug log

        // Update diesel icon
        when (dieselChange) {
            "Increase" -> {
                dieselIcon.setImageResource(R.drawable.increase)
                dieselIcon.visibility = View.VISIBLE
            }
            "Decrease" -> {
                dieselIcon.setImageResource(R.drawable.decrease)
                dieselIcon.visibility = View.VISIBLE
            }
            else -> dieselIcon.visibility = View.GONE
        }

        // Update gasoline icon
        when (gasolineChange) {
            "Increase" -> {
                gasolineIcon.setImageResource(R.drawable.increase)
                gasolineIcon.visibility = View.VISIBLE
            }
            "Decrease" -> {
                gasolineIcon.setImageResource(R.drawable.decrease)
                gasolineIcon.visibility = View.VISIBLE
            }
            else -> gasolineIcon.visibility = View.GONE
        }
    }

    private fun clearInputs() {
        editTextDieselChange.text.clear()
        editTextGasolineChange.text.clear()
        spinnerDieselChange.setSelection(0)
        spinnerGasolineChange.setSelection(0)
    }
}
