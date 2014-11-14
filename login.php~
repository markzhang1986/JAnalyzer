<?php
if($_SERVER["REQUEST_METHOD"] == "POST")
{
  // username and password sent from Form
  $myusername=$_POST["username"];
  $mypassword=$_POST["password"];

  if($myusername == $mypassword)
  {
    echo "Success";
  } else {
    echo "Failure";
  }

} else {
  
  echo '<form action="" method="post">
  <label>UserName :</label>
  <input type="text" name="username"/><br />
  <label>Password :</label>
  <input type="password" name="password"/><br/>
  <input type="submit" value=" Submit "/><br />
  </form>';
}
?>
