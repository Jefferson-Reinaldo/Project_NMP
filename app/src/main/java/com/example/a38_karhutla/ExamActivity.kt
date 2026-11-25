package com.example.a38_karhutla

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.a38_karhutla.databinding.ActivityExamBinding
import com.example.a38_karhutla.model.Category
import com.example.a38_karhutla.model.Question
import com.example.a38_karhutla.util.GlobalData
import com.squareup.picasso.Picasso
import org.json.JSONObject

class ExamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamBinding

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }

    private var category: Category? = null
    private val questions = ArrayList<Question>()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil kategori dari Intent
        category = intent.getParcelableExtra(EXTRA_CATEGORY)

        binding.toolbarExam.title = category?.name ?: "Ujian"
        binding.toolbarExam.setNavigationOnClickListener {
            confirmExit()
        }

        // Listener perubahan jawaban pada RadioGroup
        binding.radioGroupOptions.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1 && questions.isNotEmpty()) {
                val selectedOption = when (checkedId) {
                    R.id.rbOptionA -> "A"
                    R.id.rbOptionB -> "B"
                    R.id.rbOptionC -> "C"
                    R.id.rbOptionD -> "D"
                    else -> ""
                }
                questions[currentIndex].selectedAnswer = selectedOption
            }
        }

        // Tombol Sebelumnya
        binding.btnPrevious.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                displayCurrentQuestion()
            }
        }

        // Tombol Selanjutnya / Selesai
        binding.btnNext.setOnClickListener {
            if (currentIndex < questions.size - 1) {
                currentIndex++
                displayCurrentQuestion()
            } else {
                // Konfirmasi selesai ujian
                confirmFinishExam()
            }
        }

        // Muat soal dari API backend
        loadQuestions()
    }

    private fun loadQuestions() {
        val cat = category ?: return

        binding.progressBarLoadingQuestions.visibility = View.VISIBLE
        binding.cardQuestion.visibility = View.GONE
        binding.layoutNavButtons.visibility = View.GONE
        binding.txtEmptyExam.visibility = View.GONE

        val queue = Volley.newRequestQueue(this)
        val url = "${GlobalData.BASE_URL}get_questions.php?category_id=${cat.id}"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                binding.progressBarLoadingQuestions.visibility = View.GONE
                Log.d("ExamActivity", "Response: $response")

                try {
                    val jsonResponse = JSONObject(response)
                    val result = jsonResponse.getString("result")

                    if (result == "success") {
                        val dataArray = jsonResponse.getJSONArray("data")
                        questions.clear()

                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)
                            val q = Question(
                                id = obj.getInt("id"),
                                categoryId = obj.getInt("category_id"),
                                questionText = obj.getString("question_text"),
                                optionA = obj.getString("option_a"),
                                optionB = obj.getString("option_b"),
                                optionC = obj.getString("option_c"),
                                optionD = obj.getString("option_d"),
                                correctAnswer = obj.getString("correct_answer"),
                                imageUrl = obj.optString("image_url", ""),
                                isActive = obj.optInt("is_active", 1)
                            )
                            questions.add(q)
                        }

                        if (questions.isNotEmpty()) {
                            binding.cardQuestion.visibility = View.VISIBLE
                            binding.layoutNavButtons.visibility = View.VISIBLE
                            currentIndex = 0
                            displayCurrentQuestion()
                        } else {
                            binding.txtEmptyExam.visibility = View.VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ExamActivity", "JSON Parsing Error: ${e.message}")
                    Toast.makeText(this, "Format data soal tidak valid", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                binding.progressBarLoadingQuestions.visibility = View.GONE
                Log.e("ExamActivity", "Volley Error: ${error.message}")
                Toast.makeText(this, "Gagal memuat soal ujian", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(stringRequest)
    }

    private fun displayCurrentQuestion() {
        if (questions.isEmpty()) return

        val question = questions[currentIndex]

        // Update teks progres & progress indicator
        binding.txtQuestionNumber.text = "Soal ${currentIndex + 1} dari ${questions.size}"
        val progressPercent = (((currentIndex + 1).toDouble() / questions.size) * 100).toInt()
        binding.progressExam.progress = progressPercent

        // Teks pertanyaan & pilihan ganda
        binding.txtQuestionText.text = question.questionText
        binding.rbOptionA.text = "A. ${question.optionA}"
        binding.rbOptionB.text = "B. ${question.optionB}"
        binding.rbOptionC.text = "C. ${question.optionC}"
        binding.rbOptionD.text = "D. ${question.optionD}"

        // Gambar soal via Picasso jika tersedia URL-nya
        if (question.imageUrl.isNotEmpty()) {
            binding.imgQuestion.visibility = View.VISIBLE
            Picasso.get()
                .load(question.imageUrl)
                .into(binding.imgQuestion)
        } else {
            binding.imgQuestion.visibility = View.GONE
        }

        // Tampilkan jawaban yang telah dipilih sebelumnya (jika ada)
        binding.radioGroupOptions.clearCheck()
        when (question.selectedAnswer) {
            "A" -> binding.rbOptionA.isChecked = true
            "B" -> binding.rbOptionB.isChecked = true
            "C" -> binding.rbOptionC.isChecked = true
            "D" -> binding.rbOptionD.isChecked = true
        }

        // Atur tombol Sebelumnya & Selanjutnya/Selesai
        binding.btnPrevious.isEnabled = currentIndex > 0

        if (currentIndex == questions.size - 1) {
            binding.btnNext.text = "Selesai Ujian"
        } else {
            binding.btnNext.text = "Selanjutnya"
        }
    }

    private fun confirmFinishExam() {
        val unansweredCount = questions.count { it.selectedAnswer.isEmpty() }
        val message = if (unansweredCount > 0) {
            "Masih ada $unansweredCount soal yang belum dijawab. Yakin ingin menyelesaikan ujian?"
        } else {
            "Semua soal telah dijawab. Kumpulkan jawaban sekarang?"
        }

        AlertDialog.Builder(this)
            .setTitle("Kumpulkan Jawaban")
            .setMessage(message)
            .setPositiveButton("Ya, Kumpulkan") { _, _ ->
                calculateAndFinish()
            }
            .setNegativeButton("Periksa Kembali", null)
            .show()
    }

    private fun calculateAndFinish() {
        var correctCount = 0
        for (q in questions) {
            if (q.selectedAnswer.equals(q.correctAnswer, ignoreCase = true)) {
                correctCount++
            }
        }

        // Skor skala 100
        val finalScore = if (questions.isNotEmpty()) {
            ((correctCount.toDouble() / questions.size) * 100).toInt()
        } else 0

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_CATEGORY, category)
            putExtra(ResultActivity.EXTRA_SCORE, finalScore)
            putExtra(ResultActivity.EXTRA_TOTAL_QUESTIONS, questions.size)
            putExtra(ResultActivity.EXTRA_CORRECT_COUNT, correctCount)
        }
        startActivity(intent)
        finish() // Tutup ExamActivity agar peserta tidak dapat kembali ke ujian setelah selesai
    }

    private fun confirmExit() {
        AlertDialog.Builder(this)
            .setTitle("Keluar dari Ujian?")
            .setMessage("Jawaban Anda tidak akan tersimpan jika keluar sekarang.")
            .setPositiveButton("Keluar") { _, _ ->
                finish()
            }
            .setNegativeButton("Lanjut Ujian", null)
            .show()
    }

    override fun onBackPressed() {
        confirmExit()
    }
}
