
<?php
// 包含在该应用内的函数文件
require_once('bookmark_fns.php');
    require_once('db_fns.php');

    do_html_header('Search Result');
$tag = $_POST['tag'];

   $db = new MySQLi("localhost","root","root","urls");
   $sql = "select * from tags where tag='{$tag}'";

   $result = $db->query($sql);
    while($arr = $result->fetch_row()){
        echo"<a href=\"".$arr[0]."\">".htmlspecialchars($arr[0])."</a>";
        echo "<br />";
    };
    
display_user_menu();
do_html_footer();

?>
