<?php

require "init.php";
echo 'Le produit a bien été supprimé';

$Product=$_POST['Product'];
$Date=$_POST['Date'];


// ---------------------------------------------------- \\
$sql="DELETE FROM Product where Product='".$Product."';";
$result= mysqli_query($con,$sql);

$sql="SELECT IdDate FROM Date where Date ='".$Date."';";
$result= mysqli_query($con,$sql);
$row= mysqli_fetch_assoc($result);
$IdDate= $row['IdDate'];

$sql="SELECT IdProduct FROM Product where Product='".$Product."';";
$result= mysqli_query($con,$sql);
$row= mysqli_fetch_assoc($result);
$IdProduct= $row['IdProduct'];

$sql="SELECT * FROM Unity where (IdDate='".$IdDate."' AND IdProduct='".$IdProduct."');";
$result= mysqli_query($con,$sql);

if(mysqli_num_rows($result)>0)
{
	$sql="UPDATE Unity SET NbUnity = NbUnity-1 WHERE (IdDate='".$IdDate."' AND IdProduct='".$IdProduct."');";
	$result= mysqli_query($con,$sql);
}
else
{
	// On fait rien!
}


mysqli_close($con);

?>
