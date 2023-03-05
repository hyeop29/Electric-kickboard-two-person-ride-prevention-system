<?php 
    $con = mysqli_connect("localhost", "root", "", "user_info1");

    mysqli_query($con, "DELETE FROM user ");
    mysqli_close($con);
   

?>
