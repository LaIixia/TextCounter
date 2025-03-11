package com.myapp.textcounter.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLibrary extends AppCompatActivity {
    //edittextの文字列の長さを取得するメソッド
    public String Count(Object text) {return intFormat(text.toString().length ( ));}
    //edittextのテキストの行数を取得するメソッド
    public String line(EditText text) {return text.length()==0 ? intFormat(text.getLineCount()-1) : intFormat(text.getLineCount());}
    //edittextの改行抜きでテキストの文字数を取得するメソッド
    public String BreakCnt(String letters,String lines) {return format2((Integer.parseInt(letters) - Integer.parseInt(lines)));}
    //受けとったテキストの改行・半角空白・全角空白を省いた際の文字数の戻り値をString型に変換して返す
    public String EmptyCnt(String letters) {
        return intFormat(letters.replaceAll("\n","")
                .replaceAll(" ","").replaceAll("　","").replaceAll(" ","").length());
    }
    //edittextの文字列が空であるか判断するメソッド
    public boolean textJudge(Object l) {return l.toString().isEmpty();}//空の場合だけtrueを返す
     //  Editable、EditText、型をString型に変換するメソッド || Intをstring型に変換するメソッド
    public String format2(Object j){return  String.valueOf(j);}
    public String intFormat(int lines) {return String.valueOf (lines);}
    //デバイスの時刻を取得し、DateFormatクラスで西暦/月/日/時/日/秒で返すように成形して返す
    public static String getNowDate(){return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));}
    //バブルメッセージ
    public void msgToast(Context c, int res , int duration){Toast.makeText(c,res,duration).show();}

    /**DB関係**/
    //dbを追加
    public void dataInsert(SQLiteDatabase db, String tableName, String[] values, String[] datas){
        ContentValues value = new ContentValues();
        for(int i =0 ; i<datas.length;i++){value.put(values[i], datas[i]);}
        db.insert(tableName, null, value);}
    //delete(テーブル名・削除する時に参照するカラムの場所・参照したカラムを用いてデータを操作(削除))
    public void dataRowDelete(SQLiteDatabase db, String tableName,String[] args){db.delete(tableName,"_id = ?" ,args);}
    //dbを削除
    public void dataAllDelete(SQLiteDatabase db, String tableName){db.delete(tableName,null,null);}
    //データの有無で追加・更新を判断
    public void changeData(SQLiteDatabase db, Cursor cursor, String tableName,String key,String data) {
        //一番先頭のレコードデータの有無を判別
        if (cursor.moveToFirst()) {configUpdate(db, tableName,key,data);} //ある場合はupdate
        else {configInsert(db, tableName,key,data);}//データが空の場合insert
    }
    //DBを追加
    public void configInsert(SQLiteDatabase db, String tableName,String key ,String data) {
        ContentValues values = new ContentValues();
        values.put(key, data);
        db.insert(tableName, null, values);
    }
    //dbを更新
    public void configUpdate(SQLiteDatabase db, String tableName,String key ,String data) {
        ContentValues values = new ContentValues();
        values.put(key, data);
        db.update(tableName,values, null,null);
    }
    //引数のカーソルからテーブルの列にデータがるかつ、そこに含まれている場合はCompoundButtonをtrueにする
    public boolean isMode(SQLiteDatabase db, String tableName, String[] value, String matchFlags){
        Cursor modeCursor = db.query(tableName, value,null,null,null,null,null);
        if(modeCursor.moveToFirst() && modeCursor.getString(0).equals(matchFlags)){
            modeCursor.close();
            return  true;
        }else {
            modeCursor.close();
            return false;
        }

    }
    //CompoundButton系統でスイッチが操作され、trueならdbにデータを格納し、falseなら消すメソッド
    public void dataMatchListener(boolean isChecked, SQLiteDatabase db, String tableName, String[] values , String[] flag){
        if (isChecked) {
            dataInsert(db, tableName, values, flag);
        } else {
            dataAllDelete(db, tableName);
        }
    }

    /**view関係**/
    public float dpToPx(Context context, float dp) {return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());}
    public void setLayoutDp(Context context, View Item, float widthDp, float heightDp) {
        // LayoutParamsを取得して設定
        ViewGroup.LayoutParams layoutParams = Item.getLayoutParams();
        layoutParams.width = (int) dpToPx(context, widthDp);// dpからpxに変換
        layoutParams.height = (int) dpToPx(context, heightDp);
        // LayoutParamsを引数のViewに設定する
        Item.setLayoutParams(layoutParams);
    }
}