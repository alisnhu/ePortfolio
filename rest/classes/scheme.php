<?php
    if(!defined('pagelock') || pagelock!=true) die ('Security breach detected');
/**
 * 
 * 
 * This Page will serve as a manuel about web service all classes methods how to use them return value expoected values etc.
 * becuse rest douesnt provide a wsdl like soap we need to find a way to inform users how to use this database
 * 
 * 
*/
    $myReturnClass->status=true;
    $myReturnClass->message="This Page shows classes and how to use";
    $myReturnClass->result=array();

    //add user login
    $add=array(
        "url"=>"/user",
        "method"=>array(
            "get"=>array(
                "explination"=>"login to system",
                "fields"=>"username -string  , password -string",
                "return-field"=>"status - boolean(true success , false falied) , message -string (explanation), result array(sessionToken -string)"
            ),
            "post"=>array(
                "explination"=>"signup to system",
                "fields"=>"username -string , password -string , phone -string, email -string",
                "return-field"=>"status - boolean(true success , false falied) , message -string (explanation), result null"
            ),
        )


    );
    $myReturnClass->result[]=$add;

    
    //subuser
    $add = array(
        "url" => "/subuser",
        "method" => array(
            "get" => array(
                "explanation" => "Retrieve the list of subusers or specific subuser details",
                "fields" => "sessionToken - string, userId (optional) - int",
                "return-field" => "status - boolean (true if success, false if failed), message - string (explanation), result - array (user details)"
            ),
            "post" => array(
                "explanation" => "Add a new subuser",
                "fields" => "sessionToken - string, username - string, password - string",
                "return-field" => "status - boolean (true if success, false if failed), message - string (explanation), result - null"
            ),
            "delete" => array(
                "explanation" => "Delete an existing subuser",
                "fields" => "sessionToken - string, userId - int",
                "return-field" => "status - boolean (true if success, false if failed), message - string (explanation), result - null"
            ),
            "put" => array(
                "explanation" => "Update an existing subuser's username and password",
                "fields" => "sessionToken - string, userId - int, username - string, password - string",
                "return-field" => "status - boolean (true if success, false if failed), message - string (explanation), result - null"
            ),
        )
    );
    $myReturnClass->result[]=$add;


    $add = array(
        "url" => "/inventory",
        "method" => array(
            "get" => array(
                "explination" => "Retrieve inventory list or a specific inventory item",
                "fields" => "sessionToken -string, inventoryID -int (optional, for specific item)",
                "return-field" => "status - boolean(true success, false failed), message - string (explanation), result - array (inventory data)"
            ),
            "post" => array(
                "explination" => "Add a new inventory item",
                "fields" => "sessionToken -string, inventoryName -string",
                "return-field" => "status - boolean(true success, false failed), message - string (explanation), result null"
            ),
            "patch" => array(
                "explination" => "Update an existing inventory item",
                "fields" => "sessionToken -string, inventoryName -string, inventoryID -int",
                "return-field" => "status - boolean(true success, false failed), message - string (explanation), result null"
            ),
            "delete" => array(
                "explination" => "Delete an inventory item",
                "fields" => "sessionToken -string, inventoryID -int",
                "return-field" => "status - boolean(true success, false failed), message - string (explanation), result null"
            )
        )
    );

    $myReturnClass->result[]=$add;

    $add = array(
        "url" => "/item",
        "method" => array(
            "get" => array(
                "explanation" => "Retrieve all items",
                "fields" => "userId - int (User's ID)",
                "return-field" => "status - boolean(true success, false failed), message - string (explanation), result - array of items (id - int, name - string, location - string, stock - int, itemNotification - boolean, lessthan - int, inventoryId - int, userId - int)"
            ),
            "post" => array(
                "explanation" => "Add a new item",
                "fields" => "name - string, location - string, stock - int, itemNotification - boolean, lessthan - int, inventoryId - int, userId - int",
                "return-field" => "status - boolean(true success, false failed), message - string (explanation), result - null"
            ),
            "delete" => array(
                "explanation" => "Delete an item",
                "fields" => "id - int (Item's ID)",
                "return-field" => "status - boolean(true success, false failed), message - string (explanation), result - null"
            ),
            "put" => array(
                "explanation" => "Update item details",
                "fields" => "id - int (Item's ID), name - string, location - string, stock - int, itemNotification - boolean, lessthan - int, inventoryId - int, userId - int",
                "return-field" => "status - boolean(true success, false failed), message - string (explanation), result - null"
            ),
        )
    );

    
    $myReturnClass->result[]=$add;


    $add = array(
        "url" => "/notification",
        "method" => array(
            "get" => array(
                "explanation" => "Retrieve notifications for an inventory or a specific notification",
                "fields" => array(
                    "sessionToken - string (required)",
                    "inventoryId - int (required)",
                    "notificationId - int (optional, in URL path)"
                ),
                "return-field" => "status - boolean (true success, false failed),message - string (explanation), 
                                  result - array of notifications (
                                      id - int,
                                      name - string,
                                      lessthan - int,
                                      inventoryid - int,
                                      rlock - boolean,
                                      lockowner - int,
                                      lockdate - datetime
                                  )"
            ),
            "post" => array(
                "explanation" => "Add a new notification",
                "fields" => array(
                    "sessionToken - string (required)",
                    "name - string (required)",
                    "less - int (required)",
                    "inventoryId - int (required)"
                ),
                "return-field" => "status - boolean (true success, false failed),
                                  message - string (explanation)"
            ),
            "delete" => array(
                "explanation" => "Delete a notification",
                "fields" => array(
                    "sessionToken - string (required)",
                    "id - int (required)"
                ),
                "return-field" => "status - boolean (true success, false failed),
                                  message - string (explanation)"
            ),
            "patch" => array(
                "explanation" => "Update notification details",
                "fields" => array(
                    "sessionToken - string (required)",
                    "id - int (required)",
                    "name - string (required)",
                    "less - int (required)",
                    "inventoryId - int (required)"
                ),
                "return-field" => "status - boolean (true success, false failed),
                                  message - string (explanation)"
            )
        )
    );
    

    
    $myReturnClass->result[]=$add;

?>