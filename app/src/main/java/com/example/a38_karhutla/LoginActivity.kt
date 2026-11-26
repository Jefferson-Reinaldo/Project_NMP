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
import com.example.a38_karhutla.databinding.ActivityLoginBinding
import com.example.a38_karhutla.model.User
import com.example.a38_karhutla.util.GlobalData
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    companion object {
        const val PREFS_NAME = "ExaminPrefs"
        const val KEY_USER_ID = "key_user_id"
        const val KEY_USERNAME = "key_username"
        const val KEY_NAME = "key_name"
        const val KEY_ROLE = "key_role"
        const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek sesi login tersimpan di SharedPreferences
        checkSavedSession()

        // Tombol Login
        binding.btnLogin.setOnClickListener {
            val username = binding.txtUsername.text.toString().trim()
            val password = binding.txtPassword.text.toString().trim()

            if (username.isEmpty()) {
                binding.layoutUsername.error = "Username tidak boleh kosong"
                return@setOnClickListener
            } else {
                binding.layoutUsername.error = null
            }

            if (password.isEmpty()) {
                binding.layoutPassword.error = "Password tidak boleh kosong"
                return@setOnClickListener
            } else {
                binding.layoutPassword.error = null
            }

            performLogin(username, password)
        }
    }

    private fun checkSavedSession() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

        if (isLoggedIn) {
            val id = prefs.getInt(KEY_USER_ID, 0)
            val username = prefs.getString(KEY_USERNAME, "") ?: ""
            val name = prefs.getString(KEY_NAME, "") ?: ""
            val role = prefs.getString(KEY_ROLE, "user") ?: "user"

            val user = User(id, username, name, role)
            GlobalData.currentUser = user
            navigateToHome(user)
        }
    }

    private fun performLogin(username: String, password: String) {
        // Tampilkan loading
        binding.progressBarLogin.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        // Penggunaan Volley
        val queue = Volley.newRequestQueue(this)
        val url = GlobalData.BASE_URL + "login.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                binding.progressBarLogin.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                Log.d("LoginActivity", "Response: $response")

                try {
                    val jsonResponse = JSONObject(response)
                    val result = jsonResponse.getString("result")

                    if (result == "success") {
                        val dataObj = jsonResponse.getJSONObject("data")
                        val user = User(
                            id = dataObj.getInt("id"),
                            username = dataObj.getString("username"),
                            name = dataObj.getString("name"),
                            role = dataObj.getString("role")
                        )

                        // Simpan sesi ke SharedPreferences
                        saveSession(user)

                        // Simpan ke Singleton memori
                        GlobalData.currentUser = user

                        Toast.makeText(this, "Selamat datang, ${user.name}!", Toast.LENGTH_SHORT).show()
                        navigateToHome(user)
                    } else {
                        val message = jsonResponse.optString("message", "Login gagal")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("LoginActivity", "JSON Parsing Error: ${e.message}")
                    Toast.makeText(this, "Format data server tidak valid: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                binding.progressBarLogin.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                Log.e("LoginActivity", "Volley Error: ${error.message}")
                Toast.makeText(this, "Gagal terhubung ke server. Pastikan XAMPP aktif.", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                params["password"] = password
                return params
            }
        }

        queue.add(stringRequest)
    }

    private fun saveSession(user: User) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_USER_ID, user.id)
        editor.putString(KEY_USERNAME, user.username)
        editor.putString(KEY_NAME, user.name)
        editor.putString(KEY_ROLE, user.role)
        editor.apply()
    }

    private fun navigateToHome(user: User) {
        val intent = if (user.role.equals("admin", ignoreCase = true)) {
            Intent(this, AdminActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish() // Hapus LoginActivity dari backstack
    }
}
