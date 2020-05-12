
<?php
require_once('bookmark_fns.php');
//session_start();

// 创建变量
$del_me = $_POST['del_me'];
//$valid_user=$_SESSION['valid_users'];
do_html_header('delete /update');
//check_valid_user();
    function filled_out($form_vars){
        //检查每个变量是否有值
        // foreach遍历数组
        // isset检查变量是否已经设置并且非null
        foreach($form_vars as $key =>$value){
            if ((!isset($key))||($value=='')) {
                return false;
            }
        }
        return true;
    }
if (!(filled_out($_POST))) {
    echo '<p>you have not clicked on any url,<br>please try again</p>';
    display_user_menu();
    do_html_footer();
    exit;
}else{
    //echo $del_me;
    if (count($del_me)>0) {
        foreach($del_me as $url){
            if (delete_url($url)) {
                echo 'delete '.htmlspecialchars($url).'<br>';
            }else{
                echo 'failure in deleting'.htmlspecialchars($url).'<br>';
            }
        }
    }else{
       // echo 'you have not choose the url to delete';
    }    
}
//    display_update_form();
//    //$update_me=$_POST['update_me'];
//
//    $url=$_POST['url'];
//    $new_note=$_POST['new_note'];
//    echo $new_note;
//      // echo count($update_me);
//        if (update_note($url,$new_note)) {
//                          echo 'update '.$note.'<br>';
//                     }else{
//                          echo 'failure in updating'.$note.'<br>';
//                      }
////      if (count($update_me)>=0) {
////           display_update_form();
////
////            echo $new_note;
////           foreach($update_me as $note){
////              if (update_note($url,$new_note)) {
////                   echo 'update '.$note.'<br>';
////              }else{
////                   echo 'failure in updating'.$note.'<br>';
////               }
////           }
////      }else{
////           echo 'you have not choose the note to update';
////      }
//
//
//

display_user_menu();
do_html_footer();

?>

