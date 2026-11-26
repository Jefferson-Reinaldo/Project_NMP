package com.example.a38_karhutla

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.a38_karhutla.adapter.AdminQuestionAdapter
import com.example.a38_karhutla.databinding.ActivityAdminBinding
import com.example.a38_karhutla.model.Question
import com.example.a38_karhutla.util.GlobalData
import org.json.JSONObject

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val questions = ArrayList<Question>()
    private lateinit var adapter: AdminQuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()

        // Floating Action Button untuk menambah soal baru
        binding.fabAddQuestion.setOnClickListener {
            val intent = Intent(this, AddEditQuestionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupToolbar() {
        binding.toolbarAdmin.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_admin_logout -> {
                    confirmLogout()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvAdminQuestions.layoutManager = LinearLayoutManager(this)
        adapter = AdminQuestionAdapter(
            questions,
            onEditClick = { question ->
                // Navigasi ke AddEditQuestionActivity dengan membawa data soal lama via Intent Extra
                val intent = Intent(this, AddEditQuestionActivity::class.java).apply {
                    putExtra(AddEditQuestionActivity.EXTRA_QUESTION, question)
                }
                startActivity(intent)
            },
            onDeleteClick = { question ->
                // Konfirmasi hapus soal dengan AlertDialog
                confirmDeleteQuestion(question)
            }
        )
        binding.rvAdminQuestions.adapter = adapter
    }

    /**
     * Sesuai ketentuan penyesuaian: onResume() wajib digunakan untuk memuat ulang daftar soal
     * agar data di RecyclerView langsung ter-update setelah Admin menambah/mengedit soal.
     */
    override fun onResume() {
        super.onResume()
        loadQuestionsFromApi()
    }

    private fun loadQuestionsFromApi() {
        binding.progressBarAdmin.visibility = View.VISIBLE
        binding.txtEmptyAdmin.visibility = View.GONE

        val queue = Volley.newRequestQueue(this)
        val url = GlobalData.BASE_URL + "get_questions.php" // Tanpa filter category_id untuk mengambil seluruh soal

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                binding.progressBarAdmin.visibility = View.GONE
                Log.d("AdminActivity", "Response: $response")

                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("result") == "success") {
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

                        adapter.notifyDataSetChanged()

                        if (questions.isEmpty()) {
                            binding.txtEmptyAdmin.visibility = View.VISIBLE
                        } else {
                            binding.txtEmptyAdmin.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AdminActivity", "JSON Parsing Error: ${e.message}")
                }
            },
            { error ->
                binding.progressBarAdmin.visibility = View.GONE
                Log.e("AdminActivity", "Volley Error: ${error.message}")
                Toast.makeText(this, "Gagal memuat bank soal", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(stringRequest)
    }

    private fun confirmDeleteQuestion(question: Question) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Soal?")
            .setMessage("Apakah Anda yakin ingin menghapus soal ini:\n\n\"${question.questionText}\"?")
            .setPositiveButton("Ya, Hapus") { _, _ ->
                deleteQuestionFromApi(question.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteQuestionFromApi(questionId: Int) {
        binding.progressBarAdmin.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = GlobalData.BASE_URL + "delete_question.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                binding.progressBarAdmin.visibility = View.GONE
                Log.d("AdminActivity", "Delete Response: $response")

                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("result") == "success") {
                        Toast.makeText(this, "Soal berhasil dihapus", Toast.LENGTH_SHORT).show()
                        loadQuestionsFromApi() // Refresh daftar soal
                    } else {
                        val msg = jsonResponse.optString("message", "Gagal menghapus soal")
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("AdminActivity", "Delete JSON Error: ${e.message}")
                }
            },
            { error ->
                binding.progressBarAdmin.visibility = View.GONE
                Log.e("AdminActivity", "Delete Volley Error: ${error.message}")
                Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = questionId.toString()
                return params
            }
        }

        queue.add(stringRequest)
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Keluar")
            .setMessage("Keluar dari panel Admin?")
            .setPositiveButton("Keluar") { _, _ ->
                val prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().clear().apply()
                GlobalData.currentUser = null

                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
