<!-- Création de la page d'insertion d'aliments -->

<?php

require "init.php";
echo '<br>Page get!!<br>';

// OrderBy = 0 si par nom, 1 si par date, 2 si par Nb dans le frigo, 3 si par Nb en tout
$OrderBy=$_POST['OrderBy'];
$Search=$_POST['Search'];

if($OrderBy==0){
$sql="select Product.Product,Date.Date,Product.NbProdIn,Product.NbProdBought from Product left join (Unity,Date) ON (Unity.IdProduct=Product.IdProduct AND Unity.IdDate=Date.IdDate) where Product.Product='".$Search."%' Order By Product.Product;";
$result= mysqli_query($con,$sql);
}

if($OrderBy==1){
$sql="select Product.Product,Date.Date,Product.NbProdIn,Product.NbProdBought from Product left join (Unity,Date) ON (Unity.IdProduct=Product.IdProduct AND Unity.IdDate=Date.IdDate) where Product.Product='".$Search."%' Order By Date.Date;";
$result= mysqli_query($con,$sql);
}

if($OrderBy==2){
$sql="select Product.Product,Date.Date,Product.NbProdIn,Product.NbProdBought from Product left join (Unity,Date) ON (Unity.IdProduct=Product.IdProduct AND Unity.IdDate=Date.IdDate) where Product.Product='".$Search."%' Order By Product.NbProdIn;";
$result= mysqli_query($con,$sql);
}

if($OrderBy==3){
$sql="select Product.Product,Date.Date,Product.NbProdIn,Product.NbProdBought from Product left join (Unity,Date) ON (Unity.IdProduct=Product.IdProduct AND Unity.IdDate=Date.IdDate) where Product.Product='".$Search."%' Order By Product.NbProdBought;";
$result= mysqli_query($con,$sql);
}

while($row= mysqli_fetch_assoc($result)){
$array[]=$row;
}
echo json_encode($array);

?>
