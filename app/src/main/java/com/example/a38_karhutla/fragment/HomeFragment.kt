package com.example.a38_karhutla.fragment

import android.content.Intent
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
import com.example.a38_karhutla.ExamActivity
import com.example.a38_karhutla.adapter.CategoryAdapter
import com.example.a38_karhutla.databinding.FragmentHomeBinding
import com.example.a38_karhutla.model.Category
import com.example.a38_karhutla.util.GlobalData
import org.json.JSONObject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val categories = ArrayList<Category>()
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set teks sapaan user
        val userName = GlobalData.currentUser?.name ?: "Peserta"
        binding.txtGreeting.text = "Halo, $userName!"

        // Inisialisasi RecyclerView
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoryAdapter(categories) { selectedCategory ->
            // Buka ExamActivity membawa objek Category terpilih via Intent putExtra
            val intent = Intent(requireContext(), ExamActivity::class.java)
            intent.putExtra(ExamActivity.EXTRA_CATEGORY, selectedCategory)
            startActivity(intent)
        }
        binding.rvCategories.adapter = adapter

        // Memuat data kategori dari API backend
        loadCategories()
    }

    override fun onResume() {
        super.onResume()
        // Muat ulang kategori jika data berubah
        loadCategories()
    }

    private fun loadCategories() {
        binding.progressBarHome.visibility = View.VISIBLE
        binding.txtEmptyHome.visibility = View.GONE

        val queue = Volley.newRequestQueue(requireContext())
        val url = GlobalData.BASE_URL + "get_categories.php"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                _binding?.let {
                    binding.progressBarHome.visibility = View.GONE
                    Log.d("HomeFragment", "Response: $response")

                    try {
                        val jsonResponse = JSONObject(response)
                        val result = jsonResponse.getString("result")

                        if (result == "success") {
                            val dataArray = jsonResponse.getJSONArray("data")
                            categories.clear()

                            for (i in 0 until dataArray.length()) {
                                val obj = dataArray.getJSONObject(i)
                                val category = Category(
                                    id = obj.getInt("id"),
                                    name = obj.getString("name"),
                                    description = obj.optString("description", ""),
                                    iconUrl = obj.optString("icon_url", "")
                                )
                                categories.add(category)
                            }

                            adapter.notifyDataSetChanged()

                            if (categories.isEmpty()) {
                                binding.txtEmptyHome.visibility = View.VISIBLE
                            } else {
                                binding.txtEmptyHome.visibility = View.GONE
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("HomeFragment", "JSON Parsing Error: ${e.message}")
                    }
                }
            },
            { error ->
                _binding?.let {
                    binding.progressBarHome.visibility = View.GONE
                    Log.e("HomeFragment", "Volley Error: ${error.message}")
                    Toast.makeText(requireContext(), "Gagal memuat kategori ujian", Toast.LENGTH_SHORT).show()
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
        fun newInstance() = HomeFragment()
    }
}
