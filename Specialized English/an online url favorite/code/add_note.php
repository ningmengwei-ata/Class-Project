<!-- //将书签真正添加到数据库的脚本  -->
<?php
require_once('bookmark_fns.php');
     require_once('db_fns.php');


// 创建变量
$url=$_POST['url'];
$note=$_POST['note'];
    $db = new MySQLi("localhost","root","root","urls");
    $sql="insert into note values('$url','$note')";
   

    if($db->query($sql))
    {
        header("location:member.php");
        }
    else{
        echo"failure in adding the note";
        }
display_user_menu();
do_html_footer();
?>
