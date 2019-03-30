package com.example.chatserver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainScreen extends AppCompatActivity {

    int[] imageid = {R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.four,R.drawable.five,
            R.drawable.six};
    String[] name = {"Mayur","Arkan Khan","Arkan Malek","Yash","Tapshwi","Chelson"};
//    String[] descriptions = {"onee","twoo","three","fourr","fivee","sixx","sevenn","eightt","ninee","tenn"};

    ArrayAdapter<String> adapter;
    Toolbar mToolbar;
    ListView mListView;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.drawable.aa));
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(mToolbar);

        mListView = (ListView)findViewById(R.id.listview);
        MyAdapter myAdapter = new MyAdapter(MainScreen.this, imageid, name);
        mListView.setAdapter(myAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        registerForContextMenu(mListView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(MainScreen.this, ChatScreen.class);
                intent.putExtra("ContactName",name[i]);
                intent.putExtra("ConatctPhoto",imageid[i]);
                startActivity(intent);
            }
        });

    }
    ///Popup MENU....
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.popup_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.pmnuDelete:
                Toast.makeText(getApplicationContext(), "You clicked delete on Item : " , Toast.LENGTH_SHORT).show();
                break;
            case R.id.pmnuEdit:
                Toast.makeText(getApplicationContext(), "You clicked edit on Item : " , Toast.LENGTH_SHORT).show();
                break;
            case R.id.pmnuShare:
                Toast.makeText(getApplicationContext(), "You clicked share on Item : " , Toast.LENGTH_SHORT).show();
                break;

        }

        return super.onContextItemSelected(item);
    }

    ///CONTEXT MENU...
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.newchat:
                Toast.makeText(this,"Select NewChat",Toast.LENGTH_SHORT).show();
                Intent m1 = new Intent(MainScreen.this, SettingScreen.class);
                startActivity(m1);
                return true;
            case R.id.setting:
                Toast.makeText(this,"Select Setting",Toast.LENGTH_SHORT).show();
                Intent m2 = new Intent(MainScreen.this, SettingScreen.class);
                startActivity(m2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}