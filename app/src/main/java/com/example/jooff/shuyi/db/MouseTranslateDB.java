package com.example.jooff.shuyi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jooff.shuyi.model.Collect;
import com.example.jooff.shuyi.model.RecHistoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jooff on 2016/8/14.
 */

public class MouseTranslateDB {

    private static final String DB_NAME = "mouse_translate";
    private static final int VERSION = 1;
    private static MouseTranslateDB mouseTranslateDB = null;
    private SQLiteDatabase db;

    /**
     * 构造方法私有化，创建单例类
     */
    private MouseTranslateDB(Context context) {
        MouseTranslateOpenHelper mthelper = new MouseTranslateOpenHelper(context, DB_NAME, null, VERSION);
        db = mthelper.getWritableDatabase();
    }

    /**
     * 获取单例的实例,使用双重校验锁，保证线程安全与速率
     */
    public static MouseTranslateDB getInstance(Context context){
        if (mouseTranslateDB == null){
            synchronized (MouseTranslateDB.class){
                if (mouseTranslateDB == null){
                    mouseTranslateDB = new MouseTranslateDB(context);
                }
            }
        }
        return mouseTranslateDB;
    }

    public void saveHistory(RecHistoryItem recHistoryItem){
        if (recHistoryItem != null){
            ContentValues values = new ContentValues();
            values.put("original",recHistoryItem.getTextOriginal());
            values.put("result",recHistoryItem.getTextResult());
            db.insert("History",null,values);
        }
    }

    public List<RecHistoryItem> loadHistory(){
        List<RecHistoryItem> list = new ArrayList<>();
        Cursor cursor = db.query("History",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                RecHistoryItem recHistoryItem = new RecHistoryItem();
                recHistoryItem.setTextOriginal(cursor.getString(cursor.getColumnIndex("original")));
                recHistoryItem.setTextResult(cursor.getString(cursor.getColumnIndex("result")));
                list.add(recHistoryItem);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public void deleteHistory(String s){
        db.delete("History","original == ?",new String[] {s});
    }

    public void saveCollect(Collect collect){
        if (collect != null){
            ContentValues values = new ContentValues();
            values.put("original",collect.getOriginal());
            values.put("result",collect.getResult());
            db.insert("Collect",null,values);
        }
    }

    public List<Collect> loadCollect(){
        List<Collect> list = new ArrayList<>();
        Cursor cursor = db.query("Collect",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                Collect collect = new Collect();
                collect.setOriginal(cursor.getString(cursor.getColumnIndex("original")));
                collect.setResult(cursor.getString(cursor.getColumnIndex("result")));
                list.add(collect);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}
