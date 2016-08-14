package com.example.jooff.shuyi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jooff on 2016/8/14.
 */

class MouseTranslateOpenHelper extends SQLiteOpenHelper {
    /**
     * History建表语句
     */
    private static final String CREATE_HISTORY = "create table History("
            + "id integer primary key autoincrement, "
            + "original text,"
            + "result text)";

    /**
     * collect建标语句
     */
    private static final String CREATE_COLLECT = "create table Collect("
            + "id integer primary key autoincrement, "
            + "original text,"
            + "result text)";

    MouseTranslateOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_HISTORY);
        sqLiteDatabase.execSQL(CREATE_COLLECT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
