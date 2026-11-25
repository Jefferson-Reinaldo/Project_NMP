<?php
require_once "koneksi.php";

$user_id         = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0;
$category_id     = isset($_POST['category_id']) ? (int)$_POST['category_id'] : 0;
$score           = isset($_POST['score']) ? (int)$_POST['score'] : 0;
$total_questions = isset($_POST['total_questions']) ? (int)$_POST['total_questions'] : 0;

if (empty($user_id) || empty($category_id)) {
    echo json_encode([
        "result" => "error",
        "message" => "Data user_id dan category_id wajib dikirim"
    ]);
    exit();
}

$stmt = $conn->prepare("INSERT INTO scores (user_id, category_id, score, total_questions) VALUES (?, ?, ?, ?)");
$stmt->bind_param("iiii", $user_id, $category_id, $score, $total_questions);

if ($stmt->execute()) {
    echo json_encode([
        "result" => "success",
        "message" => "Skor berhasil disimpan",
        "score_id" => $stmt->insert_id
    ]);
} else {
    echo json_encode([
        "result" => "error",
        "message" => "Gagal menyimpan skor: " . $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>