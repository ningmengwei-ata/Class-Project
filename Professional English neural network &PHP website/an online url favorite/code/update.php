<!-- // 从用户书签列表中删除选定书签的脚本 -->
<?php
require_once('bookmark_fns.php');
    require_once('db_fns.php');
//session_start();
   

   
    $note = $_POST['note'];
    $url=$_POST['url'];
    //echo $note;
    //echo $url;
    //$tag = isset($_POST['tag'])? ($_POST['tag']==''?0:$_POST['tag']) :0;

   
  //  $sql = "SELECT tag_content,tag_id FROM tag WHERE tag_content='$tag'";
   // $result=$mydb->query($sql);

//    if(mysqli_num_rows($result)<=0){
//    $tag_id = md5(uniqid());
//    //insert_values($tag_id,$tag_content[$i]);
//    $sql_a = "insert into tag values('$tag_id','$tag')";
//    $result_a = $mydb->query($sql_a);
//
//    }
//    else{
//      $row = mysqli_fetch_assoc($result);
//      $tag_id=$row['tag_id'];
//    }
    $db = new MySQLi("localhost","root","root","urls");
    $sql = "update note set url='$url',note='$note' where url='$url'";
    
    if($db->query($sql))
    {
        header("location:member.php");
    }
    else
    {
        echo "sorry,the update has failed！";
    }
   // $sql = "update url note = '$note' where url = '$url' ";

  //  $result = $mydb->query($sql);
    // $sql_a="update tag set tag_id ='$tag_id' where tag_content='$tag'";
    // $result_x=$mydb->query($sql_a);
//   $conn=db_connect();
//          if (!$conn->query("update url set note='".$note."' from url where  url='".$url."'")) {
//                 throw new Exception('Sorry,the note cannot be updated');
//
//             }
//          else{
//              header("location:member.php");
//          }
//    if($result){
//        echo json_encode(array(
//            "status"=> "success",
//            "message"=>"update successfully！",
//            "result"=>$result
//        ));
//    }else{
//        echo json_encode(array(
//            "status"=> "fail",
//            "message"=>$result,
//            "sql"=>$sql
//        ));
//    }

   
//// 创建变量
//$update_me=$_POST['update_me'];
////$new_note=$_POST['new_note'];
////$valid_user=$_SESSION['valid_users'];
//
//do_html_header('update notes');
////check_valid_user();
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
//if (!(filled_out($_POST))) {
//    echo '<p>you have not clicked on any note,<br>please try again</p>';
//    display_user_menu();
//    do_html_footer();
//    exit;
//}else{
//    //echo count($del_me);
//    echo count($update_me);
//    if (count($update_me)>0) {
//        foreach($update_me as $note){
//            if (update_note($note,$new_note)) {
//                echo 'update '.$note.'<br>';
//            }else{
//                echo 'failure in updating'.$note.'<br>';
//            }
//        }
//    }else{
//        echo 'you have not choose the note to update';
//    }
//}
//



display_user_menu();
do_html_footer();

?>
