<!-- Merci à Prabeesh pour son tuto sur php et volley -->
<!-- Page d'accès à la database -->

<?php

$host="http://192.168.1.1";
$db_user="alex";
$db_password="password";
$db_name="Optali_db";

$con = mysqli_connect($host,$db_user,$db_password,$db_name);

if($con)
{
	echo "C'est un succès!"
}
else 
{
	echo "C'est un echec!"
}
//mysqli_close($con);
?>
