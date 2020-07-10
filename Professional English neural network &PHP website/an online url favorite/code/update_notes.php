<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>update note</title>
</head>

<body>



<?php
    require_once('bookmark_fns.php');
    do_html_header('Update notes');
$url = $_GET["url"];
$db = new MySQLi("localhost","root","root","urls");
$sql = "select * from note where url='{$url}'";

$result = $db->query($sql);
$arr = $result->fetch_row();
?>

<form action="update.php" method="post">


    
    <div>
    <input type="hidden" name="url" value="<?php echo $arr[0]; ?>" />
    </div>
   
    <div>note：
    <input type="text" name="note" value="<?php echo $arr[1]; ?>" />
    </div>
    <div><input type="submit" value="update" /></div>
</form>
</body>
</html>
