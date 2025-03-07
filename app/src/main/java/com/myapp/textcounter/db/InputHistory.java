package com.myapp.textcounter.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InputHistory extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 1;
    // データーベース名
    private static final String DATABASE_NAME = "TextData.db";
    private static final String TABLE_NAME = "textData";
    private static final String _ID = "_id";
    private static final String ADD_HISTORY_DATE = "date", ADD_HISTORY_DESCRIPTION= "description",
            ADD_HISTORY_LINES = "line",ADD_HISTORY_BREAKS = "break",ADD_HISTORY_EMPTY = "empty";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
      _ID + " TEXT PRIMARY KEY," + ADD_HISTORY_DATE + " TEXT," + ADD_HISTORY_DESCRIPTION + " TEXT,"
            + ADD_HISTORY_LINES + " TEXT," + ADD_HISTORY_BREAKS + " TEXT," + ADD_HISTORY_EMPTY + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public InputHistory(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //
    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブル作成
        // SQLiteファイルがなければSQLiteファイルが作成される
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // アップデートの判別
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}