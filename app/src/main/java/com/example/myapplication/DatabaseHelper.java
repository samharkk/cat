package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // 数据库信息
    private static final String DATABASE_NAME = "cat_app.db";
    private static final int DATABASE_VERSION = 1;

    // 用户表
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_AVATAR = "avatar";
    private static final String COLUMN_CREATE_TIME = "create_time";

    // 聊天记录表
    private static final String TABLE_CHAT = "chat_messages";
    private static final String COLUMN_MESSAGE_ID = "message_id";
    private static final String COLUMN_SENDER = "sender";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_AVATAR + " TEXT DEFAULT 'cat1',"
                + COLUMN_CREATE_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(createUsersTable);

        // 创建聊天记录表
        String createChatTable = "CREATE TABLE " + TABLE_CHAT + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SENDER + " TEXT NOT NULL,"
                + COLUMN_MESSAGE + " TEXT NOT NULL,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(createChatTable);

        // 插入默认用户数据
        insertDefaultUsers(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        onCreate(db);
    }

    private void insertDefaultUsers(SQLiteDatabase db) {
        // 插入默认用户
        String[] defaultUsers = {
                "小明,123456,cat1",
                "小红,123456,cat2",
                "小猫,123456,cat3"
        };

        for (String userData : defaultUsers) {
            String[] parts = userData.split(",");
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, parts[0]);
            values.put(COLUMN_PASSWORD, parts[1]);
            values.put(COLUMN_AVATAR, parts[2]);
            db.insert(TABLE_USERS, null, values);
        }
    }

    // 用户相关操作
    public boolean addUser(String username, String password, String avatar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_AVATAR, avatar);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public User getUserInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_AVATAR, COLUMN_CREATE_TIME};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        User user = null;

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setUsername(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            user.setAvatar(cursor.getString(3));
            user.setCreateTime(cursor.getString(4));
        }
        cursor.close();
        return user;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_AVATAR, COLUMN_CREATE_TIME};

        Cursor cursor = db.query(TABLE_USERS, columns, null, null, null, null, COLUMN_CREATE_TIME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setUsername(cursor.getString(1));
                user.setPassword(cursor.getString(2));
                user.setAvatar(cursor.getString(3));
                user.setCreateTime(cursor.getString(4));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    public boolean updateUser(int id, String username, String password, String avatar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_AVATAR, avatar);

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        int result = db.update(TABLE_USERS, values, selection, selectionArgs);
        return result > 0;
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        int result = db.delete(TABLE_USERS, selection, selectionArgs);
        return result > 0;
    }

    // 聊天记录相关操作
    public boolean addChatMessage(String sender, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER, sender);
        values.put(COLUMN_MESSAGE, message);

        long result = db.insert(TABLE_CHAT, null, values);
        return result != -1;
    }

    public List<String> getChatMessages() {
        List<String> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_SENDER, COLUMN_MESSAGE, COLUMN_TIMESTAMP};

        Cursor cursor = db.query(TABLE_CHAT, columns, null, null, null, null, COLUMN_TIMESTAMP + " ASC");

        if (cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(0);
                String message = cursor.getString(1);
                messages.add(sender + ": " + message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }
}
