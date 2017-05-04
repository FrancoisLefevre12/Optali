<?php

require "init.php";

$sql="select Recipe.Recipe,Product.Product from Recipe left join (ListProduct,Product) ON (
ListProduct.IdRecipe=Recipe.IdRecipe AND ListProduct.IdProduct=Product.IdProduct);";
$result= mysqli_query($con,$sql);
$response=array();

while($row=mysqli_fetch_array($result)){

array_push($response,array("sNomRecette"=>$row["Recipe"],"sAliment"=>$row["Product"]));

}

echo json_encode($response);

mysqli_close($con);

?>
