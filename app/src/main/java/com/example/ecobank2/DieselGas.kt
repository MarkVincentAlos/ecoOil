package com.example.ecobank2

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    val dieselPrice = document.getDouble("diesel_price") ?: 0.0
                    val gasolinePrice = document.getDouble("gasoline_price") ?: 0.0

                    val dieselChange = document.getDouble("diesel_change") ?: 0.0
                    val gasolineChange = document.getDouble("gasoline_change") ?: 0.0

                    // Display the total prices
                    binding.dieselPrice.text = "₱${"%.2f".format(dieselPrice)}"
                    binding.gasolinePrice.text = "₱${"%.2f".format(gasolinePrice)}"

                    // Show/hide price change icons
                    updatePriceChangeIcon(binding.dieselIcon, dieselChange)
                    updatePriceChangeIcon(binding.gasolineIcon, gasolineChange)
                }
            }
    }

    private fun updatePriceChangeIcon(iconView: View, priceChange: Double) {
        val iconDrawable: Drawable? = when {
            priceChange > 0 -> ContextCompat.getDrawable(requireContext(), R.drawable.increase) // Upward arrow
            priceChange < 0 -> ContextCompat.getDrawable(requireContext(), R.drawable.decrease) // Downward arrow
            else -> null
        }

        if (iconDrawable != null) {
            (iconView as? android.widget.ImageView)?.apply {
                setImageDrawable(iconDrawable)
                visibility = View.VISIBLE
            }
        } else {
            iconView.visibility = View.GONE  // Hide if no change
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        priceListener?.remove()
    }
}
