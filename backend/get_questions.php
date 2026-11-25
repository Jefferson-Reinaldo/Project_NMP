<?php
require_once "koneksi.php";

$category_id = isset($_REQUEST['category_id']) ? (int)$_REQUEST['category_id'] : 0;

if ($category_id > 0) {
    // Untuk peserta ujian: ambil hanya soal yang aktif dalam kategori tersebut
    $stmt = $conn->prepare("SELECT id, category_id, question_text, option_a, option_b, option_c, option_d, correct_answer, image_url, is_active FROM questions WHERE category_id = ? AND is_active = 1 ORDER BY id ASC");
    $stmt->bind_param("i", $category_id);
    $stmt->execute();
    $res = $stmt->get_result();
} else {
    // Untuk Admin: ambil seluruh bank soal
    $res = $conn->query("SELECT id, category_id, question_text, option_a, option_b, option_c, option_d, correct_answer, image_url, is_active FROM questions ORDER BY id DESC");
}

$questions = [];
if ($res) {
    while ($row = $res->fetch_assoc()) {
        $questions[] = [
            "id" => (int)$row['id'],
            "category_id" => (int)$row['category_id'],
            "question_text" => $row['question_text'],
            "option_a" => $row['option_a'],
            "option_b" => $row['option_b'],
            "option_c" => $row['option_c'],
            "option_d" => $row['option_d'],
            "correct_answer" => $row['correct_answer'],
            "image_url" => $row['image_url'] ? $row['image_url'] : '',
            "is_active" => (int)$row['is_active']
        ];
    }
}

echo json_encode([
    "result" => "success",
    "data" => $questions
]);

if (isset($stmt)) $stmt->close();
$conn->close();
?>