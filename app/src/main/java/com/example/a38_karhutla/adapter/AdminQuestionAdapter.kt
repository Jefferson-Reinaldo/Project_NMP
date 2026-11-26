package com.example.a38_karhutla.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a38_karhutla.databinding.ItemQuestionAdminBinding
import com.example.a38_karhutla.model.Question
import com.squareup.picasso.Picasso

class AdminQuestionAdapter(
    private val questions: ArrayList<Question>,
    private val onEditClick: (Question) -> Unit,
    private val onDeleteClick: (Question) -> Unit
) : RecyclerView.Adapter<AdminQuestionAdapter.AdminQuestionViewHolder>() {

    class AdminQuestionViewHolder(val binding: ItemQuestionAdminBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminQuestionViewHolder {
        val binding = ItemQuestionAdminBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdminQuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminQuestionViewHolder, position: Int) {
        val question = questions[position]

        holder.binding.badgeKeyAnswer.text = "Kunci: ${question.correctAnswer}"
        holder.binding.txtAdminQuestionText.text = "${position + 1}. ${question.questionText}"

        val optionsText = "A: ${question.optionA} | B: ${question.optionB} | C: ${question.optionC} | D: ${question.optionD}"
        holder.binding.txtAdminOptionsSummary.text = optionsText

        // Status aktif
        if (question.isActive == 1) {
            holder.binding.txtStatusActive.text = "Aktif"
            holder.binding.txtStatusActive.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_green_dark)
            )
        } else {
            holder.binding.txtStatusActive.text = "Nonaktif"
            holder.binding.txtStatusActive.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_red_dark)
            )
        }

        // Preview gambar Picasso jika tersedia
        if (question.imageUrl.isNotEmpty()) {
            holder.binding.imgAdminQuestionPreview.visibility = View.VISIBLE
            Picasso.get()
                .load(question.imageUrl)
                .into(holder.binding.imgAdminQuestionPreview)
        } else {
            holder.binding.imgAdminQuestionPreview.visibility = View.GONE
        }

        // Listener Aksi Edit & Hapus
        holder.binding.btnAdminEdit.setOnClickListener {
            onEditClick(question)
        }

        holder.binding.btnAdminDelete.setOnClickListener {
            onDeleteClick(question)
        }
    }

    override fun getItemCount(): Int = questions.size
}
