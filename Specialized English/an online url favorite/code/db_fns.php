
<?php

function db_connect(){
    $result=new mysqli('localhost','root','root','urls');
    if (!$result) {
        throw new Exception("无法连接数据库");
    }
        
   
    return $result;
}
?>
