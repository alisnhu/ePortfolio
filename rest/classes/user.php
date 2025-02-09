<?php
    if(!defined('pagelock') || pagelock!=true) die ('Security breach detected');

class user
{

    private $username;
    private $password;
    private $email;
    private $phone;
    private $dbConn;
    private $userId;
    private $childid;

    public function __set($name , $value)
    {
        $this->$name=securityFilter($value,$this->dbConn);
    }

    public function __construct($getDbConn)
    {
        $this->dbConn=$getDbConn;
    }

    public function login($returnClass)
    {

        if(empty($this->username) || empty($this->password))
        {
            $returnClass->status=false;
            $returnClass->message="User Name or password cannot be empty";
        }
        else
        {
            $this->password = hash('sha256', $this->password );
            $quesryString="select * from users2 where username='".$this->username."' and password = '".$this->password."' limit 1";
            $select =  $this->dbConn->query($quesryString);
            if($select)
            {
                if($select->num_rows==1)
                {
                    $results=$select->fetch_array(MYSQLI_ASSOC);
                    $this->userId=$results["master_id"];
                    $this->childId=$results["child_id"];
                    $token=md5(uniqid());
                   if( $this->dbConn->query("insert into session (userid,logintoken,validdate,child_id) values ('".$this->userId."','$token', DATE_ADD(NOW(), INTERVAL ".$GLOBALS["sessiontimeout"]." MINUTE) , '".$this->childId."')"))
                    {
                        $returnClass->status=true;
                        $returnClass->message="Login Success";
                        $returnClass->result = array("sessionToken"=>$token);
                    }
                    else
                    {
                        $returnClass->status=false;
                        $returnClass->message="Session Can not be created";
                    }
                }
                else
                {
                    $returnClass->status=false;
                    $returnClass->message="User Name or password is incorrect";
                }

            }
            else
            {
                $returnClass->status=false;
                $returnClass->message="Error Occured During Database process";
            }
        }
    }

    public function signup($returnClass)
    {
        if(empty($this->username) || empty($this->password) || empty($this->email) || empty($this->phone) )
        {

            $returnClass->status=false;
            $returnClass->message="Username password email and phone must be filled";

        }
        else
        {
            //check username email or phone
            $queryString = "select * from users2 where username='{$this->username}' or email='{$this->email}' or phone='{$this->phone}'";
            $query= $this->dbConn->query($queryString);
            if(!$query)
            {
                
                $returnClass->status=true;
                $returnClass->message="internal server error - db error";
            }
            else
            {

                if($query->num_rows!=0)
                {
                    
                    $returnClass->status=false;
                    $record=$query->fetch_array(MYSQLI_ASSOC);
                    if($record["username"]==$this->username)
                    {
                        $returnClass->message="username already recorded";
                    }
                    elseif($record["email"]==$this->email)
                    {

                        $returnClass->message="email already recorded";
                    }
                    elseif($record["phone"]==$this->phone)
                    {
                        $returnClass->message="phone already recorded";
                    }
                    else
                    {
                        $returnClass->message="unknown error";
                    }

                }
                else
                {
                    $this->password = hash('sha256', $this->password );
                    $this->dbConn->begin_transaction(); 
                    $queryString = "insert into users (email,phone) values ('".$this->email."','".$this->phone."')" ;
                    if( $this->dbConn->query($queryString ))
                    {
                        $masterId=$this->dbConn->insert_id;
                        $sqlString= "insert into child_user (username,password,master_user,admin) values ('{$this->username}','{$this->password}',$masterId,1)";
                        $query= $this->dbConn->query($sqlString);
                        if($query)
                        {
                            $this->dbConn->commit(); 
                            $returnClass->status=true;
                            $returnClass->message="Signup Success Success";
                        }
                        else
                        {
                            $this->dbConn->rollback();
                            $returnClass->status=false;
                            $returnClass->message="user could not be created";
                        }
                    }
                    else
                    {
                        $this->dbConn->rollback();
                        $returnClass->status=false;
                        $returnClass->message="can not register user";
                    }
                }
                
            }    

        }
    }
}
$inputData = json_decode(file_get_contents("php://input"));
$myuser =  new user($conn);
if($_SERVER['REQUEST_METHOD'] == 'PUT')
{
    $myuser->username=$inputData->username;
    $myuser->password=$inputData->password;
    $myuser->email=$inputData->email;
    $myuser->phone=$inputData->phone;
    $myuser->signup($myReturnClass );
}
elseif($_SERVER['REQUEST_METHOD'] == 'GET')
{
    $myuser->username=$inputData->username;
    $myuser->password=$inputData->password;
    $myuser->login($myReturnClass );
}
else
{

    $myReturnClass ->status=false;
    $myReturnClass ->message="Undefined User Operation";
}


?>