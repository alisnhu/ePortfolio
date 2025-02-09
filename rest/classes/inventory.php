<?php
    if(!defined('pagelock') || pagelock!=true) die ('Security breach detected');

class inventory
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
    public function __construct($getdbConn,$getreturnClass)
    {
        $this->dbConn=$getdbConn;
        $this->returnClass=$getreturnClass;
    }

    public function addInventory($inventoryName)
    {

        $auth=$this->authenticateSession();
        if($auth)
        {
             $myInventoryName=securityFilter($inventoryName,$this->dbConn);
             $queryString="insert into inventory (inventory_name,userid) values (";
             $queryString.="'{$myInventoryName}' , ";
             $queryString.="'{$this->parentId}'";
             $queryString.=")";
             $query=$this->dbConn->query($queryString);
             if($query)
             {
                $this->returnClass->status=true;
                $this->returnClass->message="Inventory add Success";
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
            $this->returnClass->message="Authentication Error";
        }

    }



    public function setLock($inventoryId)
    {
        $this->deleteLock();
        $queryString = "
                        UPDATE inventory
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
                        WHERE id = $inventoryId and userid = {$this->parentId};
        ";
        $query=$this->dbConn->query($queryString);       
    }
    public function deleteLock()
    {
        $sqlstring=" UPDATE inventory SET rlock=FALSE , lockowner = NULL ,lockdate=NULL WHERE  NOW()>lockdate";
        $query=$this->dbConn->query($sqlstring);
    }
    public function checkLock($inventoryId)
    {
        $this->deleteLock();
        $sqlstring=" SELECT * , CASE WHEN NOW()>lockdate THEN TRUE ELSE FALSE END AS time_result FROM inventory WHERE  id= $inventoryId";
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


    public function getInvenroty($inventoryId)
    {

        $auth=$this->authenticateSession();
        if($auth)
        {
            $inventoryId=securityFilter($inventoryId,$this->dbConn);
            if($inventoryId==0 || empty($inventoryId) || !is_numeric($inventoryId))
            {
                $sqlstring = "select * from inventory where userid = {$this->parentId}";
                $query=$this->dbConn->query($sqlstring);
                if($query)
                {
                    $this->returnClass->status=true;
                    $this->returnClass->message="inventory list successfully retreived";
                    
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
                $this->setLock($inventoryId);
                $sqlstring = "select * from inventory where id=$inventoryId  and userid = {$this->parentId} limit 1";
                $query=$this->dbConn->query($sqlstring);
              
                if($query)
                {                    
                    if($query->num_rows!=1)
                    {
                        $this->returnClass->status=false;
                        $this->returnClass->message="inventory id not valid";
                    }
                    else
                    {
                        $this->returnClass->status=true;
                        $this->returnClass->message="inventory successfully retreived";
                        $row=$query->fetch_array(MYSQLI_ASSOC);
                        $this->returnClass->result[]=$row;
                        
                    }

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
            $this->returnClass->message="Authentication Error";

        }
    }
    public function deleteInventory($inventoryId)
    {

        $auth=$this->authenticateSession();
        if($auth)
        {
            $isavailable = $this->checkLock($inventoryId);
            if($isavailable)
            {
                $sqlstring =  "delete from inventory where id = $inventoryId and userid = {$this->parentId}";
                print $sqlstring.PHP_EOL;
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
        else
        {
            $this->returnClass->status=false;
            $this->returnClass->message="Authentication Error";

        }
    }
    public function updateInventory($inventoryName,$inventoryId)
    {   
        $auth=$this->authenticateSession();
        if($auth)
        {
            $isavailable = $this->checkLock($inventoryId);
            if($isavailable)
            {
                $sqlstring =  " UPDATE inventory SET inventory_name='$inventoryName',  rlock=FALSE , lockowner =NULL, lockdate=NULL where id = $inventoryId";
                print $sqlstring.PHP_EOL;
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
                $this->returnClass->message="Record Locked By Somone Else";
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
$myinventory =  new inventory($conn,$myReturnClass);
if(!isset($inputData->sessionToken) || empty($inputData->sessionToken))
{
    $myReturnClass ->status=false;
    $myReturnClass ->message="session token can not be empty";
}
else
{
    
    $myinventory->setsessionToken($inputData->sessionToken);
    if($_SERVER['REQUEST_METHOD'] == 'POST')
    {
        //addinf
        if(empty($inputData->inventoryName))
        {
    
            $myReturnClass->status=false;
            $myReturnClass->message="please fill nessesary places";
        }
        else
        {
            $myinventory->addInventory($inputData->inventoryName );
        }
    
    }
    elseif($_SERVER['REQUEST_METHOD'] == 'GET')
    {
       // retreiving
       isset($urls[1]) ? $urls[1] : null;
       $myinventory->getInvenroty( $urls[1]);
    }
    elseif($_SERVER['REQUEST_METHOD'] == 'PATCH')
    {
    
        //updating
       if(!isset($inputData->inventoryName) || empty($inputData->inventoryName) ||!isset($inputData->inventoryID) ||empty($inputData->inventoryID)  )
       {
            
            $myReturnClass->status=false;
            $myReturnClass->message="please fill nessesary places";
       }
       else
       {
            $myinventory->updateInventory($inputData->inventoryName,$inputData->inventoryID);
       }
    }
    elseif($_SERVER['REQUEST_METHOD'] == 'DELETE')
    {
    
        //deleting
        if(!isset($inputData->inventoryID) )
        {
             
             $myReturnClass->status=false;
             $myReturnClass->message="inventory id must be sent";
        }
        else
        {
             $myinventory->deleteInventory($inputData->inventoryID);
        }
    }
    else
    {
    
        $myReturnClass->status=false;
        $myReturnClass->message="Undefined User Operation";
    }
    
    


}

?>