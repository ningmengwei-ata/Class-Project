
<?php
require_once('bookmark_fns.php');
//session_start();
   

    $url=$_GET["url"];

    $db=new MySQLi("localhost","root","root","urls");
    $sql="delete from tags where url ='{$url}'";

    if($db->query($sql))
        {
        header("location:member.php");
        }
        else
        {
        echo"Failure in deleting the tag";
        }

    ?>
