<?php
    if(!defined('pagelock') || pagelock!=true) die ('Security breach detected');


class notification
{
    private $dbConn;
    private $returnClass;
    private $userId;
    private $sessionToken;
    private $parentId;

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
        $sqlstring="update session set validdate= DATE_ADD(NOW(), INTERVAL ".$GLOBALS["sessiontimeout"]." MINUTE)  where logintoken='".$this->sessionToken."' and child_id={$this->userId} limit 1";
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
                $this->userId = $data["child_id"];
                $this->parentId = $data["userid"];
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
    private function setLock($notificationId,$inventoryid)
    {
        if($this->checkInventory($inventoryid))
        {
            $this->deleteLock();
            $queryString = "
                            UPDATE notification
                            SET 
                                rlock = CASE 
                                            WHEN COALESCE(rlock, FALSE) = FALSE THEN TRUE 
                                            ELSE rlock 
                                        END,
                                lockowner = CASE 
                                                WHEN COALESCE(rlock, FALSE) = FALSE THEN {$this->userId}
                                                WHEN COALESCE(rlock, FALSE) = TRUE AND COALESCE(lockowner,0) = {$this->userId} THEN lockowner 
                                                WHEN COALESCE(rlock, FALSE) = TRUE AND COALESCE(lockowner,0) != {$this->userId}  AND NOW()>COALESCE(lockdate,'2000-01-01 00:00:01') THEN {$this->userId}
                                                ELSE lockowner 
                                            END,
                                lockdate = CASE 
                                                WHEN rlock = COALESCE(rlock, FALSE) THEN NOW() + INTERVAL {$GLOBALS['lockTime']} MINUTE
                                                WHEN rlock = COALESCE(rlock, FALSE) AND COALESCE(lockowner,0) = {$this->userId} THEN NOW() + INTERVAL {$GLOBALS['lockTime']} MINUTE                    
                                                WHEN COALESCE(rlock, FALSE) = TRUE AND COALESCE(lockowner,0) != {$this->userId}  AND NOW()>COALESCE(lockdate,'2000-01-01 00:00:01') THEN NOW() + INTERVAL {$GLOBALS['lockTime']} MINUTE
                                                ELSE lockdate
                                            END
                            WHERE id = $notificationId ;
            ";
            $query=$this->dbConn->query($queryString);  

        }     
    }
    private function deleteLock()
    {
        $sqlstring=" UPDATE notification SET rlock=FALSE , lockowner = NULL ,lockdate=NULL WHERE  NOW()>lockdate";
        $query=$this->dbConn->query($sqlstring);
    }
    private function checkLock($notificationId)
    {
        $this->deleteLock();
        $sqlstring=" SELECT * , CASE WHEN NOW()>lockdate THEN TRUE ELSE FALSE END AS time_result FROM notification WHERE  id= $notificationId";
        $query=$this->dbConn->query($sqlstring);
        if($query)
        {
            $row=$query->fetch_array(MYSQLI_ASSOC);
            if($row["rlock"]==false)
            {
                return true;
            }
            else
            {
                if($row["rlock"]==false)
                {
                    return true;
                }
                else
                {
                    if($row["lockowner"]==$this->userId)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }

            }
        }
        else
        {
            return false;
        }
    }
    public function __construct($getdbConn,$getreturnClass)
    {
        $this->dbConn=$getdbConn;
        $this->returnClass=$getreturnClass;
    }
    private function checkInventory($inventoryid)
    {
        $sqlString="select * from inventory where id=$inventoryid and userid={$this->parentId} limit 1";
        $query=$this->dbConn->query($sqlString);
        if($query)
        {
            if($query->num_rows==1)
            {
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
    public function getNotification($notificationId,$inventoryid)
    {

        $auth=$this->authenticateSession();
        if($auth)
        {
            $notificationId=securityFilter($notificationId,$this->dbConn);
            $inventoryid=securityFilter($inventoryid,$this->dbConn);
            if($notificationId==0 || empty($notificationId) || !is_numeric($notificationId))
            {
                $sqlstring = "select * from notification2 where userid={$this->parentId} and inventoryid=$inventoryid";
                $query=$this->dbConn->query($sqlstring);
                if($query)
                {
                    $this->returnClass->status=true;
                    $this->returnClass->message="notification list successfully retreived";
                    while($row=$query->fetch_array(MYSQLI_ASSOC))
                    {
                        $this->returnClass->result[]=$row;
                    }

                }
                else
                {
                    $this->returnClass->status=false;
                    $this->returnClass->message="Error occured while retreiving list";

                }
            }
            else
            {
                $this->setLock($notificationId,$inventoryid);
                $sqlstring = "select * from notification2 where id=$notificationId and userid={$this->parentId} and inventoryid=$inventoryid limit 1";
                $query=$this->dbConn->query($sqlstring);
                if($query)
                {
                    $this->returnClass->status=true;
                    $this->returnClass->message="item successfully retreived";
                    $row=$query->fetch_array(MYSQLI_ASSOC);
                    $this->returnClass->result[]=$row;

                }
                else
                {
                    $this->returnClass->status=false;
                    $this->returnClass->message="Error occured while retreiving inventory";

                }
            }
        }
        else
        {
            $this->returnClass->status=false;
            $this->returnClass->message="Authentication Error oldu";

        }
    }

    public function addNotification($name,$lessthan,$inventoryid)
    {

        $auth=$this->authenticateSession();
        if($auth)
        {
            $name=securityFilter($name,$this->dbConn);
            $lessthan=securityFilter($lessthan,$this->dbConn);
            $inventoryid=securityFilter($inventoryid,$this->dbConn);
            if($this->checkInventory($inventoryid))
            { 
                $queryString="insert into  notification (name,lessthan,inventoryid) values (";
                $queryString.="'{$name}' , ";
                $queryString.="{$lessthan} , ";
                $queryString.="{$inventoryid}";
                $queryString.=")";
                $query=$this->dbConn->query($queryString);
                if($query)
                {
                   $this->returnClass->status=true;
                   $this->returnClass->message="Item add Success";
                }
                else
                {
                   $this->returnClass->status=false;
                   $this->returnClass->message="Can not be added";
                }

            }
            else
            {
                    
                $this->returnClass->status=false;
                $this->returnClass->message="inventory is not exist";
            }

            

        }
        else
        {

            $this->returnClass->status=false;
            $this->returnClass->message="Authentication Error";
        }

    }
    
    public function deleteNotification($notificationId)
    {
        $auth=$this->authenticateSession();
        if($auth)
        {
            //check ownership
            
            $notificationId=securityFilter($notificationId,$this->dbConn);
            $ownersql= "select * from notification2 where id=$notificationId and userid={$this->parentId} limit 1";
            $ownersql = $this->dbConn->query($ownersql);
            if(!$ownersql || $ownersql->num_rows!=1)
            {
                $this->returnClass->status=false;
                $this->returnClass->message="You are trying to delete an item not exist or you dont have permission on it";
            }
            else
            {
                $isavailable = $this->checkLock($notificationId);
                if($isavailable)
                {
                    $sqlstring =  "delete from notification where id = $notificationId";
                    $query = $this->dbConn->query($sqlstring);
                    if($query)
                    {
                        $this->returnClass->status=true;
                        $this->returnClass->message="Record and related Data deleted";
                    }
                    else
                    {
                        $this->returnClass->status=false;
                        $this->returnClass->message="Record can not delete right now";
                    }
                }
                else
                {
                    $this->returnClass->status=false;
                    $this->returnClass->message="Record Locked By Somone Else";
                }

            }
        }
        else
        {
            $this->returnClass->status=false;
            $this->returnClass->message="Authentication Error";

        }
    }

    
    public function updateNotification($id, $name,$lessthan,$inventoryid)
    {   
        
        $auth=$this->authenticateSession();
        if($auth)
        {
             //check ownership
             $id=securityFilter($id,$this->dbConn);
             $ownersql= "select * from notification2 where id=$id and userid={$this->parentId} limit 1";
             $ownersql = $this->dbConn->query($ownersql);
             if(!$ownersql || $ownersql->num_rows!=1)
             {
                 $this->returnClass->status=false;
                 $this->returnClass->message="You are trying to delete an item not exist or you dont have permission on it";
             }
             else
             {
                $isavailable = $this->checkLock($id);
                if($isavailable)
                {
                    $name=securityFilter($name,$this->dbConn);
                    $lessthan=securityFilter($lessthan,$this->dbConn);
                    $inventoryid=securityFilter($inventoryid,$this->dbConn);
                    if($this->checkInventory($inventoryid))
                    { 
                        $sqlstring = "update notification set name='$name' , rlock=false , lockowner=NULL , lockdate=NULL , lessthan=$lessthan   where id=$id and inventoryid=$inventoryid";
                        $query = $this->dbConn->query($sqlstring);
                        if($query)
                        {
                            $this->returnClass->status=true;
                            $this->returnClass->message="Record updated";
                        }
                        else
                        {
                            $this->returnClass->status=false;
                            $this->returnClass->message="Record can not update right now";
                        }
                    }
                    else
                    {
                            
                        $this->returnClass->status=false;
                        $this->returnClass->message="Invalid input";
                    }
                }
                else
                {
                    $this->returnClass->status=false;
                    $this->returnClass->message="Record Locked By Somone Else";
                }
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
$myNotification = new notification($conn, $myReturnClass);
if(!isset($inputData->sessionToken) || empty($inputData->sessionToken))
{
    $myReturnClass ->status=false;
    $myReturnClass ->message="session token can not be empty";
}
else
{
    $myNotification->setsessionToken($inputData->sessionToken);
    if ($_SERVER['REQUEST_METHOD'] == 'GET') 
    {
        // Retrieving
        $notificationId = isset($urls[1]) ? $urls[1] : null;
        if(!isset($inputData->inventoryId) || empty($inputData->inventoryId))
        {
            $myReturnClass->status = false;
            $myReturnClass->message = "inventoryId is nessesarry";
        }
        else
        {
            $myinventoryid=securityFilter($inputData->inventoryId,$conn);
            $myNotification->getNotification($notificationId,$myinventoryid);
        }
    }
    elseif ($_SERVER['REQUEST_METHOD'] == 'POST') 
    {
        // Adding an item
        if (empty($inputData->name) || empty($inputData->less) || !isset($inputData->inventoryId) || empty($inputData->inventoryId)) 
        {
            $myReturnClass->status = false;
            $myReturnClass->message = "Please fill necessary fields";
        } 
        else 
        {
            $myNotification->addNotification($inputData->name,  $inputData->less, $inputData->inventoryId);
        }
    }
    elseif ($_SERVER['REQUEST_METHOD'] == 'DELETE') 
    {
        // Deleting an item
        if (!isset($inputData->id) || empty($inputData->id) || !is_numeric($inputData->id)) 
        {
            $myReturnClass->status = false;
            $myReturnClass->message = "Please fill necessary fields";
        } 
        else 
        {
            $myNotification->deleteNotification($inputData->id);
        }
    } 
    elseif ($_SERVER['REQUEST_METHOD'] == 'PATCH') 
    {
        // Updating an item
        if (!isset($inputData->id) || empty($inputData->id) || !isset($inputData->name) || empty($inputData->name) || !isset($inputData->inventoryId) || empty($inputData->inventoryId)) 
        {
            $myReturnClass->status = false;
            $myReturnClass->message = "Please fill necessary fields";
        } 
        else 
        {
            $myNotification->updateNotification($inputData->id,$inputData->name, $inputData->less, $inputData->inventoryId);
        }
    } 
    else 
    {
        $myReturnClass->status = false;
        $myReturnClass->message = "Undefined User Operation";
    }
}
?>