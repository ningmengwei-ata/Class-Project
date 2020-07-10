<!-- //将书签真正添加到数据库的脚本  -->
<?php
require_once('bookmark_fns.php');
session_start();

// 创建变量
$new_url=$_POST['new_url'];

do_html_header('add urls');

try{
    //check_valid_user();
    // 判断是否输入为空
    if (!filled_out($_POST)) {
        throw new Exception('the post have not been completed yet,please complete the post');
    }

    // 检查URL格式 ===同时检查值和类型 ==只检查值
    if (strstr($new_url,'https://')===false) {
        $new_url='https://'.$new_url;
    }

    // 检查URL是否有效
    if (!(@fopen($new_url.'r'))) {
        throw new Exception('this URL is invalid');
        
    }

    // 添加书签
    add_url($new_url);
    echo 'The url you submit has been added';

    // 获取用户已保存的书签
    // url_fns.php>get_user_urls()
    //if ($url_array=get_user_urls($_SESSION['valid_user'])) {
        // output_fns.php>display_user_urls;
        display_user_urls($url_array);
   // }
    
}
catch(Exception $e){
    echo $e->getMessage();
}
display_user_menu();
do_html_footer();
?>
