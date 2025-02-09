<?php
    if(!defined('pagelock') || pagelock!=true) die ('Security breach detected');
//database setting
$servername = "localhost";
$username = "root";  
$password = "00190300";      
$dbname = "onlineStock";  
//program setting
$sessiontimeout = 10; // minutes
$lockTime = 2;  // minutes

//filter function
function securityFilter($str,$connect)
{
    $return_str=trim($str);
    $return_str = str_replace( array('<','>',"'",'"',')','('), '', $str );
    $return_str = str_ireplace( '%3Cscript', '', $return_str );
    $return_str=filter_var($return_str, FILTER_SANITIZE_STRING);
    $connect->real_escape_string($return_str);
    return $return_str;
}


function normalizeUrl($array)
{
    $rv=array();

    if(count($array)>0)
    {
        foreach ($array as $yeni)
        {
            if(empty($yeni))
            {
                continue;
            }
            else
            {
                $rv[]=$yeni;
            }
        }

    }

    if(count($rv)==0)
    {
        $rv[0]="index";
    }
    return $rv;

}

class returnClass
{
    public $status = false;
    public $message = null;
    public $result = null;
}


$myReturnClass =  new returnClass();


$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) 
{
    $myReturnClass->message="Database Cnnection Error";
}




?>