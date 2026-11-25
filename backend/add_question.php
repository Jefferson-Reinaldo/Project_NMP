<?php
require_once "koneksi.php";

$category_id   = isset($_POST['category_id']) ? (int)$_POST['category_id'] : 0;
$question_text = isset($_POST['question_text']) ? trim($_POST['question_text']) : '';
$option_a      = isset($_POST['option_a']) ? trim($_POST['option_a']) : '';
$option_b      = isset($_POST['option_b']) ? trim($_POST['option_b']) : '';
$option_c      = isset($_POST['option_c']) ? trim($_POST['option_c']) : '';
$option_d      = isset($_POST['option_d']) ? trim($_POST['option_d']) : '';
$correct_answer= isset($_POST['correct_answer']) ? strtoupper(trim($_POST['correct_answer'])) : '';
$image_url     = isset($_POST['image_url']) ? trim($_POST['image_url']) : '';
$is_active     = isset($_POST['is_active']) ? (int)$_POST['is_active'] : 1;

if (empty($category_id) || empty($question_text) || empty($option_a) || empty($option_b) || empty($option_c) || empty($option_d) || empty($correct_answer)) {
    echo json_encode([
        "result" => "error",
        "message" => "Semua bidang pertanyaan, opsi, dan kunci jawaban wajib diisi"
    ]);
    exit();
}

$stmt = $conn->prepare("INSERT INTO questions (category_id, question_text, option_a, option_b, option_c, option_d, correct_answer, image_url, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
$stmt->bind_param("isssssssi", $category_id, $question_text, $option_a, $option_b, $option_c, $option_d, $correct_answer, $image_url, $is_active);

if ($stmt->execute()) {
    echo json_encode([
        "result" => "success",
        "message" => "Soal berhasil ditambahkan",
        "inserted_id" => $stmt->insert_id
    ]);
} else {
    echo json_encode([
        "result" => "error",
        "message" => "Gagal menambahkan soal: " . $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>