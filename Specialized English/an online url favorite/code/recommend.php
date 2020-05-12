<?php
 require_once('bookmark_fns.php');

session_start();

do_html_header('Recommending URLS');
try {
    check_valid_user();
    $urls = recommend_urls($_SESSION['valid_user'], 1);
    display_recommended_urls($urls);
}catch(Exception $e) {
    echo $e -> getMessage();
}

 display_user_menu();
 do_html_footer();
?>
