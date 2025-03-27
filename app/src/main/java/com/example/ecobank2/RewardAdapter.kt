package com.example.ecobank2

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
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
            claimReward(reward)
        }

        // Cancel Button
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun claimReward(reward: RewardData) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Get user's current points and username
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userPoints = document.getLong("points") ?: 0
                    val username = document.getString("username") ?: "Unknown"

                    if (userPoints >= reward.points) {
                        val newPoints = userPoints - reward.points

                        // Update Firestore (deduct points)
                        db.collection("users").document(userId)
                            .update("points", newPoints)
                            .addOnSuccessListener {
                                // Create a new Timestamp object for current time
                                val timestamp = Timestamp.now()  // Correct way to get current time

                                // Store voucher in 'vouchers' collection
                                val voucherData = hashMapOf(
                                    "userId" to userId,
                                    "username" to username,
                                    "rewardTitle" to reward.title,
                                    "rewardPoints" to reward.points,
                                    "timestamp" to timestamp,  // Correct Timestamp field
                                    "used" to false // Mark voucher as unused initially
                                )

                                // Add voucher to Firestore
                                db.collection("vouchers").add(voucherData)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Reward Claimed! Remaining Points: $newPoints", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to save voucher.", Toast.LENGTH_SHORT).show()
                                    }
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
                Toast.makeText(context, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show()
            }
    }
}
