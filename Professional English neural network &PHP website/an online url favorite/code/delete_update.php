<!-- // 添加新书签脚本 -->
<?php
// 包含在该应用内的函数文件
require_once('bookmark_fns.php');
//session_start();

// 开始输出html
do_html_header('delete/update notes');


display_update_form();

//display_user_menu();
do_html_footer();

?>
