package com.example.a38_karhutla.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.a38_karhutla.adapter.LeaderboardAdapter
import com.example.a38_karhutla.databinding.FragmentLeaderboardBinding
import com.example.a38_karhutla.model.Score
import com.example.a38_karhutla.util.GlobalData
import org.json.JSONObject

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private val scores = ArrayList<Score>()
    private lateinit var adapter: LeaderboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())
        adapter = LeaderboardAdapter(scores)
        binding.rvLeaderboard.adapter = adapter

        loadLeaderboard()
    }

    override fun onResume() {
        super.onResume()
        // Refresh leaderboard saat kembali ke tab ini
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        binding.progressBarLeaderboard.visibility = View.VISIBLE
        binding.txtEmptyLeaderboard.visibility = View.GONE

        val queue = Volley.newRequestQueue(requireContext())
        val url = GlobalData.BASE_URL + "get_leaderboard.php"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                _binding?.let {
                    binding.progressBarLeaderboard.visibility = View.GONE
                    Log.d("LeaderboardFragment", "Response: $response")

                    try {
                        val jsonResponse = JSONObject(response)
                        val result = jsonResponse.getString("result")

                        if (result == "success") {
                            val dataArray = jsonResponse.getJSONArray("data")
                            scores.clear()

                            for (i in 0 until dataArray.length()) {
                                val obj = dataArray.getJSONObject(i)
                                val score = Score(
                                    id = obj.getInt("id"),
                                    userId = obj.getInt("user_id"),
                                    username = obj.getString("username"),
                                    categoryId = obj.getInt("category_id"),
                                    categoryName = obj.getString("category_name"),
                                    score = obj.getInt("score"),
                                    totalQuestions = obj.getInt("total_questions"),
                                    date = obj.optString("created_at", "")
                                )
                                scores.add(score)
                            }

                            adapter.notifyDataSetChanged()

                            if (scores.isEmpty()) {
                                binding.txtEmptyLeaderboard.visibility = View.VISIBLE
                            } else {
                                binding.txtEmptyLeaderboard.visibility = View.GONE
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("LeaderboardFragment", "JSON Parsing Error: ${e.message}")
                    }
                }
            },
            { error ->
                _binding?.let {
                    binding.progressBarLeaderboard.visibility = View.GONE
                    Log.e("LeaderboardFragment", "Volley Error: ${error.message}")
                    Toast.makeText(requireContext(), "Gagal memuat riwayat skor", Toast.LENGTH_SHORT).show()
                }
            }
        )

        queue.add(stringRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = LeaderboardFragment()
    }
}
