package com.example.a38_karhutla

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.a38_karhutla.databinding.ActivityAddEditQuestionBinding
import com.example.a38_karhutla.model.Category
import com.example.a38_karhutla.model.Question
import com.example.a38_karhutla.util.GlobalData
import org.json.JSONObject

class AddEditQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditQuestionBinding
    private val categories = ArrayList<Category>()
    private lateinit var spinnerAdapter: ArrayAdapter<Category>
    private var editingQuestion: Question? = null

    companion object {
        const val EXTRA_QUESTION = "extra_question"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek apakah mode Edit atau Tambah Baru
        editingQuestion = intent.getParcelableExtra(EXTRA_QUESTION)

        setupToolbar()
        setupSpinner()
        loadCategoriesFromApi()

        binding.btnSaveQuestion.setOnClickListener {
            validateAndSave()
        }
    }

    private fun setupToolbar() {
        if (editingQuestion != null) {
            binding.toolbarFormQuestion.title = "Edit Soal"
            binding.btnSaveQuestion.text = "Perbarui Soal"
        } else {
            binding.toolbarFormQuestion.title = "Tambah Soal Baru"
            binding.btnSaveQuestion.text = "Simpan Soal"
        }

        binding.toolbarFormQuestion.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupSpinner() {
        spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        binding.spinnerCategory.adapter = spinnerAdapter
    }

    private fun loadCategoriesFromApi() {
        val queue = Volley.newRequestQueue(this)
        val url = GlobalData.BASE_URL + "get_categories.php"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("result") == "success") {
                        val dataArray = jsonResponse.getJSONArray("data")
                        categories.clear()

                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)
                            categories.add(
                                Category(
                                    id = obj.getInt("id"),
                                    name = obj.getString("name"),
                                    description = obj.optString("description", ""),
                                    iconUrl = obj.optString("icon_url", "")
                                )
                            )
                        }

                        spinnerAdapter.notifyDataSetChanged()

                        // Jika sedang mengedit, isi formulir dengan data lama
                        editingQuestion?.let { q ->
                            populateExistingData(q)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AddEditQuestion", "Category JSON Error: ${e.message}")
                }
            },
            { error ->
                Log.e("AddEditQuestion", "Category Volley Error: ${error.message}")
            }
        )

        queue.add(stringRequest)
    }

    private fun populateExistingData(q: Question) {
        binding.txtInputQuestionText.setText(q.questionText)
        binding.txtInputOptionA.setText(q.optionA)
        binding.txtInputOptionB.setText(q.optionB)
        binding.txtInputOptionC.setText(q.optionC)
        binding.txtInputOptionD.setText(q.optionD)
        binding.txtInputImageUrl.setText(q.imageUrl)
        binding.cbIsActive.isChecked = (q.isActive == 1)

        // Pilih kunci jawaban pada RadioGroup
        when (q.correctAnswer.uppercase()) {
            "A" -> binding.rbKeyA.isChecked = true
            "B" -> binding.rbKeyB.isChecked = true
            "C" -> binding.rbKeyC.isChecked = true
            "D" -> binding.rbKeyD.isChecked = true
        }

        // Pilih kategori yang sesuai di Spinner
        val selectedCatIndex = categories.indexOfFirst { it.id == q.categoryId }
        if (selectedCatIndex != -1) {
            binding.spinnerCategory.setSelection(selectedCatIndex)
        }
    }

    private fun validateAndSave() {
        val questionText = binding.txtInputQuestionText.text.toString().trim()
        val optionA = binding.txtInputOptionA.text.toString().trim()
        val optionB = binding.txtInputOptionB.text.toString().trim()
        val optionC = binding.txtInputOptionC.text.toString().trim()
        val optionD = binding.txtInputOptionD.text.toString().trim()
        val imageUrl = binding.txtInputImageUrl.text.toString().trim()
        val isActive = if (binding.cbIsActive.isChecked) 1 else 0

        val selectedCategory = binding.spinnerCategory.selectedItem as? Category
        if (selectedCategory == null) {
            Toast.makeText(this, "Pilih kategori ujian terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (questionText.isEmpty()) {
            binding.layoutInputQuestionText.error = "Teks pertanyaan wajib diisi"
            return
        } else {
            binding.layoutInputQuestionText.error = null
        }

        if (optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()) {
            Toast.makeText(this, "Semua pilihan jawaban (A, B, C, D) wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val checkedKeyId = binding.radioGroupKeyAnswer.checkedRadioButtonId
        if (checkedKeyId == -1) {
            Toast.makeText(this, "Pilih salah satu kunci jawaban yang benar", Toast.LENGTH_SHORT).show()
            return
        }

        val correctAnswer = when (checkedKeyId) {
            R.id.rbKeyA -> "A"
            R.id.rbKeyB -> "B"
            R.id.rbKeyC -> "C"
            R.id.rbKeyD -> "D"
            else -> "A"
        }

        sendDataToApi(
            categoryId = selectedCategory.id,
            questionText = questionText,
            optionA = optionA,
            optionB = optionB,
            optionC = optionC,
            optionD = optionD,
            correctAnswer = correctAnswer,
            imageUrl = imageUrl,
            isActive = isActive
        )
    }

    private fun sendDataToApi(
        categoryId: Int,
        questionText: String,
        optionA: String,
        optionB: String,
        optionC: String,
        optionD: String,
        correctAnswer: String,
        imageUrl: String,
        isActive: Int
    ) {
        binding.progressBarSaveQuestion.visibility = View.VISIBLE
        binding.btnSaveQuestion.isEnabled = false

        val isEdit = (editingQuestion != null)
        val url = GlobalData.BASE_URL + if (isEdit) "edit_question.php" else "add_question.php"

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                binding.progressBarSaveQuestion.visibility = View.GONE
                binding.btnSaveQuestion.isEnabled = true
                Log.d("AddEditQuestion", "Response: $response")

                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("result") == "success") {
                        val message = if (isEdit) "Soal berhasil diperbarui" else "Soal baru berhasil ditambahkan"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        finish() // Selesai dan kembali ke AdminActivity
                    } else {
                        val errorMsg = jsonResponse.optString("message", "Gagal menyimpan soal")
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("AddEditQuestion", "JSON Error: ${e.message}")
                    Toast.makeText(this, "Format data server tidak valid", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                binding.progressBarSaveQuestion.visibility = View.GONE
                binding.btnSaveQuestion.isEnabled = true
                Log.e("AddEditQuestion", "Volley Error: ${error.message}")
                Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                if (isEdit) {
                    params["id"] = editingQuestion!!.id.toString()
                }
                params["category_id"] = categoryId.toString()
                params["question_text"] = questionText
                params["option_a"] = optionA
                params["option_b"] = optionB
                params["option_c"] = optionC
                params["option_d"] = optionD
                params["correct_answer"] = correctAnswer
                params["image_url"] = imageUrl
                params["is_active"] = isActive.toString()
                return params
            }
        }

        queue.add(stringRequest)
    }
}
