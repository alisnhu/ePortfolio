package com.example.ali_arslan_project2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;

import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    // Veritabanı adı ve versiyonu
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    // Kullanıcı tablosu ve sütun adları
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";

    //stting tablosu ve sutun adlari
    private static final String SETTINGS = "settings";
    private static final String SETTING_ID = "id";
    private static final String SETTING_USER_ID = "userid";
    private static final String SETTING_WORKING_MODE = "workingmode";
    private static final String SETTING_PROXY = "proxy";


    // Inventory tablosu ve sütun adları
    private static final String TABLE_INVENTORY = "inventory";
    private static final String COLUMN_INVENTORY_ID = "id";
    private static final String COLUMN_INVENTORY_NAME = "inventory_name";
    private static final String COLUMN_INVENTORY_USER_ID = "user_id";

    // Item tablosu ve sütun adları
    private static final String TABLE_ITEM = "item";
    private static final String COLUMN_ITEM_ID = "id";
    private static final String COLUMN_ITEM_NAME = "name";
    private static final String COLUMN_ITEM_LOCATION = "location";
    private static final String COLUMN_ITEM_STOCK = "stock";
    private static final String COLUMN_ITEM_NOTIFICATION = "notification";
    private static final String COLUMN_ITEM_LESSTHAN = "lessthan";
    private static final String COLUMN_ITEM_USER_ID = "user_id";
    private static final String COLUMN_ITEM_INVENTORY_ID = "inventory_id";


    // Notification tablosu ve sütun adları
    private static final String TABLE_NOTIFICATION = "notification";
    private static final String COLUMN_NOTIFICATION_ID = "id";
    private static final String COLUMN_NOTIFICATION_NAME = "name";
    private static final String COLUMN_NOTIFICATION_LESS = "less";
    private static final String COLUMN_NOTIFICATION_INVENTORY_ID = "inventory_id";
    private static final String COLUMN_NOTIFICATION_USER_ID = "user_id";


    // Kullanıcı tablosu oluşturma SQL komutu
    private static final String TABLE_CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PHONE + " TEXT);";

    // settings tablosu oluşturma SQL komutu
    private static final String TABLE_CREATE_SETTINGS =
            "CREATE TABLE " + SETTINGS + " (" +
                    SETTING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SETTING_USER_ID + " INTEGER  ," +
                    SETTING_WORKING_MODE + " TEXT NOT NULL, " +
                    SETTING_PROXY + " TEXT);";

    // Inventory tablosu oluşturma SQL komutu
    private static final String TABLE_CREATE_INVENTORY =
            "CREATE TABLE " + TABLE_INVENTORY + " (" +
                    COLUMN_INVENTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "+
                    COLUMN_INVENTORY_USER_ID + " INTEGER );";


    // Item tablosu oluşturma SQL komutu
    private static final String TABLE_CREATE_ITEM =
            "CREATE TABLE " + TABLE_ITEM + " (" +
                    COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                    COLUMN_ITEM_LOCATION + " TEXT, " +
                    COLUMN_ITEM_STOCK + " INTEGER, " +
                    COLUMN_ITEM_NOTIFICATION + " INTEGER, " + // SQLite'de boolean değeri INTEGER olarak saklanır (0 veya 1)
                    COLUMN_ITEM_LESSTHAN + " INTEGER, " +
                    COLUMN_ITEM_USER_ID + " INTEGER, " +
                    COLUMN_ITEM_INVENTORY_ID + " INTEGER);";


    private static final String TABLE_CREATE_NOTIFICATION =
            "CREATE TABLE " + TABLE_NOTIFICATION + " (" +
                    COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOTIFICATION_NAME + " TEXT, " +
                    COLUMN_NOTIFICATION_LESS + " INTEGER, " +
                    COLUMN_NOTIFICATION_INVENTORY_ID + " INTEGER, " +
                    COLUMN_NOTIFICATION_USER_ID + " INTEGER);";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_USERS);  // Kullanıcı tablosunu oluşturur
        db.execSQL(TABLE_CREATE_INVENTORY);  // Inventory tablosunu oluşturur
        db.execSQL(TABLE_CREATE_ITEM);// Item tablosunu oluşturur
        db.execSQL(TABLE_CREATE_NOTIFICATION);// Notification tablosunu oluşturur
        db.execSQL(TABLE_CREATE_SETTINGS);// Notification tablosunu oluşturur
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);  // Eski kullanıcı tablosunu siler
        db.execSQL("DROP TABLE IF EXISTS " + SETTINGS);  // Eski inventory tablosunu siler
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);  // Eski inventory tablosunu siler
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);  // Eski inventory tablosunu siler
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);  // Eski inventory tablosunu siler
        onCreate(db);  // Yeni tabloları oluşturur
    }

    // Kullanıcı ekleme fonksiyonu
    public void addUser(String username, String password, String email, String phone ,String  workingmode,String proxy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);

        long userid = db.insert(TABLE_USERS, null, values);  // Veriyi kullanıcı tablosuna ekler
        values.clear();

        values.put(SETTING_USER_ID , userid);
        values.put(SETTING_WORKING_MODE , workingmode);
        values.put(SETTING_PROXY , proxy);
        db.insert(SETTINGS, null, values);
        db.close();  // Veritabanını kapatır
    }
    // Kullanıcı kontrol fonksiyonu

    public int validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ? AND password = ?", new String[]{username, password});

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    public  String getWorkingMode(String userId)
    {
        String rv = "offline";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+ SETTING_WORKING_MODE +" FROM "+SETTINGS +" WHERE "+SETTING_USER_ID+"=? ", new String[] {userId});
        if (cursor.moveToFirst()) {
            rv = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return rv;
    }

    // Inventory ekleme fonksiyonu
    public void addInventory(String inventoryName,int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INVENTORY_NAME, inventoryName);
        values.put(COLUMN_INVENTORY_USER_ID , userId);
        db.insert(TABLE_INVENTORY, null, values);  // Veriyi inventory tablosuna ekler
        db.close();  // Veritabanını kapatır
    }

    public List<List> getAllInventories(String userId) {

        List<List> inventories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM inventory WHERE user_id = ?", new String[]{userId});

        if (cursor.moveToFirst()) {
            do {
                /*
                int idindex = cursor.getColumnIndex(COLUMN_INVENTORY_ID);
                int nameindex = cursor.getColumnIndex(COLUMN_INVENTORY_NAME);
                int id = cursor.getInt(idindex);
                String name = cursor.getString(nameindex);
                */

                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("inventory_name"));
                List<String> pairlist = new ArrayList<>();
                pairlist.add(String.valueOf(id));
                pairlist.add(name);
                inventories.add(pairlist);



            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return inventories;
    }

    public List<Item> getAllItems(String inventoryId) {

        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM "+TABLE_ITEM+" WHERE "+COLUMN_ITEM_INVENTORY_ID+" = ?", new String[]{inventoryId});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_LOCATION));
                    int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_STOCK));
                    boolean notification = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NOTIFICATION)) > 0;
                    int lessThan = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_LESSTHAN));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_USER_ID));
                    int stockId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_INVENTORY_ID));

                    Item item = new Item(id, name, location, stock, notification, lessThan, userId, stockId);
                    itemList.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return itemList;


    }

    public void addItem(String name, String location, int stock, boolean notification, int lessThan, int userId, int inventoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, name);
        values.put(COLUMN_ITEM_LOCATION, location);
        values.put(COLUMN_ITEM_STOCK, stock);
        values.put(COLUMN_ITEM_NOTIFICATION, notification ? 1 : 0);
        values.put(COLUMN_ITEM_LESSTHAN, lessThan);
        values.put(COLUMN_ITEM_USER_ID, userId);
        values.put(COLUMN_ITEM_INVENTORY_ID, inventoryId);

        Log.d("Debug","helper calisti");
        db.insert(TABLE_ITEM, null, values);  // Veriyi item tablosuna ekler
        db.close();  // Veritabanını kapatır
    }

    public void deleteItemById(boolean islist ,int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // item tablosundan belirli bir id ile satır silme
        int rowsDeleted;
        if(islist)
        {

             rowsDeleted = db.delete(TABLE_ITEM, COLUMN_ITEM_INVENTORY_ID + " = ?", new String[]{String.valueOf(itemId)});
        }
        else
        {
             rowsDeleted = db.delete(TABLE_ITEM, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
        }

        if (rowsDeleted > 0) {
            Log.d("DBHelper", "Item with id " + itemId + " deleted successfully.");
        } else {
            Log.d("DBHelper", "Item with id " + itemId + " not found.");
        }

        db.close();  // Veritabanını kapat
    }

    public void updateItem(String name, String location, int stock, boolean notification, int lessThan, int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ITEM_NAME, name);
        values.put(COLUMN_ITEM_LOCATION, location);
        values.put(COLUMN_ITEM_STOCK, stock);
        values.put(COLUMN_ITEM_NOTIFICATION, notification ? 1 : 0);
        values.put(COLUMN_ITEM_LESSTHAN, lessThan);

        int rowsUpdated = db.update(
                TABLE_ITEM,
                values,
                COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(itemId)}
        );



        db.close();  // Veritabanını kapat
    }

    public Item getItemById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Item item = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEM + " WHERE " + COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_LOCATION));
                int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_STOCK));
                boolean notification = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NOTIFICATION)) > 0;
                int lessThan = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_LESSTHAN));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_USER_ID));
                int inventoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_INVENTORY_ID));

                item = new Item(id, name, location, stock, notification, lessThan, userId, inventoryId);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return item;
    }
    public List<Item2> getAllNotification(String argsinventoryId) {
        List<Item2> item2List = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION + " WHERE "+COLUMN_NOTIFICATION_INVENTORY_ID + " = ?",  new String[]{argsinventoryId});

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_NAME));
                String less = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_LESS));
                String inventoryId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_INVENTORY_ID));
                String userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_USER_ID));

                Item2 item = new Item2(id, name, less, userId, inventoryId);
                item2List.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return item2List;
    }
    public Item2 getNotificationById(int id ,  int inventoryId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Item2 item = null;
        cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION + " WHERE " + COLUMN_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(id)});
        if(cursor != null && cursor.moveToFirst())
        {
            String rid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_NAME));
            String less = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_LESS));
            String rinventoryId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_INVENTORY_ID));
            String userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_USER_ID));
            item = new Item2(rid, name, less, userId, rinventoryId);
        }
        return item;
    }

    public void addNotification(String name, String less, int userId, int inventoryId) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_NAME, name);
        values.put(COLUMN_NOTIFICATION_LESS, less);
        values.put(COLUMN_NOTIFICATION_INVENTORY_ID, inventoryId);
        values.put(COLUMN_NOTIFICATION_USER_ID, userId);

        db.insert(TABLE_NOTIFICATION, null, values);  // Veriyi item tablosuna ekler
        db.close();  // Veritabanını kapatır
    }


    public void deletenotification(boolean islist ,int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted;
        if(islist)
        {

            rowsDeleted = db.delete(TABLE_NOTIFICATION, COLUMN_NOTIFICATION_INVENTORY_ID + " = ?", new String[]{String.valueOf(itemId)});
        }
        else
        {
            rowsDeleted = db.delete(TABLE_NOTIFICATION, COLUMN_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(itemId)});
        }

        if (rowsDeleted > 0) {
            Log.d("DBHelper", "Item with id " + itemId + " deleted successfully.");
        } else {
            Log.d("DBHelper", "Item with id " + itemId + " not found.");
        }

        db.close();  // Veritabanını kapat
    }

    public void updateNotification(int notificationId,String name, String less)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_NAME, name);
        values.put(COLUMN_NOTIFICATION_LESS, less);
        int rowsUpdated = db.update(
                TABLE_NOTIFICATION,
                values,
                COLUMN_NOTIFICATION_ID + " = ?",
                new String[]{String.valueOf(notificationId)}
        );
    }
    public String getPHoneNumber(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String phoneNumber = null;

        try {
            cursor = db.rawQuery("SELECT " + COLUMN_PHONE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return phoneNumber;
    }

    public boolean checkNotificationLessThan(int stock) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = false;

        try {
            // `item` tablosunda `lessthan` değerinin `stock` değerinden küçük olduğu herhangi bir satır olup olmadığını kontrol eder
            cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEM + " WHERE " + COLUMN_ITEM_LESSTHAN + " < ?", new String[]{String.valueOf(stock)});
            if (cursor != null && cursor.getCount() > 0) {
                result = true;  // Eğer varsa `true` döner
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return result;  // `true` veya `false` döner
    }



}
