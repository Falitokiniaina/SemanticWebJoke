<?php
	$topic = $_GET['topic'];
	echo file_get_contents("http://127.0.0.1:8080/generate?topic=".$topic);
?>