package com.example.ecobank2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Rewards : Fragment() {

    private lateinit var rewardPointsTextView: TextView
    private lateinit var rewardsListView: RecyclerView
    private lateinit var rewardAdapter: RewardAdapter
    private val rewardList = mutableListOf<RewardData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rewards, container, false)

        // Initialize views
        rewardPointsTextView = view.findViewById(R.id.reward_points)
        rewardsListView = view.findViewById(R.id.rewards_listview)

        // Set up RecyclerView
        rewardsListView.layoutManager = LinearLayoutManager(requireContext())
        rewardAdapter = RewardAdapter(requireContext(), rewardList)
        rewardsListView.adapter = rewardAdapter

        // Fetch user points & rewards
        fetchUserPoints()
        fetchRewards()

        return view
    }

    private fun fetchUserPoints() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Listen for real-time updates on points
        db.collection("users").document(userId)
            .addSnapshotListener { document, _ ->
                if (document != null && document.exists()) {
                    val points = document.getLong("points") ?: 0
                    rewardPointsTextView.text = "You have $points Points"
                }
            }
    }

    private fun fetchRewards() {
        val db = FirebaseFirestore.getInstance()
        db.collection("rewards")
            .get()
            .addOnSuccessListener { documents ->
                rewardList.clear()
                for (document in documents) {
                    val reward = RewardData(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        points = document.getLong("points")?.toInt() ?: 0
                    )
                    rewardList.add(reward)
                }
                rewardAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load rewards.", Toast.LENGTH_SHORT).show()
            }
    }
}
