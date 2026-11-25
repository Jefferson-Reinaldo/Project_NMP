<?php
require_once "koneksi.php";

$username = isset($_POST['username']) ? trim($_POST['username']) : '';
$password = isset($_POST['password']) ? trim($_POST['password']) : '';

if (empty($username) || empty($password)) {
    echo json_encode([
        "result" => "error",
        "message" => "Username dan password tidak boleh kosong"
    ]);
    exit();
}

$stmt = $conn->prepare("SELECT id, username, name, role FROM users WHERE username = ? AND password = ?");
$stmt->bind_param("ss", $username, $password);
$stmt->execute();
$res = $stmt->get_result();

if ($row = $res->fetch_assoc()) {
    echo json_encode([
        "result" => "success",
        "message" => "Login berhasil",
        "data" => [
            "id" => (int)$row['id'],
            "username" => $row['username'],
            "name" => $row['name'],
            "role" => $row['role']
        ]
    ]);
} else {
    echo json_encode([
        "result" => "error",
        "message" => "Username atau password salah"
    ]);
}

$stmt->close();
$conn->close();
?>