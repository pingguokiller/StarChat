package com.nano.starchat2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "starchat.db";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context) {
        this(context, DATABASE_NAME);
    }

    public DatabaseHelper(Context context, String name) {
        this(context, name, VERSION);
    }

    public DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DatabaseHelper: Create database!\n");

        db.execSQL("CREATE TABLE IF NOT EXISTS starchat_bubbleplayer" +
                "(id INTEGER PRIMARY KEY, " +
                "name VARCHAR, " +
                "latitude double, " +    //纬度
                "lontitude double, " +    //经度
                "addr String, " +
                "headImg String, " +
                "sex String, " +
                "ismale bool, " +
                "thetime Time, " +
                "age INTEGER, " +
                "info TEXT" +
                ")");
        System.out.println("create table!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE starchat_bubbleplayer ADD COLUMN other STRING");
    }
}