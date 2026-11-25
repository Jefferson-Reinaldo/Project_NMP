package com.example.a38_karhutla.util

import com.example.a38_karhutla.model.User

/**
 * Singleton object murni Kotlin untuk menyimpan data sementara di memori
 * sesuai materi perkuliahan NMP (tanpa menyimpan Context agar tidak memory leak).
 */
object GlobalData {
    // Alamat IP 10.0.2.2 adalah alias emulator Android ke localhost komputer (XAMPP)
    const val BASE_URL = "http://10.0.2.2/examin/"

    // Menyimpan data user yang sedang login saat ini di memori
    var currentUser: User? = null
}
