package com.example.a38_karhutla

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a38_karhutla.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        // Akan dipanggil untuk memuat ulang daftar soal dari get_questions.php
    }
}
