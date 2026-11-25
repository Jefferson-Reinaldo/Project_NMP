package com.example.a38_karhutla

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.a38_karhutla.databinding.ActivityResultBinding
import com.example.a38_karhutla.model.Category
import com.example.a38_karhutla.util.GlobalData
import org.json.JSONObject

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_TOTAL_QUESTIONS = "extra_total_questions"
        const val EXTRA_CORRECT_COUNT = "extra_correct_count"
    }

    private var category: Category? = null
    private var score: Int = 0
    private var totalQuestions: Int = 0
    private var correctCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data hasil dari Intent extras
        category = intent.getParcelableExtra(EXTRA_CATEGORY)
        score = intent.getIntExtra(EXTRA_SCORE, 0)
        totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 0)
        correctCount = intent.getIntExtra(EXTRA_CORRECT_COUNT, 0)

        displayResult()
        handleHighScore()
        saveScoreToServer()

        // Tombol Bagikan Skor (Implicit Intent ACTION_SEND + createChooser)
        binding.btnShareScore.setOnClickListener {
            shareScore()
        }

        // Tombol Kembali ke Beranda
        binding.btnBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun displayResult() {
        binding.txtResultCategory.text = "Kategori: ${category?.name ?: "Ujian"}"
        binding.txtFinalScore.text = score.toString()
        binding.txtScoreSummary.text = "Benar: $correctCount dari $totalQuestions soal"
    }

    private fun handleHighScore() {
        val prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val currentHighScore = prefs.getInt("key_high_score", 0)

        if (score > currentHighScore) {
            prefs.edit().putInt("key_high_score", score).apply()
            binding.txtHighScoreNotice.visibility = View.VISIBLE
        } else {
            binding.txtHighScoreNotice.visibility = View.GONE
        }
    }

    private fun saveScoreToServer() {
        val currentUser = GlobalData.currentUser
        val cat = category

        if (currentUser == null || cat == null) return

        binding.progressBarSavingScore.visibility = View.VISIBLE
        binding.txtSavingStatus.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = GlobalData.BASE_URL + "submit_score.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                binding.progressBarSavingScore.visibility = View.GONE
                binding.txtSavingStatus.visibility = View.GONE
                Log.d("ResultActivity", "Submit Score Response: $response")

                try {
                    val jsonResponse = JSONObject(response)
                    val result = jsonResponse.getString("result")
                    if (result == "success") {
                        Toast.makeText(this, "Skor tersimpan ke riwayat!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("ResultActivity", "JSON Parsing Error: ${e.message}")
                }
            },
            { error ->
                binding.progressBarSavingScore.visibility = View.GONE
                binding.txtSavingStatus.visibility = View.GONE
                Log.e("ResultActivity", "Volley Error: ${error.message}")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = currentUser.id.toString()
                params["category_id"] = cat.id.toString()
                params["score"] = score.toString()
                params["total_questions"] = totalQuestions.toString()
                return params
            }
        }

        queue.add(stringRequest)
    }

    private fun shareScore() {
        // Penerapan materi Implicit Intent: Intent.ACTION_SEND + Intent.createChooser
        val shareText = """
            Halo! Saya baru saja menyelesaikan ujian "${category?.name}" di aplikasi Examin.
            Skor Akhir: $score
            Benar: $correctCount dari $totalQuestions soal!
            Ayo uji kemampuanmu sekarang!
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Hasil Ujian Examin - ${category?.name}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, "Bagikan Skor Ujian Lewat"))
    }
}
