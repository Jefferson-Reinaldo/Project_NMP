<?php
require_once "koneksi.php";

$id = isset($_POST['id']) ? (int)$_POST['id'] : 0;

if (empty($id)) {
    echo json_encode([
        "result" => "error",
        "message" => "ID soal tidak valid"
    ]);
    exit();
}

$stmt = $conn->prepare("DELETE FROM questions WHERE id = ?");
$stmt->bind_param("i", $id);

if ($stmt->execute()) {
    echo json_encode([
        "result" => "success",
        "message" => "Soal berhasil dihapus"
    ]);
} else {
    echo json_encode([
        "result" => "error",
        "message" => "Gagal menghapus soal: " . $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>