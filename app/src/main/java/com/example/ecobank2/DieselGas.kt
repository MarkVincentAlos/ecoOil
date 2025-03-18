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
                    val dieselChange = document.getDouble("diesel_change") ?: 0.0
                    val gasolineChange = document.getDouble("gasoline_change") ?: 0.0

                    // Update Diesel Price Change Digits
                    updatePriceDigits(
                        change = dieselChange,
                        digit1 = binding.dieselDigit1,
                        digit2 = binding.dieselDigit2,
                        digit3 = binding.dieselDigit3,
                        digit4 = binding.dieselDigit4
                    )

                    // Update Gasoline Price Change Digits
                    updatePriceDigits(
                        change = gasolineChange,
                        digit1 = binding.gasolineDigit1,
                        digit2 = binding.gasolineDigit2,
                        digit3 = binding.gasolineDigit3,
                        digit4 = binding.gasolineDigit4
                    )

                    // Update Diesel Icon
                    updatePriceIcon(binding.dieselIcon, dieselChange)

                    // Update Gasoline Icon
                    updatePriceIcon(binding.gasolineIcon, gasolineChange)
                } else {
                    Log.e("DieselGasFragment", "No price data found.")
                }
            }
    }

    private fun updatePriceDigits(
        change: Double,
        digit1: TextView,
        digit2: TextView,
        digit3: TextView,
        digit4: TextView
    ) {
        val formattedChange = "%.2f".format(change).replace("+", "").replace("-", "") // Ensure two decimal places
        val cleanChange = formattedChange.padStart(5, '0') // Ensure it has at least 5 characters

        digit1.text = cleanChange[0].toString() // First digit
        digit2.text = cleanChange[1].toString() // Second digit
        digit3.text = cleanChange[3].toString() // Third digit (after dot)
        digit4.text = cleanChange[4].toString() // Fourth digit (after dot)
    }

    private fun updatePriceIcon(iconView: ImageView, priceChange: Double) {
        val context = requireContext() // Get the context for using ContextCompat

        // Set icon based on price change
        val iconDrawable: Drawable? = when {
            priceChange > 0 -> ContextCompat.getDrawable(context, R.drawable.increase)
            priceChange < 0 -> ContextCompat.getDrawable(context, R.drawable.decrease)
            else -> null
        }
        iconView.setImageDrawable(iconDrawable)
        iconView.visibility = if (iconDrawable != null) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        priceListener?.remove() // Remove Firestore listener to prevent memory leaks
        _binding = null
    }
}
