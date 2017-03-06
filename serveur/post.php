<!-- Création de la page d'insertion d'aliments -->

<?php

require "init.php";
echo '<br>Page post!!<br>';

$Product=$_POST['Product'];
$Date=$_POST['Date'];



// ---------------------------------------------------- \\
// Pour on regarde si le produit existe déjà
$sql="SELECT * FROM Product where Product='".$Product."';";
$result= mysqli_query($con,$sql);

// Si le produit existe déjà, on ajoute +1 au produit
if(mysqli_num_rows($result)>0)
{

	//On augmente le nombre de ce produit dans le frigo
	$sql="UPDATE Product SET NbProdIn = NbProdIn+1 WHERE Product='".$Product."';";
	$result= mysqli_query($con,$sql);

	$sql="UPDATE Product SET NbProdBought = NbProdBought+1 WHERE Product='".$Product."';";
	$result= mysqli_query($con,$sql);
}
else {
	$sql="insert into Product (Product, NbProdIn, NbProdBought) values('".$Product."','1','1');";
	$result= mysqli_query($con,$sql);
}



//----------------------------------------------------\\
// De même pour la Date
$sql="SELECT * FROM Date where Date='".$Date."';";
$result= mysqli_query($con,$sql);

// Si la date existe déjà, on ajoute +1 à NbDate
if(mysqli_num_rows($result)>0)
{
	$sql="UPDATE Date SET NbDate = NbDate+1 WHERE Date='".$Date."';";
	$result= mysqli_query($con,$sql);
}
else {
	$sql="insert into Date (Date) values('".$Date."');";
	$result= mysqli_query($con,$sql);
}



//-----------------------------------------------------\\
// On récupère les clefs primaires pour rajouter une ligne dans la table Unity
$sql="SELECT IdDate FROM Date where Date ='".$Date."';";
$result= mysqli_query($con,$sql);
$row= mysqli_fetch_assoc($result);
$IdDate= $row['IdDate'];

$sql="SELECT IdProduct FROM Product where Product='".$Product."';";
$result= mysqli_query($con,$sql);
$row= mysqli_fetch_assoc($result);
$IdProduct= $row['IdProduct'];

// On regarde s'il n'existe déjà pas un produit avec la même date de péremption
$sql="SELECT * FROM Unity where (IdDate='".$IdDate."' AND IdProduct='".$IdProduct."');";
$result= mysqli_query($con,$sql);

if(mysqli_num_rows($result)>0)
{
	$sql="UPDATE Unity SET NbUnity = NbUnity+1 WHERE (IdDate='".$IdDate."' AND IdProduct='".$IdProduct."');";
	$result= mysqli_query($con,$sql);
}
else
{
	$sql="insert into Unity (idDate,idProduct,NbUnity) values('".$IdDate."','".$IdProduct."','1');";
	$result= mysqli_query($con,$sql);
}



?>
