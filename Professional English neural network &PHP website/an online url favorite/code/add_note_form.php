<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>无标题文档</title>
</head>

<body>
<?php
require_once('bookmark_fns.php');
do_html_header('Add notes');
?>
<form action="add_note.php" method="post">

<div>url：<input type="text" name="url" /></div>
<div>note：<input type="text" name="note" /></div>

<div><input type="submit" value="add the note" /></div>
</form>

</body>
</html>
