
<?php
    function do_html_header($title){
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><?php echo $title;?></title>
    <style>
      body { font-family: Arial, Helvetica, sans-serif; font-size: 13px }
      li, td { font-family: Arial, Helvetica, sans-serif; font-size: 13px }
      hr { color: #3333cc;}
      a { color: #000 }
      div.formblock
         { background: #ccc; width: 300px; padding: 6px; border: 1px solid #000;}
    </style>
</head>
<body>
<div>
    <img  src="favorite.png" alt="PHPfavorite logo" height="55" width="57" style="float:left; padding_right:6px;"/>

    <h1>MY FAVORITE</h1>
    
</div>
    <hr/>
    <?php
        if ($title) {
            do_html_heading($title);
    }
}
function do_html_footer(){
    ?>
    </body>
</html>
<?php
}

function do_html_heading($heading){
    ?>
    <h2><?php echo $heading;?></h2>
    <?php
}

function display_site_info(){
    ?>
    <ul>
    <li>This website offer you a way to store your urls</li>
    <li>You can delete and add urls </li>
    <li>You can add tags to the urls and sort by tags</li>
    <li>You can delete and add comments to the urls </li>
    <li> Wish you a happy time!</li>
    </ul>
<?php
}


function display_user_urls($url_array){
    //显示网址集合

    //设置全局变量，我们可以稍后测试它是否在页面上
    global $url_table;
    $url_table = true;
    $len=count($url_array);
    
?>
    
    
       <form method="post" action="search_by_tag.php">
          <div>tag：
          <input type="text" name="tag"  /></div>
          <div><input type="submit" value="query" /></div>
        </form>
   
     
    <br>
    <form name="url_table" action="delete_url.php" method="post">
   
    <table width="300" cellpadding="2" cellspacing="0">
    <?php
    $color="#cccccc";
    echo "<tr bgcolor=\"".$color."\"><td><strong>urls</strong></td>";
    echo "<td><strong>delete？</strong></td><td><strong>note </strong></td><td><strong> update note？ </strong></td><td><strong> delete note？ </strong></td><td><strong> tag</strong></td><td><strong> delete tag?</strong></td></tr>";
    for($i=1;$i<=$len; $i++){
        echo "<tr>";
        echo "<td><a href=\"".$url_array[$i]."\">".htmlspecialchars($url_array[$i])."</a></td>";
        echo "<td><input type=\"checkbox\" name=\"del_me[]\"
               value=\"".$url_array[$i]."\" ></td>";
        $note_array=get_user_notes($url_array[$i]);

               echo"<td>";
               foreach($note_array as $note){
                   echo $note;
                   echo " ";
               }
               echo"</td>";
        echo"<td><a href='update_notes.php?url={$url_array[$i]}'>update</a></td>";
        echo"<td><a href='delete_note.php?url={$url_array[$i]}'>delete</a></td>";
         $tag_array=get_user_tags($url_array[$i]);

        echo"<td>";
        foreach($tag_array as $tag){
            echo $tag;
            echo "/";
        }
        echo"</td>";
         echo"<td><a onclick=\"return confirm('Are you sure you want to delete the tag of this url?')\" href='delete_tag.php?url={$url_array[$i]}'>delete </a></td>";
        echo "</tr>";
    }
//    if ((is_array($url_array)) && (count($url_array)>0) ){
//        foreach($url_array as $url ){
////            if ($color == "cccccc") {
////                $color="#ffffff";
////            }else{
////                $color = "#cccccc";
////            }
//
//            //记得在我们显示用户数据时调用htmlspecialchars（）
//            echo "<tr bgcolor=\"".$color."\"><td><a href=\"".$url."\">".htmlspecialchars($url)."</a></td>
//            <td><input type=\"checkbox\" name=\"del_me[]\"
//                value=\"".$url."\"></td>
//
//               </tr>";
//    }
//
//  } else {
//    echo "<tr><td>No urls on record</td></tr>";
//  }
//    if ((is_array($note_array)) && (count($note_array)>0 )){
//           foreach($note_array as $note){
//               if ($color == "cccccc") {
//                   $color="#ffffff";
//               }else{
//                   $color = "#cccccc";
//               }
//               //记得在我们显示用户数据时调用htmlspecialchars（）
//               echo "<tr bgcolor=\"".$color."\"><td><a href=\"".$note."\">".htmlspecialchars($url)."</a></td>
//               <td><input type=\"checkbox\" name=\"del_me[]\"
//                   value=\"".$url."\"></td>
//               </tr>";
//       }
//     } else {
//       echo "<tr><td>No notes on record</td></tr>";
//     }
?>
  </table>
  </form>
    
<?php
}
          

function display_user_menu() {
  //显示此页面上的菜单选项
?>
<hr>
<a href="member.php">homepage</a> &nbsp;|&nbsp;
<a href="add_url_form.php">add url</a> &nbsp;|&nbsp;
<?php
  //如果此页面上有书签，则仅提供删除选项
  global $url_table;
  if ($url_table == true) {
    echo "<a href=\"#\" onClick=\"url_table.submit();\">delete  urls</a> &nbsp;|&nbsp;";
  } else {
    echo "<span style=\"color: #cccccc\">delete url</span> &nbsp;|&nbsp;";
  }
?>
    <a href="add_note_form.php">add notes</a> &nbsp;|&nbsp;
<a href="add_tag_form.php">add tags</a> &nbsp;|&nbsp;
<a href="recommend.php">recommend site</a> &nbsp;|&nbsp;
<hr>

<?php
}

function display_add_url_form() {
  //显示用户输入新书签的表单
?>
<form name="url_table" action="add_url.php" method="post">

 <div class="formblock">
    <h2>Adding New Url</h2>

    <p>
    <input type="text" name="new_url" id="new_url" 
      size="40"  maxlength="255" value="" required />
    

    <button type="submit">add url</button>

   </div>

</form>
<?php
}
          function display_add_tag_form() {
            //显示用户输入新书签的表单
          ?>
          <form name="url_table" action="add_tag.php" method="post">

           <div class="formblock">
              <h2>Adding New Url</h2>

          <div>url:
               <input type="text" name="url" value="" />
               </div>
              
               <div>note：
               <input type="text" name="tag" value="" />
               </div>
              <button type="submit">add tag</button>

             </div>

          </form>
          <?php
          }
function display_update_form() {
            //显示用户输入新书签的表单
?>
 <form name="url_table" action="delete_url.php" method="post">

 <div class="formblock">
 <h2>Delete/Update</h2>

<p>
    <input type="text" name="url" id="url"
    size="40"  maxlength="255" value="" required />
<input type="text" name="new_note" id="new_note"
 size="40"  maxlength="255" value="" required />
</p>

<button type="submit">delete/update</button>

 </div>

</form>
  <?php
 }


function display_recommended_urls($url_array) {
    //输出display_user_urls
    //而不是显示用户书签，显示推荐
?>
  <br>
  <table width="300" cellpadding="2" cellspacing="0">
<?php
  $color = "#cccccc";
  echo "<tr bgcolor=\"".$color."\">
        <td><strong>Recommendations</strong></td></tr>";
  if ((is_array($url_array)) && (count($url_array)>0)) {
    foreach ($url_array as $url) {
      if ($color == "#cccccc") {
        $color = "#ffffff";
      } else {
        $color = "#cccccc";
      }
      echo "<tr bgcolor=\"".$color."\">
            <td><a href=\"".$url."\">".htmlspecialchars($url)."</a></td></tr>";
    }
  } else {
    echo "<tr><td>No recommendations for you today.</td></tr>";
  }
?>
  </table>
<?php
}

?>
