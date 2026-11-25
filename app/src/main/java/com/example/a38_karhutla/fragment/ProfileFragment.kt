package com.example.a38_karhutla.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.a38_karhutla.LoginActivity
import com.example.a38_karhutla.databinding.FragmentProfileBinding
import com.example.a38_karhutla.util.GlobalData

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayUserProfile()

        // Tombol Logout dengan konfirmasi AlertDialog
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
                .setPositiveButton("Ya, Keluar") { _, _ ->
                    performLogout()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        displayUserProfile()
    }

    private fun displayUserProfile() {
        val user = GlobalData.currentUser
        if (user != null) {
            binding.txtProfileName.text = user.name
            binding.txtProfileUsername.text = "@${user.username} • ${user.role.replaceFirstChar { it.uppercase() }}"
            binding.txtAccountRole.text = "${user.role.replaceFirstChar { it.uppercase() }} Aktif"
        }

        // Baca skor tertinggi dari SharedPreferences
        val prefs = requireContext().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val highScore = prefs.getInt("key_high_score", 0)
        binding.txtHighScoreValue.text = "$highScore Poin"
    }

    private fun performLogout() {
        // Hapus sesi login di SharedPreferences
        val prefs = requireContext().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Reset singleton memori
        GlobalData.currentUser = null

        // Kembali ke LoginActivity dan bersihkan backstack
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
