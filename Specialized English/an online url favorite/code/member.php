
<?php

// 在这个应用中包含函数文件
require_once('bookmark_fns.php');



do_html_header('homepage');

    $url_array=get_user_urls();
    //$note_array=get_user_notes();
   

    display_user_urls($url_array);

display_user_menu();
do_html_footer();
?>
