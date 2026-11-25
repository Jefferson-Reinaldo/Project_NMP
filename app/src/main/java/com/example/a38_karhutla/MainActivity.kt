package com.example.a38_karhutla

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.a38_karhutla.adapter.MainViewPagerAdapter
import com.example.a38_karhutla.databinding.ActivityMainBinding
import com.example.a38_karhutla.model.User
import com.example.a38_karhutla.util.GlobalData

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Validasi sesi user
        checkUserSession()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPagerAndNavigation()
    }

    private fun checkUserSession() {
        if (GlobalData.currentUser == null) {
            val prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val isLoggedIn = prefs.getBoolean(LoginActivity.KEY_IS_LOGGED_IN, false)

            if (isLoggedIn) {
                val user = User(
                    id = prefs.getInt(LoginActivity.KEY_USER_ID, 0),
                    username = prefs.getString(LoginActivity.KEY_USERNAME, "") ?: "",
                    name = prefs.getString(LoginActivity.KEY_NAME, "") ?: "",
                    role = prefs.getString(LoginActivity.KEY_ROLE, "user") ?: "user"
                )
                GlobalData.currentUser = user
            } else {
                // Belum login, alihkan ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupViewPagerAndNavigation() {
        // Pasang adapter ViewPager2
        val pagerAdapter = MainViewPagerAdapter(this)
        binding.viewPagerMain.adapter = pagerAdapter

        // Sinkronisasi klik Bottom Navigation ke ViewPager2
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> binding.viewPagerMain.currentItem = 0
                R.id.menu_leaderboard -> binding.viewPagerMain.currentItem = 1
                R.id.menu_profile -> binding.viewPagerMain.currentItem = 2
            }
            true
        }

        // Sinkronisasi geser (swipe) ViewPager2 ke Bottom Navigation
        binding.viewPagerMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.bottomNav.selectedItemId = R.id.menu_home
                    1 -> binding.bottomNav.selectedItemId = R.id.menu_leaderboard
                    2 -> binding.bottomNav.selectedItemId = R.id.menu_profile
                }
            }
        })
    }
}
