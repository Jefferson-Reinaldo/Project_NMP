package com.example.a38_karhutla.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a38_karhutla.R
import com.example.a38_karhutla.databinding.ItemCategoryBinding
import com.example.a38_karhutla.model.Category
import com.squareup.picasso.Picasso

class CategoryAdapter(
    private val categories: ArrayList<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.txtCategoryName.text = category.name
        holder.binding.txtCategoryDesc.text = category.description

        // Memuat gambar icon kategori menggunakan Picasso
        if (category.iconUrl.isNotEmpty()) {
            Picasso.get()
                .load(category.iconUrl)
                .placeholder(R.drawable.ic_exam_logo)
                .error(R.drawable.ic_exam_logo)
                .into(holder.binding.imgCategoryIcon)
        } else {
            holder.binding.imgCategoryIcon.setImageResource(R.drawable.ic_exam_logo)
        }

        // Listener saat item card atau tombol Mulai Ujian diklik
        holder.binding.cardCategory.setOnClickListener {
            onItemClick(category)
        }
        holder.binding.btnStartExam.setOnClickListener {
            onItemClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}
