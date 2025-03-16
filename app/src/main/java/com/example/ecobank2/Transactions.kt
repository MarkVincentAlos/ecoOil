package com.example.ecobank2

import TransactionAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecobank2.databinding.FragmentTransactionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Transactions : Fragment(R.layout.fragment_transactions) {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TransactionAdapter
    private val transactionList = mutableListOf<TransactionData>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener { navigateToFragment(More()) }

        binding.transactionList.layoutManager = LinearLayoutManager(requireContext())
        adapter = TransactionAdapter(transactionList)
        binding.transactionList.adapter = adapter

        fetchTransactions()
    }

    private fun fetchTransactions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            db.collection("transactions")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val transactions = snapshot.documents.mapNotNull {
                            it.toObject(TransactionData::class.java)
                        }

                        updateTransactionList(transactions)
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

    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
