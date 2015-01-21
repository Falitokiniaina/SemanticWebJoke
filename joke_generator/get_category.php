<?php
/*
echo '["Insults",
      "Animals",
      "Police",
      "Government",
      "Environment",
      "Home",
      "Nationality",
      "Child",
      "Adult"
    ]';
*/
echo str_replace("'","\"",file_get_contents("http://127.0.0.1:8080/get_genres"));
?>