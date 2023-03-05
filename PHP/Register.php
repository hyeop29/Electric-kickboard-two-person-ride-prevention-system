<?php 
    $con = mysqli_connect("localhost", "root", "", "codemonster");
    mysqli_query($con,'SET NAMES utf8');

    $userID =$_POST["userID"];
    $userPassword =$_POST["userPassword"];
    $userName =$_POST["userName"]; 
    $userAge =$_POST["userAge"];
    $userWeight = "0";
    

    $statement = mysqli_prepare($con, "INSERT INTO user_info VALUES (?,?,?,?,?)");
    mysqli_stmt_bind_param($statement, "sssii", $userID, $userPassword, $userName, $userAge, $userWeight);
    mysqli_stmt_execute($statement);


    $response = array();
    $response["success"] = true;
 
   
    echo json_encode($response);
?>
