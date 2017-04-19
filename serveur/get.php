<?php

require "init.php";

$sql="select Product.Product,Date.Date,Product.NbProdIn,Product.NbProdBought from Product left join (Unity,Date) ON (Unity.IdProduct=Product.IdProduct AND Unity.IdDate=Date.IdDate);";
$result= mysqli_query($con,$sql);
$response=array();

while($row=mysqli_fetch_array($result)){

array_push($response,array("sProduct"=>$row["Product"],"sDate"=>$row["Date"],"sStock"=>$row["NbProdIn"],"sHisto"=>$row["NbProdBought"]));

}

echo json_encode($response);

mysqli_close($con);

?>
