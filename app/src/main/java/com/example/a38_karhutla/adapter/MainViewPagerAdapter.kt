package com.example.a38_karhutla.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.a38_karhutla.fragment.HomeFragment
import com.example.a38_karhutla.fragment.LeaderboardFragment
import com.example.a38_karhutla.fragment.ProfileFragment

class MainViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment.newInstance()
            1 -> LeaderboardFragment.newInstance()
            2 -> ProfileFragment.newInstance()
            else -> HomeFragment.newInstance()
        }
    }
}
