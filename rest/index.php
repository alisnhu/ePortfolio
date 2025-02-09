<?php
/*
ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);
*/
header('Content-Type: application/json; charset=utf-8');
define("pagelock",true);
require_once("config.php");



$url=securityFilter(@$_GET["url"],$conn);
$urls = normalizeUrl(explode("/",$url));
unset($url);
$method = securityFilter($_SERVER['REQUEST_METHOD'],$conn);



$mainProcess = $urls[0];

switch($mainProcess)
{
    case 'index':
        include_once("classes/scheme.php");
        break;
    case 'user':
        include_once("classes/user.php");
        break;
    case 'subuser':
        include_once("classes/subuser.php");
        break;
    case 'inventory':
        include_once("classes/inventory.php");
        break;
    case 'item':
        include_once("classes/item.php");
        break;
    case 'notification':
        include_once("classes/notification.php");
        break;
    default: 
        print "invalid request error" ;
    break;

}



print json_encode($myReturnClass);



?>