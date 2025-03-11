package com.myapp.textcounter.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.myapp.textcounter.R;
import java.util.regex.Pattern;

public class PatternModel {
    MyLibrary myLibrary = new MyLibrary();

    public PatternModel(Context c,SQLiteDatabase db,Object tryText){//チェックボックスがタッチされているパターンに応じてメッセージを振り分け
        //1項目以上押された時 英字全角+数字全角+数字半角@@
        if(isPattern(
                myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE"),"[a-z]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE")
        ){       myLibrary.msgToast(c, R.string.burble_matches_messages_1, Toast.LENGTH_SHORT);}
        //英字半角+全角英字
        if(isPattern(
                myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE"),"[0-9]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE")
        ){       myLibrary.msgToast(c,R.string.burble_matches_messages_2, Toast.LENGTH_SHORT);}
        //半角数字＋全角数字
        if(isPattern(
                myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE")  ,"[A-Z]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE")
        ){       myLibrary.msgToast(c,R.string.burble_matches_messages_3, Toast.LENGTH_SHORT);}
        //全角英字+半角英数字
        if(isPattern(
                myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE"),"[０-９]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE")
        ){       myLibrary.msgToast(c,R.string.burble_matches_messages_4, Toast.LENGTH_SHORT);}

        //2項目以上押された時
        // 全角数字+半角数字が無効　左上と右上
        if(isPattern(myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE"),"[a-z]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE"),"[A-Z]", myLibrary.format2(tryText))
                &&! myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE")
        ){       myLibrary.msgToast(c,R.string.burble_matches_messages_5, Toast.LENGTH_SHORT);}
        //全角半角英字　左下と右下
        if(isPattern(myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE"),"[0-9]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE"),"[０-９]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE")
        ){  myLibrary.msgToast(c,R.string.burble_matches_messages_6, Toast.LENGTH_SHORT);}
        //全角英数字が無効 左上と左下
        if(isPattern(myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE"),"[0-9]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE"),"[a-z]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE")
        ){       myLibrary.msgToast(c,R.string.burble_matches_messages_7, Toast.LENGTH_SHORT);}
        //全角数字+半角英字　左下と右上
        if(isPattern(myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE"),"[0-9]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE"),"[A-Z]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE")
        ){       myLibrary.msgToast(c,R.string.burble_matches_messages_8, Toast.LENGTH_SHORT);}
        //半角英数字　右上と右下
        if(isPattern(myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE"),"[０－９]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE"),"[A-Z]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE")
                &&!myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE")
        ){       myLibrary.msgToast(c,R.string.burble_matches_messages_9, Toast.LENGTH_SHORT);}

        //3項目以上押された時
        //数字半角+英字半角+英字全角
        //数字全角
        if(isPattern(myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE"),"[0-9]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE"),"[a-z]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE"),"[A-Z]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE")
        ){         myLibrary.msgToast(c,R.string.burble_matches_messages_10, Toast.LENGTH_SHORT);}
        //英字半角+英字全角+数字全角
        //数字半角
        if(isPattern(myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE"),"[a-z]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE"),"[A-Z]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE"),"[０－９]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE")
        ){         myLibrary.msgToast(c,R.string.burble_matches_messages_11, Toast.LENGTH_SHORT);}
        //英字全角+数字全角+数字半角
        //英字半角
        if(isPattern(myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE"),"[A-Z]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE"),"[０－９]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE"),"[0-9]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE")
        ) {      myLibrary.msgToast(c,R.string.burble_matches_messages_12, Toast.LENGTH_SHORT);}
        //数字全角+数字半角+英字半角
        //英字全角
        if(isPattern(myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE"),"[０－９]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE"),"[0-9]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE"),"[a-z]", myLibrary.format2(tryText))
                &&!myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE")
        ) {      myLibrary.msgToast(c,R.string.burble_matches_messages_13, Toast.LENGTH_SHORT);}
        //4項目(全チェック時)
        if(isPattern(myLibrary.isMode (db,"half_numberModeData", new String[]{"value"}, "TRUE"),"[0-9]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"half_alphabetModeData", new String[]{"value"}, "TRUE"),"[a-z]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"full_alphabetModeData", new String[]{"value"}, "TRUE"),"[A-Z]", myLibrary.format2(tryText))
                && isPattern(myLibrary.isMode (db,"full_numberModeData", new String[]{"value"}, "TRUE"),"[０-９]", myLibrary.format2(tryText)))
        {        myLibrary.msgToast(c,R.string.burble_matches_messages_14, Toast.LENGTH_SHORT);}


    }
    //特定の文字が含まれているかどうかを判断
    public boolean isPattern(boolean cb, String regex, String text){return  cb && Pattern.compile(regex).matcher(text).find();}
}
