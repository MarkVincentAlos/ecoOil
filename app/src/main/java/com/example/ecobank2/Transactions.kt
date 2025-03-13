package com.example.ecobank2

import TransactionAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class Transactions : Fragment(R.layout.fragment_transactions) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private val transactionList = mutableListOf<TransactionData>()
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.transaction_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TransactionAdapter(transactionList)
        recyclerView.adapter = adapter

        fetchTransactions()
    }

    private fun fetchTransactions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()

            db.collection("transactions")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val transactions = snapshot.documents.mapNotNull {
                            it.toObject(TransactionData::class.java)
                        }

                        transactionList.addAll(transactions)

                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("Firestore", "No transactions found")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching transactions: ${e.message}")
                    e.printStackTrace()
                }
        } else {
            Log.e("Transactions", "User not logged in")
        }
    }



    private fun updateTransactionList(transactions: List<TransactionData>) {
        if (isAdded && !isDetached) {
            transactionList.clear()
            transactionList.addAll(transactions)
            adapter.notifyDataSetChanged()
        }
    }
}
