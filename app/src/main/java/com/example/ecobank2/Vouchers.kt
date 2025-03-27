package com.example.ecobank2

import VoucherAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Vouchers : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VoucherAdapter
    private lateinit var progressBar: ProgressBar
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_vouchers, container, false)

        recyclerView = view.findViewById(R.id.voucherRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchVouchers()

        return view
    }

    private fun fetchVouchers() {
        if (userId == null) {
            Log.e("VoucherError", "User ID is null")
            return
        }

        db.collection("vouchers")
            .whereEqualTo("userId", userId)
            .whereEqualTo("used", false) // Only show unused vouchers
            .get()
            .addOnSuccessListener { snapshots ->
                val voucherList = mutableListOf<VoucherData>()

                if (snapshots.isEmpty) {
                    Log.d("VoucherData", "No vouchers found")
                }

                snapshots.documents.forEach { document ->
                    val voucher = document.toObject(VoucherData::class.java)?.copy(id = document.id)
                    if (voucher != null) {
                        Log.d("VoucherData", "Voucher retrieved: $voucher")
                        voucherList.add(voucher)
                    } else {
                        Log.e("VoucherData", "Voucher data is null for document: ${document.id}")
                    }
                }

                if (voucherList.isNotEmpty()) {
                    adapter = VoucherAdapter(voucherList)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "No available vouchers", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("VoucherError", "Error fetching vouchers: ${exception.localizedMessage}")
                Toast.makeText(requireContext(), "Failed to load vouchers", Toast.LENGTH_SHORT).show()
            }
    }



}
