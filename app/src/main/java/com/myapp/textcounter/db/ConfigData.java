package com.myapp.textcounter.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConfigData extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 1;
    // データーベース名
    private static final String DATABASE_NAME = "ConfigData.db";

    public String addEntries(String tableName,String colum,String ids){
        return "CREATE TABLE " + tableName + " (" + ids + " CHAR PRIMARY KEY," + colum + " CHAR)";
    }
    public String remEntries(String tableName){ return "DROP TABLE IF EXISTS " + tableName;}

    public ConfigData(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(addEntries("themeModeData","flag","_id"));//テーマ用  1
        db.execSQL(addEntries("watcherModeData","situation","_id"));//Watcher用 1
        db.execSQL(addEntries("maxLengthData","size","_id"));//1

        db.execSQL(addEntries("half_alphabetModeData","value","_id"));//2
        db.execSQL(addEntries("half_numberModeData","value","_id"));//2

        db.execSQL(addEntries("full_alphabetModeData","value","_id"));//2
        db.execSQL(addEntries("full_numberModeData","value","_id"));//2
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    /*  db.execSQL(remEntries("themeModeData"));
        db.execSQL(remEntries("watcherModeData"));
        db.execSQL(remEntries("half_alphabetModeData"));
        db.execSQL(remEntries("half_numberModeData"));
        db.execSQL(remEntries("full_alphabetModeData"));
        db.execSQL(remEntries("full_numberModeData"));*/
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}