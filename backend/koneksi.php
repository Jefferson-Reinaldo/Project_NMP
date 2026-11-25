<?php
header("Content-Type: application/json; charset=UTF-8");

$host = "localhost";
$user = "root";
$pass = "";
$db   = "examin_db";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    echo json_encode([
        "result" => "error",
        "message" => "Koneksi database gagal: " . $conn->connect_error
    ]);
    exit();
}
?>