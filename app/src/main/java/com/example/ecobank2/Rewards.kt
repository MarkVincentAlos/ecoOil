package com.example.ecobank2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.fragment.app.Fragment
import com.example.ecobank.R
import java.util.ArrayList
import java.util.HashMap
class Rewards : Fragment() {

    private lateinit var rewardsListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rewards, container, false)

        // Initialize ListView
        rewardsListView = view.findViewById(R.id.rewards_listview)

        // Create a list of rewards with hardcoded data
        val rewardList = ArrayList<HashMap<String, String>>()

        // Example 1: 50 Points for a 100 pesos voucher for free gas
        val reward1 = HashMap<String, String>()
        reward1["title"] = "Free Gas Voucher"
        reward1["points"] = "50 Points"
        reward1["description"] = "Voucher worth 100 pesos for free gas"
        rewardList.add(reward1)

        // Example 2: 100 Points for a 200 pesos shopping voucher
        val reward2 = HashMap<String, String>()
        reward2["title"] = "Get SHOEI Helmet"
        reward2["points"] = "1000 Points"
        reward2["description"] = "Grab a SHOEI Helmet just for 1000 points"
        rewardList.add(reward2)

        // Example 3: 200 Points for a 500 pesos restaurant voucher
        val reward3 = HashMap<String, String>()
        reward3["title"] = "FilOil Ticket"
        reward3["points"] = "200 Points"
        reward3["description"] = "Grab a ticket to wtach your favorite sports at FilOil Arena"
        rewardList.add(reward3)

        // Set up the adapter for the ListView
        val from = arrayOf("title", "points", "description")
        val to = intArrayOf(R.id.reward_title, R.id.reward_points, R.id.reward_description)
        val adapter = SimpleAdapter(requireContext(), rewardList, R.layout.reward_list_item, from, to)

        // Set the adapter for the ListView
        rewardsListView.adapter = adapter

        return view
    }
}
