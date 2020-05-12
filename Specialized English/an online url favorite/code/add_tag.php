<!-- //将书签真正添加到数据库的脚本  -->
<?php
require_once('bookmark_fns.php');
     require_once('db_fns.php');


// 创建变量
$url=$_POST['url'];
$tag=$_POST['tag'];
    $db = new MySQLi("localhost","root","root","urls");
    $sql="insert into tags values('$url','$tag')";
   

    if($db->query($sql))
    {
        header("location:member.php");
        }
    else{
        echo"failure in adding the tag";
        }
//try{
//    //check_valid_user();
//    // 判断是否输入为空
//
//    function filled_out($form_vars){
//        //检查每个变量是否有值
//        // foreach遍历数组
//        // isset检查变量是否已经设置并且非null
//        foreach($form_vars as $key =>$value){
//            if ((!isset($key))||($value=='')) {
//                return false;
//            }
//        }
//        return true;
//    }
//    if (!filled_out($_POST)) {
//           throw new Exception('the post have not been completed yet,please complete the post');
//       }
//    // 检查URL是否有效
////    if (!(@fopen($new_url.'r'))) {
////        throw new Exception('this URL is invalid');
////
////    }
//
//    // 添加书签
//    add_tag($new_url,$new_tag);
//    echo 'The tag you submit has been added';
//
//    // 获取用户已保存的书签
//    // url_fns.php>get_user_urls()
//    //if ($url_array=get_user_urls($_SESSION['valid_user'])) {
//        // output_fns.php>display_user_urls;
//
//   // }
//
//}
//catch(Exception $e){
//    echo $e->getMessage();
//}
//display_user_urls($url_array);
display_user_menu();
do_html_footer();
?>
