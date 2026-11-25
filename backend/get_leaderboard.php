<?php
require_once "koneksi.php";

$user_id = isset($_REQUEST['user_id']) ? (int)$_REQUEST['user_id'] : 0;

if ($user_id > 0) {
    // Riwayat ujian peserta spesifik
    $stmt = $conn->prepare("SELECT s.id, s.user_id, u.name as username, s.category_id, c.name as category_name, s.score, s.total_questions, s.created_at FROM scores s JOIN users u ON s.user_id = u.id JOIN categories c ON s.category_id = c.id WHERE s.user_id = ? ORDER BY s.id DESC");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $res = $stmt->get_result();
} else {
    // Peringkat skor tertinggi umum (Leaderboard)
    $res = $conn->query("SELECT s.id, s.user_id, u.name as username, s.category_id, c.name as category_name, s.score, s.total_questions, s.created_at FROM scores s JOIN users u ON s.user_id = u.id JOIN categories c ON s.category_id = c.id ORDER BY s.score DESC, s.id DESC LIMIT 50");
}

$scores = [];
if ($res) {
    while ($row = $res->fetch_assoc()) {
        $scores[] = [
            "id" => (int)$row['id'],
            "user_id" => (int)$row['user_id'],
            "username" => $row['username'],
            "category_id" => (int)$row['category_id'],
            "category_name" => $row['category_name'],
            "score" => (int)$row['score'],
            "total_questions" => (int)$row['total_questions'],
            "created_at" => $row['created_at']
        ];
    }
}

echo json_encode([
    "result" => "success",
    "data" => $scores
]);

if (isset($stmt)) $stmt->close();
$conn->close();
?>