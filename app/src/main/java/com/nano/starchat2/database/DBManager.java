package com.nano.starchat2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context){
        helper = new DatabaseHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     */
    public void add(List<BubblePlayer> bubbleplayers) {
        db.beginTransaction();  //开始事务
        try {
            for (BubblePlayer player : bubbleplayers) {
                //db.execSQL("INSERT INTO starchat_bubbleplayer(id, name, latitude, lontitude ) VALUES(5, 'name', 31, 130)");
                BubblePlayer personInDB = new BubblePlayer();
                personInDB = query(player.getId());
                if (null == personInDB) {
                    db.execSQL("INSERT INTO starchat_bubbleplayer(id, name, latitude, lontitude, headImg, sex) VALUES(?, ?, ?, ?, ?, ?)",
                            new Object[]{player.getId(), player.getName(), player.getLatitude(), player.getLontitude(), player.getHeadImg(), player.getSex()});
                }
                else
                {
                    updatePlayer(player);
                }

            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    //添加player，如果有就更新，没有就添加。
    public void add(BubblePlayer player) {
        db.beginTransaction();  //开始事务
        try {
                BubblePlayer personInDB = new BubblePlayer();
                personInDB = query(player.getId());

                //如果没有找到就直接添加，如果找到了就更新
                if (null == personInDB) {
                    db.execSQL("INSERT INTO starchat_bubbleplayer(id, name, latitude, lontitude, headImg, sex) VALUES(?, ?, ?, ?, ?, ?)",
                            new Object[]{player.getId(), player.getName(), player.getLatitude(), player.getLontitude(),player.getHeadImg(), player.getSex()});
                }
                else
                {
                    updatePlayer(player);
                }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update person's age
     * @param person
     */
    public void  updatePlayer(BubblePlayer person) {
        ContentValues cv = new ContentValues();
        cv.put("id", person.getId());
        cv.put("name", person.getName());
        cv.put("latitude", person.getLatitude());
        cv.put("lontitude", person.getLontitude());
        cv.put("headImg", person.getHeadImg());
        cv.put("sex", person.getSex());
        db.update("starchat_bubbleplayer", cv, "id = ?", new String[]{String.valueOf(person.getId())} );
    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public ArrayList<BubblePlayer> query() {
        ArrayList<BubblePlayer> persons = new ArrayList<BubblePlayer>();
        //db.execSQL("INSERT INTO starchat_bubbleplayer(id, name, latitude, lontitude) VALUES(8, 'name8', 38, 138)");
        Cursor c = db.rawQuery("SELECT * FROM starchat_bubbleplayer", null); //db.query("starchat_bubbleplayer",null,null,null,null,null,null);
        BubblePlayer person = new BubblePlayer();
        while (c.moveToNext()) {
            person.setId( c.getInt(c.getColumnIndex("id")) );
            person.setName( c.getString(c.getColumnIndex("name")) );
            person.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
            person.setLontitude(c.getDouble(c.getColumnIndex("lontitude")));
            person.setHeadImg(c.getString(c.getColumnIndex("headImg")));
            person.setSex(c.getString(c.getColumnIndex("sex")));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public BubblePlayer query(Integer id) {
        Cursor c = db.rawQuery("SELECT * FROM starchat_bubbleplayer where id=?", new String[]{Integer.toString(id)}); //db.query("starchat_bubbleplayer",null,null,null,null,null,null);
        BubblePlayer person = new BubblePlayer();

        if (c.getCount() == 1)
        {
            c.moveToNext();

            person.setId( c.getInt(c.getColumnIndex("id")) );
            person.setName( c.getString(c.getColumnIndex("name")) );
            person.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
            person.setLontitude(c.getDouble(c.getColumnIndex("lontitude")));
            person.setHeadImg(c.getString(c.getColumnIndex("headImg")));
            person.setSex(c.getString(c.getColumnIndex("sex")));
            c.close();
            return person;
        }
        else if(c.getCount() > 1){
            System.out.print("DBManager: find the ID has more than one BubblePlayer!");
            c.close();
            return null;
        }
        else {
            c.close();
            return null;
        }
    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public ArrayList<BubblePlayer> queryRectBubbles(double LeftUpLat, double LeftUpLon, double RightDownLat, double RightDownLon) {
        ArrayList<BubblePlayer> persons = new ArrayList<BubblePlayer>();
        //db.execSQL("INSERT INTO starchat_bubbleplayer(id, name, latitude, lontitude) VALUES(8, 'name8', 38, 138)");
        Cursor c =  db.rawQuery("SELECT * FROM starchat_bubbleplayer where latitude>? and latitude<? and lontitude>? and lontitude<?",
                new String[]{String.valueOf(RightDownLat),String.valueOf(LeftUpLat),String.valueOf(LeftUpLon),String.valueOf(RightDownLon)});
        while (c.moveToNext()) {
            BubblePlayer person = new BubblePlayer();
            person.setId( c.getInt(c.getColumnIndex("id")) );
            person.setName( c.getString(c.getColumnIndex("name")) );
            person.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
            person.setLontitude(c.getDouble(c.getColumnIndex("lontitude")));
            person.setHeadImg(c.getString(c.getColumnIndex("headImg")));
            person.setSex(c.getString(c.getColumnIndex("sex")));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.query("starchat_bubbleplayer",null,null,null,null,null,null);//db.rawQuery("SELECT * FROM starchat_bubbleplayer", null);
        return c;
    }

    public DatabaseHelper getHelper() {
        return helper;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    /**
     * close database

     */
    public void closeDB() {
        db.close();
    }
}