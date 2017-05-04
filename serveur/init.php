<?php

//Merci à Prabeesh pour son tuto sur php et volley
//Page d'accès à la database 
// Wow wow wow toutes les erreurs sont dans /var/log/apache2/error.log


$host="localhost";
$db_user="root";
$db_password="Sql#test";
$db_name="Optali_db";

$con = mysqli_connect($host,$db_user,$db_password,$db_name);
/*
if($con)
{
	echo "<br>C'est un succès!";
}
else 
{
	echo "<br>echo C'est un echec!";
}*/
//mysqli_close($con);

?>
