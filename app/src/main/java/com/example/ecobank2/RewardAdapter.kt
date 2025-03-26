package com.example.ecobank2

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RewardAdapter(
    private val context: Context,
    private val rewardList: List<RewardData>
) : RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.reward_list_item, parent, false)
        return RewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = rewardList[position]

        holder.title.text = reward.title
        holder.description.text = reward.description
        holder.points.text = "${reward.points} Points"

        // Show claim dialog when clicked
        holder.itemView.setOnClickListener {
            showClaimDialog(reward)
        }
    }

    override fun getItemCount(): Int = rewardList.size

    inner class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.reward_title)
        val description: TextView = itemView.findViewById(R.id.reward_description)
        val points: TextView = itemView.findViewById(R.id.reward_points)
    }

    private fun showClaimDialog(reward: RewardData) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Claim Reward")
        builder.setMessage("Do you want to claim '${reward.title}' for ${reward.points} points?")

        // Claim Button
        builder.setPositiveButton("Claim") { _, _ ->
            claimReward(reward.points)
        }

        // Cancel Button
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun claimReward(rewardPoints: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Get user's current points
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userPoints = document.getLong("points") ?: 0

                    if (userPoints >= rewardPoints) {
                        // Calculate new points after claiming the reward
                        val newPoints = userPoints - rewardPoints

                        // Update Firestore
                        db.collection("users").document(userId)
                            .update("points", newPoints)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Reward Claimed! Remaining Points: $newPoints", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to claim reward.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Not enough points to claim this reward!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to retrieve user points.", Toast.LENGTH_SHORT).show()
            }
    }

}
