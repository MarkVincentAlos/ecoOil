package com.example.ecobank2

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

                    // Update UI with latest prices
                    binding.dieselPrice.text = "₱${"%.2f".format(dieselPrice)}"
                    binding.gasolinePrice.text = "₱${"%.2f".format(gasolinePrice)}"

                    // Update icons and text colors
                    updatePriceChangeIcon(binding.dieselIcon, dieselChange)
                    updatePriceChangeIcon(binding.gasolineIcon, gasolineChange)

                    updatePriceTextColor(binding.dieselPrice, dieselChange)
                    updatePriceTextColor(binding.gasolinePrice, gasolineChange)
                } else {
                    Log.e("DieselGasFragment", "No price data found.")
                }
            }
    }

    private fun updatePriceChangeIcon(iconView: View, priceChange: Double) {
        Log.d("DieselGasFragment", "Updating icon: Price Change = $priceChange")

        val iconDrawable: Drawable? = when {
            priceChange > 0 -> ContextCompat.getDrawable(requireContext(), R.drawable.increase) // Increase icon
            priceChange < 0 -> ContextCompat.getDrawable(requireContext(), R.drawable.decrease) // Decrease icon
            else -> null // No change, hide icon
        }

        (iconView as? android.widget.ImageView)?.apply {
            setImageDrawable(iconDrawable)
            visibility = if (iconDrawable != null) View.VISIBLE else View.GONE
        }
    }

    private fun updatePriceTextColor(priceTextView: TextView, priceChange: Double) {
        val context: Context = priceTextView.context
        val color = when {
            priceChange > 0 -> ContextCompat.getColor(context, R.color.green) // Increase -> Green
            priceChange < 0 -> ContextCompat.getColor(context, R.color.red)   // Decrease -> Red
            else -> Color.BLACK // No change -> Black
        }
        priceTextView.setTextColor(color)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        priceListener?.remove() // Prevent memory leaks
    }
}
