<!-- // 增加和删除书签以及推荐的函数 -->
<?php
require_once('db_fns.php');

function get_user_urls(){
    // 从数据库中提取该用户储存的所有URL

    $conn=db_connect();
    $result=$conn->query("select URL from url ");
    
    if (!$result) {
        return false;
    }
    // 创建一个URL数组
    $url_array=array();
    for($count=1;$row=$result->fetch_row();++$count){
        $url_array[$count]=$row[0];
    }
    return $url_array;
}
    function get_user_notes($url){
        // 从数据库中提取该用户储存的所有note

        $conn=db_connect();
         $result=$conn->query("select note from note  where url='".$url."'");
        
        if (!$result) {
            return false;
        }
        // 创建一个note数组
        $note_array=array();
        for($count=1;$row=$result->fetch_row();++$count){
            $note_array[$count]=$row[0];
        }
        return $note_array;
    }
    function get_user_tags($url){
        // 从数据库中提取该用户储存的所有note

        $conn=db_connect();
        $result=$conn->query("select tag from tags  where url='".$url."'");
        
        if (!$result) {
            return false;
        }
        // 创建一个note数组
        $tag_array=array();
        for($count=1;$row=$result->fetch_row();++$count){
            $tag_array[$count]=$row[0];
        }
        return $tag_array;
    }
function add_url($new_url){
    // 添加新标签到数据库
    //htmlspecialchars将字符串以html输出
    echo "Trying to add ".htmlspecialchars($new_url)."<br />";

   // $valid_user=$_SESSION['valid_user'];

    $conn=db_connect();

    // 检查是否为重复的标签
    $result=$conn->query("select * from url where URL='".$new_url."'");
    if ($result&&($result->num_rows>0)) {
        throw new Exception('We have this url in this favorite already!');
        
    }

    // 添加新url
    if (!$conn->query("insert into url (URL) values ('".$new_url."')")) {
        throw new Exception('Failure in adding the url');
        
    }
    return;
}
    function add_tag($new_url,$new_tag){
        // 添加新标签到数据库
        //htmlspecialchars将字符串以html输出
        echo "Trying to add the tag<br />";
         echo $new_tag;
       // $valid_user=$_SESSION['valid_user'];

        $conn=db_connect();

        // 添加新url
        if (!$conn->query("insert into tags (url,tag) values ('".$new_url."','".$new_tag."')")) {
            throw new Exception('Failure in adding the tag');
        }
       
        return;
    }
    function add_note($url,$new_note){
        
        //htmlspecialchars将字符串以html输出
        echo "Trying to add ".htmlspecialchars($new_note)."<br />";

       // $valid_user=$_SESSION['valid_user'];

        $conn=db_connect();

        // 检查是否为重复的标签
        $result=$conn->query("select * from url where note='".$new_note."'");

        // 添加新comment
        if (!$conn->query("insert into url  values ('".$url."', '".addslashes($new_note)."')")) {
            throw new Exception('Failure in adding the comment');
            
        }
        return;
    }

function delete_url($url){
    // 删除数据库中一个URL
    $conn=db_connect();

    
    if (!$conn->query("delete from url where  URL='".$url."'")) {
        throw new Exception('Sorry,the url cannot be deleted');
        
    }
    return true;
}
function update_note($url,$new_note){
        $conn=db_connect();
        if (!$conn->query("update url set note='".$new_note."' from url where  URL='".$url."'")) {
               throw new Exception('Sorry,the note cannot be updated');
               
           }
           return true;
    }
function delete_note($note){
        $conn=db_connect();
        if (!$conn->query("delete from url where  note='".$note."'")) {
               throw new Exception('Sorry,the note cannot be deleted');
           }
           return true;
    }
?>
