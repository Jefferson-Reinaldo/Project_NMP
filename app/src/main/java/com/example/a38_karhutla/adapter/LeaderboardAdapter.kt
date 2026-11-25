package com.example.a38_karhutla.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a38_karhutla.databinding.ItemLeaderboardBinding
import com.example.a38_karhutla.model.Score

class LeaderboardAdapter(
    private val scores: ArrayList<Score>
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(val binding: ItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val item = scores[position]
        holder.binding.txtScoreValue.text = item.score.toString()
        holder.binding.txtTotalQuestions.text = "/ ${item.totalQuestions} Soal"
        holder.binding.txtCategoryNameScore.text = item.categoryName
        holder.binding.txtParticipantName.text = "Peserta: ${item.username}"
        holder.binding.txtScoreDate.text = "Waktu: ${item.date}"
    }

    override fun getItemCount(): Int = scores.size
}
