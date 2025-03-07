package com.myapp.textcounter.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.LinkedList;

import com.myapp.textcounter.model.MyLibrary;
import com.myapp.textcounter.R;
import com.myapp.textcounter.db.InputHistory;

public class ListActivity extends AppCompatActivity {

    static MyLibrary myLibrary = new MyLibrary();

    ListView listView;
    static ArrayAdapter<String> adapter;
    //リストタッチされてからダイアログに出力する際の受け渡し用
    static String item1= null,item2= null,item3= null,item4 = null;
    static LinkedList<String>  //dbから取り出した各データを個々にリストのインスタンスを生成する
            view_list = new LinkedList<>(),
            uuid_list = new LinkedList<>(),
            date_list = new LinkedList<>(),
            text_list = new LinkedList<>(),
            line_list = new LinkedList<>() ,
            break_list = new LinkedList<>() ,
            empty_list = new LinkedList<>() ;

    private InputHistory helper;
    private static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*前回のタイミングでリスト内にデータが残っているため、クリアしないでそのまま使いまわすと、
        データが上積みされて、押したpositionの位置とリストの要素の位置がズレて、タッチしたリストと
        違うデータが取り出されてしまうので、格納する前のタイミングで初期化する*/
        view_list.clear();
        uuid_list.clear();
        date_list.clear();
        text_list.clear ();
        line_list.clear ();
        break_list.clear ();
        empty_list.clear ();

        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_list);
        //dbのヘルパーのインスタンスがnullの場合はインスタンスを作成する
        if(helper == null){ helper = new InputHistory(getApplicationContext());}
        //dbが作成されていない場合は
        if(db == null){db = helper.getReadableDatabase();}

        Button back = findViewById (R.id.button_back);
        back.setOnClickListener (new Back ());

        listView = findViewById(R.id.textList);
        listView.setOnItemClickListener(new ViewEvent());//リストにクリックイベントを登録
        //コンテキストメニューを表示させるviewを登録する
        registerForContextMenu(listView);
        //listviewに紐づける配列リストをアダプターに登録する
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,view_list);

        Cursor cursor = db.query("textData", new String[] {"_id","date","description","line","break","empty"},
                null,null,null,null,null);
        //レコードにデータがあるかどうかを判断
        if(cursor.moveToFirst()){
            //iがカーソルのする数(レコードの数)になるまで出力を行う
            for (int i = 0 ;i<cursor.getCount();i++){
            /*listActivityを開いた時点でdbからlinkedListに
            １レコードずつ配列に１要素ずつ格納されているlistviewで
            リストがタッチされた際に格納されたlinkedListからデータ
            の位置を参照して、ダイアログに渡している。*/

                uuid_list.add(cursor.getString(0));
                date_list.add(cursor.getString(1));
                text_list.add(cursor.getString(2));
                line_list.add(cursor.getString(3));
                break_list.add(cursor.getString(4));
                empty_list.add(cursor.getString(5));

                //データを取り出しアダプターに格納
                if(cursor.getString(2).length() >15) {
                    //日時とテキストを取り出し、そのデータをlistアダプターに登録したlinkedListに登録する
                    view_list.add(date_list.get(i) + "\n" + text_list.get(i).substring(0, 15) + "...");
                } else {
                    view_list.add(date_list.get(i) + "\n" + text_list.get(i));
                }
                //次の行に移動
                cursor.moveToNext();
            }
        }else {
            myLibrary.msgToast(this,R.string.burble_message_noData,Toast.LENGTH_LONG);
        }
        //for文の処理が終わってから初めてlistviewにadapterの情報を反映させる
        listView.setAdapter(adapter);
        cursor.close();
    }

    public class ViewEvent implements ListView.OnItemClickListener{
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            listView = (ListView) parent;
            //リストからデータを取り出す際にリストをタッチした際のpositionでリストの要素を指定して、データを取り出す。
            //取り出したデータをダイアログで出力するために、変数itemにデータをセットする
            item1 = text_list.get(position);
            item2 = line_list.get(position);
            item3 = break_list.get(position);
            item4 = empty_list.get(position);
            //タッチした際に定義したダイアログのクラスを呼び出す
            new TextDetail_DialogFragment().show(getSupportFragmentManager(), "my_dialog2");
        }
    }
    public static class TextDetail_DialogFragment extends DialogFragment {
        @NonNull @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View myDia = inflater.inflate(R.layout.dialog_list, null);
            TextView letter = myDia.findViewById(R.id.text_letters2),
                    line   = myDia.findViewById(R.id.text_lines2),
                    breaks = myDia.findViewById(R.id.text_break2),
                    empty  = myDia.findViewById(R.id.text_empty2),
                    scroll = myDia.findViewById(R.id.textViewer);
            letter.setText(myLibrary.Count(item1));
            line.setText(item2);
            breaks.setText(item3);
            empty.setText(item4);
            scroll.setText(item1);

            /* listviewが開始したタイミング(onCreate内)の時しかデータを格納
            * してないのでダイアログ出力した時点でクリアしてしまうと今度、listviewで別の
            * リスト をタッチした際に空になったlinkedListからデータを参照する
            * こととなりNPEが発生してしまうので、リスト画面を表示し、dbからlinkedListに
            * 格納する直前に行う
            * setTextしたタイミングでクリアは行わない */
            return new AlertDialog.Builder(requireActivity()).setView(myDia).setPositiveButton(R.string.button_bck,null).create();
        }
    }

    class Back implements View.OnClickListener{public void onClick(View v){new Intent (ListActivity.this,MainActivity.class);finish ();}}

    //単削除
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.listview_context_menu, menu);
    }
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.item1) {
            adapter.remove(myLibrary.format2(info.position));
            myLibrary.dataRowDelete(db, "textData", new String[]{uuid_list.get(info.position)});
            view_list.remove(info.position);
            uuid_list.remove(info.position);
            date_list.remove(info.position);
            text_list.remove(info.position);
            line_list.remove(info.position);
            break_list.remove(info.position);
            empty_list.remove(info.position);
            return true;
        }
        return super.onContextItemSelected(item);
    }
    //全削除
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {getMenuInflater().inflate(R.menu.list_option_menu, menu);return true;}
    public boolean onOptionsItemSelected(MenuItem item) {// オプションメニューのアイテムが選択されたときに呼び出されるメソッド
        if (item.getItemId() == R.id.item1) {
            new deleteDialogFragment().show(getSupportFragmentManager(), "my_dialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static class deleteDialogFragment extends DialogFragment {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(requireActivity())
                    .setMessage(R.string.dialog_message_allDelete)
                    .setNegativeButton(R.string.dialog_please_no, null)
                    .setPositiveButton(R.string.dialog_please_yes, (dialog, which) -> {
                        adapter.clear();
                        myLibrary.dataAllDelete(db, "textData");
                    }).create();
        }
    }
}

