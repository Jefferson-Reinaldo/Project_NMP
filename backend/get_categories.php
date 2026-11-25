<?php
require_once "koneksi.php";

$sql = "SELECT id, name, description, icon_url FROM categories ORDER BY id ASC";
$res = $conn->query($sql);

$categories = [];
if ($res) {
    while ($row = $res->fetch_assoc()) {
        $categories[] = [
            "id" => (int)$row['id'],
            "name" => $row['name'],
            "description" => $row['description'],
            "icon_url" => $row['icon_url']
        ];
    }
}

echo json_encode([
    "result" => "success",
    "data" => $categories
]);

$conn->close();
?>