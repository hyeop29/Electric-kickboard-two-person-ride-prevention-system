<?php 
    $con = mysqli_connect("localhost", "root", "", "user_info1");
    mysqli_query($con,'SET NAMES utf8');

    $userID =$_POST["userID"];

   

    $statement = mysqli_prepare($con, "INSERT INTO user VALUES (?)");
    mysqli_stmt_bind_param($statement, "s", $userID);
    mysqli_stmt_execute($statement);


    $response = array();
    $response["success"] = true;
 
   
    echo json_encode($response);
?>
