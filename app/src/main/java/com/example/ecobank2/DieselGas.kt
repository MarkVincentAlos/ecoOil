package com.example.ecobank2

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecobank2.databinding.FragmentDieselGasBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class DieselGas : Fragment() {

    private var _binding: FragmentDieselGasBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private var priceListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDieselGasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPrices()
    }

    private fun loadPrices() {
        priceListener = db.collection("prices").document("current_prices")
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Log.e("DieselGasFragment", "Firestore error: ${error.message}")
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    val dieselPrice = document.getDouble("diesel_price") ?: 0.0
                    val gasolinePrice = document.getDouble("gasoline_price") ?: 0.0
                    val dieselChange = document.getDouble("diesel_change") ?: 0.0
                    val gasolineChange = document.getDouble("gasoline_change") ?: 0.0

                    // Update Diesel Price Digits
                    updatePriceDigits(
                        price = dieselPrice,
                        digit1 = binding.dieselDigit1,
                        digit2 = binding.dieselDigit2,
                        digit3 = binding.dieselDigit3,
                        digit4 = binding.dieselDigit4
                    )

                    // Update Gasoline Price Digits
                    updatePriceDigits(
                        price = gasolinePrice,
                        digit1 = binding.gasolineDigit1,
                        digit2 = binding.gasolineDigit2,
                        digit3 = binding.gasolineDigit3,
                        digit4 = binding.gasolineDigit4
                    )

                    // Update Diesel Icon & Change Text with Color
                    updatePriceChange(binding.dieselIcon, binding.dieselChangeText, dieselChange)

                    // Update Gasoline Icon & Change Text with Color
                    updatePriceChange(binding.gasolineIcon, binding.gasolineChangeText, gasolineChange)
                } else {
                    Log.e("DieselGasFragment", "No price data found.")
                }
            }
    }

    private fun updatePriceDigits(
        price: Double,
        digit1: TextView,
        digit2: TextView,
        digit3: TextView,
        digit4: TextView
    ) {
        val formattedPrice = "%.2f".format(price) // Example: "45.89"

        if (formattedPrice.length == 5) { // Ensure proper formatting
            digit1.text = formattedPrice[0].toString() // First digit
            digit2.text = formattedPrice[1].toString() // Second digit
            digit3.text = formattedPrice[3].toString() // Third digit (after dot)
            digit4.text = formattedPrice[4].toString() // Fourth digit (after dot)
        }
    }

    private fun updatePriceChange(iconView: ImageView, changeTextView: TextView, priceChange: Double) {
        val context = requireContext() // Get the context for using ContextCompat

        // Set icon based on price change
        val iconDrawable: Drawable? = when {
            priceChange > 0 -> ContextCompat.getDrawable(context, R.drawable.increase)
            priceChange < 0 -> ContextCompat.getDrawable(context, R.drawable.decrease)
            else -> null
        }
        iconView.setImageDrawable(iconDrawable)
        iconView.visibility = if (iconDrawable != null) View.VISIBLE else View.GONE

        // Set the price change text
        if (priceChange != 0.0) {
            val sign = if (priceChange > 0) "+" else "" // Add "+" if increase
            changeTextView.text = String.format("%s%.2f", sign, priceChange)

            // Change text color based on increase/decrease
            val textColor = when {
                priceChange > 0 -> ContextCompat.getColor(context, R.color.green)  // 🟢 Green for increase
                priceChange < 0 -> ContextCompat.getColor(context, R.color.red)    // 🔴 Red for decrease
                else -> ContextCompat.getColor(context, R.color.black)              // ⚪ Gray for no change
            }
            changeTextView.setTextColor(textColor)
            changeTextView.visibility = View.VISIBLE
        } else {
            changeTextView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        priceListener?.remove() // Remove Firestore listener to prevent memory leaks
        _binding = null
    }
}
