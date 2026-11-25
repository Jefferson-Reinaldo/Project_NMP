-- Skema Database untuk Aplikasi Ujian Examin (38_karhutla)
CREATE DATABASE IF NOT EXISTS examin_db;
USE examin_db;

-- 1. Tabel Users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role ENUM('user', 'admin') DEFAULT 'user'
);

-- 2. Tabel Kategori Ujian
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255)
);

-- 3. Tabel Bank Soal
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_answer CHAR(1) NOT NULL, -- 'A', 'B', 'C', atau 'D'
    image_url VARCHAR(255) DEFAULT '',
    is_active TINYINT(1) DEFAULT 1,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- 4. Tabel Riwayat Skor Ujian
CREATE TABLE IF NOT EXISTS scores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Seeder Data Awal
INSERT INTO users (username, password, name, role) VALUES
('admin', 'admin123', 'Administrator', 'admin'),
('budi', 'budi123', 'Budi Santoso', 'user'),
('siti', 'siti123', 'Siti Rahma', 'user')
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO categories (name, description, icon_url) VALUES
('Pemrograman Kotlin', 'Ujian dasar logika dan sintaks Kotlin', 'https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/kotlin/kotlin.png'),
('Android Fundamental', 'Ujian konsep Activity, Intent, dan Layout', 'https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/android/android.png'),
('Karhutla & Lingkungan', 'Ujian edukasi pencegahan kebakaran hutan dan lahan', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Fire_icon.svg/240px-Fire_icon.svg.png')
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO questions (category_id, question_text, option_a, option_b, option_c, option_d, correct_answer, image_url, is_active) VALUES
(1, 'Manakah keyword di Kotlin yang digunakan untuk mendeklarasikan variabel read-only (immutable)?', 'var', 'val', 'const', 'let', 'B', '', 1),
(1, 'Keyword apa yang digunakan untuk mendefinisikan fungsi di Kotlin?', 'function', 'def', 'fun', 'void', 'C', '', 1),
(1, 'Apa nama fitur di Kotlin yang memungkinkan penyisipan variabel langsung ke dalam String dengan tanda $?', 'String Interpolation', 'String Concatenation', 'String Templates', 'String Injection', 'C', '', 1),
(2, 'Komponen Android mana yang digunakan untuk menampilkan antarmuka layar pengguna?', 'Service', 'BroadcastReceiver', 'Activity', 'ContentProvider', 'C', '', 1),
(2, 'Library resmi yang diajarkan untuk memuat gambar dari URL adalah...', 'Glide', 'Picasso', 'Coil', 'Fresco', 'B', '', 1),
(2, 'Jenis Intent apa yang digunakan untuk membuka aplikasi lain seperti Google Maps atau Dialer?', 'Explicit Intent', 'Implicit Intent', 'Pending Intent', 'Broadcast Intent', 'B', '', 1),
(3, 'Apa kepanjangan dari istilah Karhutla?', 'Kebakaran Hutan dan Lembah', 'Kebakaran Hutan dan Lahan', 'Kebakaran Hutan dan Lingkungan', 'Kawasan Hutan dan Lahan', 'B', '', 1),
(3, 'Jenis tanah berikut yang paling rentan terbakar dan sulit dipadamkan saat kemarau adalah...', 'Tanah Liat', 'Tanah Pasir', 'Tanah Gambut', 'Tanah Kapur', 'C', '', 1);
