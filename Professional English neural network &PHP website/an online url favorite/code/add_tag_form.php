
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>add tag</title>
</head>

<body>
<?php
require_once('bookmark_fns.php');
do_html_header('Add tags');
?>
<form action="add_tag.php" method="post">

<div>url：<input type="text" name="url" /></div>
<div>tag：<input type="text" name="tag" /></div>

<div><input type="submit" value="add the tag" /></div>
</form>

</body>
</html>
