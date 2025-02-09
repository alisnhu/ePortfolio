<?php
    if(!defined('pagelock') || pagelock!=true) die ('Security breach detected');



class subuser
{
    private $sessionToken;
    private $userId;
    private $childId;
    private $dbConn;
    private $returnClass;
    public function setsessionToken($value)
    {
        $this->sessionToken=securityFilter($value,$this->dbConn);
    }
    private function deleteExpiredSessions()
    {
        $sqlstring="delete from session where validdate< NOW()";
        $query= $this->dbConn->query($sqlstring);
    }
    public function extendSession()
    {
        $sqlstring="update session set validdate= DATE_ADD(NOW(), INTERVAL ".$GLOBALS["sessiontimeout"]." MINUTE)  where logintoken='".$this->sessionToken."' and child_id={$this->childId} limit 1";
        $query=$this->dbConn->query($sqlstring);
    }
    private function authenticateSession()
    {
        $this->deleteExpiredSessions();
        $sqlstring="select * from session where logintoken='".$this->sessionToken."' and validdate> NOW() limit 1";
        $query= $this->dbConn->query($sqlstring);
        if($query)
        {
            if($query->num_rows==1)
            {
                $data=$query->fetch_array(MYSQLI_ASSOC);
                $this->userId = $data["userid"];
                $this->childId = $data["child_id"];
                
                $adminString = "select * from users2 where child_id={$this->childId} and admin=true limit 1";
                $query=$this->dbConn->query($adminString);
                if($query)
                {
                    if($query->num_rows==1)
                    {
                        $this->extendSession();
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }

        }
        else
        {
            return false;
        }
    }

    public function __construct($db,$token,$return)
    {
        $this->dbConn=$db;
        $this->returnClass=$return;
        $this->setsessionToken($token);
    }
    public function getSubUserList()
    {
        $auth=$this->authenticateSession();
        if($auth)
        {
            $sqlString= "select * from users2 where master_id = {$this->userId}";
            $query=$this->dbConn->query($sqlString);
            if($query)
            {
                
                $this->returnClass->status=true;
                $this->returnClass->message="list successfully retreived";
                while($row=$query->fetch_array(MYSQLI_ASSOC))
                {
                    $this->returnClass->result[]=$row;
                }
            }
            else
            {

                $this->returnClass->status=false;
                $this->returnClass->message="list could not be retrieved";
            }
        }
        else
        {
            $this->returnClass->status=false;
            $this->returnClass->message="Authentication Error";
        }
    }
    public function getUser($id)
    {
        $auth=$this->authenticateSession();
        if($auth)
        {
            $sqlString= "select * from users2 where child_id = $id limit 1";
            $query=$this->dbConn->query($sqlString);
            if($query)
            {

                if($query->num_rows==1)
                {
                    $this->returnClass->status=true;
                    $this->returnClass->message="user successfully retreived";
                    $row=$query->fetch_array(MYSQLI_ASSOC);
                    $this->returnClass->result[]=$row;
                    
                }
                else
                {

                    $this->returnClass->status=false;
                    $this->returnClass->message="user id could not be found";
                }

            }
            else
            {
                $this->returnClass->status=false;
                $this->returnClass->message="user could not be retrieved";
            }
        }
        else
        {
            $this->returnClass->status=false;
            $this->returnClass->message="Authentication Error";
        }

    }
    public function addSubuser($user,$pass)
    {
        $auth=$this->authenticateSession();
        if($auth)
        {
            $username=securityFilter($user,$this->dbConn);
            $password=hash('sha256',securityFilter($pass,$this->dbConn));
            $checkUserStr = "select * from child_user where username='$username' limit 1";
            $query=$this->dbConn->query($checkUserStr);
            if(!$query || $query->num_rows==1)
            {

                $this->returnClass->status=false;
                $this->returnClass->message="username exist";
            }
            else
            {
                $sqlStr="insert into child_user (username,password ,master_user) values ('$username','$password',{$this->userId})";
                $query=$this->dbConn->query($sqlStr);
                if($query)
                {
                    $this->returnClass->status=true;
                    $this->returnClass->message="user is successfully added";
                }
                else
                {
                    $this->returnClass->status=false;
                    $this->returnClass->message="user could not be added";
                }
            }

        }
        else
        {
            $this->returnClass->status=false;
            $this->returnClass->message="Authentication Error";
        }
    }
    public function deleteSubUser($id)
    {

        $auth=$this->authenticateSession($id);
        if($auth)
        {
            $deletedUser=securityFilter($id,$this->dbConn);
            $sqlStr = "delete from child_user where id=$deletedUser and admin!=true";
            $query=$this->dbConn->query($sqlStr);
            if(!$query || $this->dbConn->affected_rows==0)
            {
                $this->returnClass->status=false;
                $this->returnClass->message="User is not exist or trying to delete admin user";
            }
            else
            {
                $this->returnClass->status=true;
                $this->returnClass->message="User is deleted";
            }

        }
        else
        {
            $this->returnClass->status=false;
            $this->returnClass->message="Authentication Error";
        }
    }

}

$inputData = json_decode(file_get_contents("php://input"));
$mysubUser =  new subuser($conn,$inputData->sessionToken,$myReturnClass);
if($_SERVER['REQUEST_METHOD'] == 'GET')
{
   // retreiving
   if(!isset($urls[1]) || empty($urls[1])|| $urls[1]==0 || !is_numeric($urls[1]))
   {
        $mysubUser->getSubUserList();
   }
   else
   {
        $mysubUser->getUser($urls[1]);
   }
}
elseif($_SERVER['REQUEST_METHOD'] == 'POST')
{
    if(!isset($inputData->username) || empty($inputData->username) || !isset($inputData->password) || empty($inputData->password))
    {

        $myReturnClass->status=false;
        $myReturnClass ->message="username or password cannot be empty";
    }
    else
    {
        $mysubUser->addSubuser($inputData->username,$inputData->password);
    }
    
}
elseif($_SERVER['REQUEST_METHOD'] == 'DELETE')
{
    if(!isset($inputData->userid) || empty($inputData->userid) || !is_numeric($inputData->userid) || $inputData->userid==0)
    {
        $myReturnClass->status=false;
        $myReturnClass ->message="username cannot be empty";
    }
    else
    {
        $mysubUser->deleteSubUser($inputData->userid);
    }
    
}
elseif($_SERVER['REQUEST_METHOD'] == 'PUT')
{


    if(!isset($inputData->username) || empty($inputData->username) || !isset($inputData->password) || empty($inputData->password) || !isset($inputData->userid) || empty($inputData->userid) || !is_numeric($inputData->userid) || $inputData->userid==0)
    {

        $myReturnClass->status=false;
        $myReturnClass ->message="username or password or userid cannot be empty";
    }
    else
    {
        $mysubUser->deleteSubUser($inputData->userid);
        if($myReturnClass->status==true)
        {
            $mysubUser->addSubuser($inputData->username,$inputData->password);
            if($myReturnClass->status==true)
            {
                $myReturnClass->status=true;
                $myReturnClass ->message="user updated";
            }
            else
            {
                $myReturnClass->status=false;
                $myReturnClass ->message="user can not be updated deleted instead you can create same user again";
            }
        }
        else
        {
            $myReturnClass->status=false;
            $myReturnClass ->message="user can not be updated";
        }
    }
    
}
?>