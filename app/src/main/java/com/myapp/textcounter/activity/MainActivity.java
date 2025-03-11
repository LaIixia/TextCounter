package com.myapp.textcounter.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.View;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import static androidx.appcompat.app.AppCompatDelegate.*;

import java.util.Objects;
import java.util.UUID;

import com.myapp.textcounter.db.*;
import com.myapp.textcounter.model.*;
import com.myapp.textcounter.R;

public class MainActivity extends AppCompatActivity {//継承

    //フィールド onCreate内で呼び出すために、ここで各クラスを宣言
    static MyLibrary myLibrary;
    public static EditText textBOX;
    TextView textLetter ,textLines;
    ClipboardManager clipboard;


    InputHistory history;
    static SQLiteDatabase historyDB;

    ConfigData mode;
    static SQLiteDatabase modeDB;
    static Cursor cursorTheme , cursorTheme1,maxCursor;
    static  InputFilter[] maxText = new InputFilter[1];//要素１のインスタンスを生成
    TextEvent TW = new TextEvent ();
    SwitchCompat Watcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        //dbを作成
        bindDB ( );

        //Activity起動時リソースのidに呼び出して、フィールドに代入
        myLibrary = new MyLibrary( );
        textBOX = findViewById (R.id.edit_text);
        textLetter = findViewById (R.id.text_letters2);
        textLines = findViewById (R.id.text_lines2);
        clipboard = (ClipboardManager) getSystemService (Context.CLIPBOARD_SERVICE);
        View (myLibrary.Count (textBOX.getText ( )), myLibrary.intFormat (textBOX.getLineCount ( )));

        //テキストカウント
        Button button_cnt = findViewById (R.id.button_cnt);
        button_cnt.setOnClickListener (new ButtonCount ( ));
        //詳細ボタン
        Button button_detail = findViewById (R.id.button_detail);
        button_detail.setOnClickListener (new ButtonDetail ( ));
        //テキスト削除
        Button button_del = findViewById (R.id.button_clr);
        button_del.setOnClickListener (new ButtonDelete ( ));
        //テキストコピー
        Button button_cpy = findViewById (R.id.button_cpy);
        button_cpy.setOnClickListener (new ButtonCopy ( ));
        //テキストペースト
        Button button_pst = findViewById (R.id.button_pst);
        button_pst.setOnClickListener (new ButtonPaste ( ));

        //TextWatcherスイッチ
        Watcher = findViewById (R.id.watcherSwitch);
        Watcher.setOnCheckedChangeListener (new swWatcher ( ));

        //dbの状態に応じて設定をアプリ起動時に反映
        bindSettingData( );


        /*ディスプレイの密度を取得し、サイズを変える*/
        //System.out.println(getResources().getDisplayMetrics().scaledDensity);
        if(getResources().getDisplayMetrics().scaledDensity> 450){
            myLibrary.setLayoutDp(this, textBOX, 320f, 300f);//テキストボックス
            myLibrary.setLayoutDp(this, Watcher, 95f, 50f);//スイッチ
        }
    }

    class ButtonDetail implements View.OnClickListener {
        @Override public void onClick(View v) {new LengthDialogFragment().show(getSupportFragmentManager(), "my_dialog1");}
    }
    class ButtonCount implements View.OnClickListener {
        public void onClick(View view) {//テキストが空の場合
            if (myLibrary.textJudge(textBOX.getText())) {myLibrary.msgToast(getApplicationContext(),R.string.burble_message_noText, Toast.LENGTH_SHORT);}
            else {
                addText();
                View(myLibrary.Count(textBOX.getText()), myLibrary.line(textBOX));
                new PatternModel(getApplicationContext(),modeDB,textBOX.getText());
            }
        }
    }

    /*TextEvent型のTWにTextEventクラスのインスタンスを生成させることで、
    TWでaddイベントを呼ぶと、removeする際に同じ場所(TW)を参照することでwatcherが切れる*/
    class swWatcher implements CompoundButton.OnCheckedChangeListener{
        public void onCheckedChanged(CompoundButton buttonView1, boolean isChecked) {
            if (isChecked) {
                textBOX.addTextChangedListener (TW);
                myLibrary.dataInsert(modeDB,"watcherModeData",
                        new String[]{"situation"},new String[]{"TRUE"});
            }else{
                textBOX.removeTextChangedListener (TW);
                myLibrary.dataAllDelete(modeDB,"watcherModeData");
            }
        }
    }

    class TextEvent implements TextWatcher {
        public void beforeTextChanged(CharSequence c, int A, int B, int C) {}
        public void onTextChanged(CharSequence c, int A, int B, int C) {}
        public void afterTextChanged(Editable editable) {
            if(!myLibrary.textJudge(textBOX.getText())){
                addText();
                View (myLibrary.Count (textBOX.getText ( )), myLibrary.intFormat(textBOX.getLineCount ( )));
                new PatternModel(getApplicationContext(),modeDB,textBOX.getText());
            }
        }
    }

    class ButtonDelete implements View.OnClickListener { public void onClick(View view){
        textBOX.getText().clear();View(myLibrary.Count(textBOX.getText()), myLibrary.line(textBOX));}}

    class ButtonCopy implements View.OnClickListener {
        public void onClick(View view) {
            if(myLibrary.textJudge(textBOX.getText ())){//空であるか判断;
                myLibrary.msgToast(getApplicationContext(),R.string.burble_message_noText,Toast.LENGTH_SHORT);}
            else{
                //クリップボードにセット
                clipboard.setPrimaryClip(ClipData.newPlainText(null, textBOX.getText ()));
                if(Build.VERSION.SDK_INT > 33) {//os 13以下の時だけメッセージを出す
                    myLibrary.msgToast(getApplicationContext(),R.string.burble_message_copied,Toast.LENGTH_SHORT);
                }
            }
        }
    }
    class ButtonPaste implements View.OnClickListener {
        public void onClick(View view) {
            //グリップボートのデータの位置  0番目
            try {
                ClipData.Item item = Objects.requireNonNull(clipboard.getPrimaryClip()).getItemAt(0);
                String pasteData = myLibrary.format2(item.getText());
                //item内が空でないの時だけテキストの処理を行う
                if (!myLibrary.textJudge(item)) {
                    textBOX.setText(pasteData);
                    View(myLibrary.Count(pasteData), myLibrary.line(textBOX));
                }
            } catch (NullPointerException ignored) {}
        }
    }

    public static class LengthDialogFragment extends DialogFragment {
        @NonNull @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View myDia = inflater.inflate(R.layout.dialog_detail, null);
            CheckBox half_engCheckbox = myDia.findViewById(R.id.half_alphabet_Checkbox),
                    half_numCheckbox = myDia.findViewById(R.id.half_number_CheckBox),
                    full_engCheckbox = myDia.findViewById(R.id.full_alphabet_Checkbox),
                    full_numCheckbox = myDia.findViewById(R.id.full_number_Checkbox);
            EditText setMaxNum = myDia.findViewById(R.id.editSetLength);
            TextView letter = myDia.findViewById(R.id.text_letters2),
                    line   = myDia.findViewById(R.id.text_lines2),
                    breaks = myDia.findViewById(R.id.text_break2),
                    empty  = myDia.findViewById(R.id.text_empty2);

            maxCursor = modeDB.query("maxLengthData",new String[]{"size"},null,null,null,null,null);
            if(maxCursor.moveToFirst()){//dbを参照して、テーブルに設定値があるかどうか
                if(myLibrary.textJudge(maxCursor.getString(0))){
                    setMaxNum.setText(null);
                }else {
                    setMaxNum.setText(maxCursor.getString(0));
                }
            }
            maxCursor.close();

            //Dialogを表示した際にdbのテーブルを参照して判断する
            half_engCheckbox.setChecked(myLibrary.isMode (modeDB, "half_alphabetModeData", new String[]{"value"},"TRUE"));
            half_numCheckbox.setChecked(myLibrary.isMode (modeDB, "half_numberModeData", new String[]{"value"}, "TRUE"));
            full_engCheckbox.setChecked(myLibrary.isMode (modeDB, "full_alphabetModeData", new String[]{"value"},"TRUE"));
            full_numCheckbox.setChecked(myLibrary.isMode (modeDB, "full_numberModeData", new String[]{"value"}, "TRUE"));
            //操作された時にdbを読み書きする
            half_engCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    myLibrary.dataMatchListener(isChecked,modeDB,"half_alphabetModeData", new String[]{"value"}, new String[]{"TRUE"}));
            half_numCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    myLibrary.dataMatchListener(isChecked,modeDB,"half_numberModeData", new String[]{"value"}, new String[]{"TRUE"}));
            full_engCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    myLibrary.dataMatchListener(isChecked,modeDB,"full_alphabetModeData", new String[]{"value"}, new String[]{"TRUE"}));
            full_numCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    myLibrary.dataMatchListener(isChecked,modeDB,"full_numberModeData", new String[]{"value"}, new String[]{"TRUE"}));

            letter.setText(myLibrary.Count(textBOX.getText()));
            line.setText(myLibrary.line (textBOX));
            breaks.setText(myLibrary.BreakCnt(myLibrary.Count(textBOX.getText ()), myLibrary.line (textBOX)));
            empty.setText(myLibrary.EmptyCnt(myLibrary.format2(textBOX.getText())));
            return new AlertDialog.Builder(requireActivity())
                    .setView(myDia)
                    .setPositiveButton(R.string.button_bck, (dialog, which) -> {
                        if (myLibrary.format2(setMaxNum.getText()).isEmpty()) {
                            textBOX.setFilters(new InputFilter[0]);//要素0番目のデータを空のインスタンスを生成
                            myLibrary.dataAllDelete(modeDB,"maxLengthData");
                        } else {
                            try {
                                myLibrary.changeData(modeDB, maxCursor,"maxLengthData","size", myLibrary.format2(setMaxNum.getText()));
                                maxText[0] = new InputFilter.LengthFilter(Integer.parseInt(myLibrary.format2(setMaxNum.getText())));
                                textBOX.setFilters(maxText);
                            } catch (Exception ex) {
                                //int型以外のデータがセットされようとした場合も空のインスタンスを生成(上限を設定しない)
                                textBOX.setFilters(new InputFilter[0]);
                            }
                        }
                    }).create();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {getMenuInflater().inflate(R.menu.main_option_menu, menu);return true;}
    public boolean onOptionsItemSelected(MenuItem item) {// オプションメニューのアイテムが選択されたときに呼び出されるメソッド
        switch (item.getItemId()) {
            case R.id.item1:
                startActivity(new Intent(MainActivity.this, ListActivity.class));
                return true;
            case R.id.item2:
                new ThemeDialogFragment().show(getSupportFragmentManager(), "my_dialog");
                return true;
            case R.id.item3:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static class ThemeDialogFragment extends DialogFragment {
        @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            cursorTheme1 = modeDB.query("themeModeData", new String[] {"flag"},null,null,null,null,null);
            int itemChoice = 2;
            if(cursorTheme1.moveToFirst()){
                if(cursorTheme1.getString(0).equals("MODE_NIGHT_NO")){
                    itemChoice = 0;
                } else if (cursorTheme1.getString(0).equals("MODE_NIGHT_YES")) {
                    itemChoice = 1;
                }
            }
            cursorTheme1.close();
            return  new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.dialog_theme_title)
                    .setSingleChoiceItems(new String[]{"ホワイト","ダーク","自動"}, itemChoice, (dialog, which) -> {
                        switch (which){
                            case 0:
                                myLibrary.changeData(modeDB,cursorTheme1,"themeModeData","flag","MODE_NIGHT_NO");
                                setDefaultNightMode(MODE_NIGHT_NO);
                                break;
                            case 1:
                                myLibrary.changeData(modeDB,cursorTheme1,"themeModeData","flag","MODE_NIGHT_YES");
                                setDefaultNightMode(MODE_NIGHT_YES);
                                break;
                            case 2:
                                myLibrary.changeData(modeDB,cursorTheme1,"themeModeData","flag","MODE_NIGHT_FOLLOW_SYSTEM");
                                setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
                                break;
                        }
                    }).create();
        }
    }

    public void addText(){
        myLibrary.dataInsert(historyDB,"textData",new String[] {"_id","date","description","line","break","empty"},new String[]{myLibrary.format2(UUID.randomUUID()), MyLibrary.getNowDate(),
                myLibrary.format2 (textBOX.getText ( )), myLibrary.line(textBOX), myLibrary.BreakCnt(myLibrary.Count(textBOX.getText ()), myLibrary.line (textBOX)), myLibrary.EmptyCnt(myLibrary.format2(textBOX.getText()))});}
    public void View(String ltr, String lns){textLetter.setText (ltr);textLines.setText(lns);}

    void bindDB(){
        //dbのファイルが無いない場合は作成する
        if(mode == null){ mode = new ConfigData(getApplicationContext());}
        if(history == null){ history = new InputHistory(getApplicationContext());}
        //dbのインスタンスが無い場合は作成する | 履歴を出力するだけなので、読み込みモードで呼び出す
        if(historyDB == null){ historyDB = history.getWritableDatabase();}
        //設定をを読み書きするので、読み書きモードで呼び出す
        if(modeDB == null){ modeDB = mode.getWritableDatabase();}
    }
    void bindSettingData(){
        cursorTheme = modeDB.query("themeModeData", new String[] {"flag"},null,null,null,null,null);
        if (cursorTheme.moveToFirst()){
            if(cursorTheme.getString(0).equals("MODE_NIGHT_NO")){
                setDefaultNightMode(MODE_NIGHT_NO);
            }else if(cursorTheme.getString(0).equals("MODE_NIGHT_YES")){
                setDefaultNightMode(MODE_NIGHT_YES);
            }
            else{setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);}
        }
        cursorTheme.close();

        Watcher.setChecked (myLibrary.isMode (modeDB, "watcherModeData", new String[]{"situation"}, "TRUE"));
        //dbの参照でリアルタイムスイッチのオンオフを判断 データがある場合のみ入力監視を開始する
        if(Watcher.isChecked()){
            textBOX.addTextChangedListener (TW);
            myLibrary.dataInsert(modeDB,"watcherModeData",
                    new String[]{"situation"},new String[]{"TRUE"});
        }
        else{
            textBOX.removeTextChangedListener (TW);
            myLibrary.dataAllDelete(modeDB,"watcherModeData");
        }
        //compound btnイベント呼び出し//
        maxCursor = modeDB.query("maxLengthData",new String[]{"size"},null,null,null,null,null);
        if (maxCursor.moveToFirst()){
            maxText[0] = new InputFilter.LengthFilter(Integer.parseInt(maxCursor.getString(0)));
            textBOX.setFilters(maxText);
        }
    }
}