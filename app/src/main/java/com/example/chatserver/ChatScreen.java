package com.example.chatserver;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatScreen extends AppCompatActivity {

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private ImageButton buttonSend,backbutton;
    private boolean side;
    android.support.v7.widget.Toolbar Toolbar;
    TextView mTextView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);


        buttonSend = (ImageButton) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);
        chatText = (EditText) findViewById(R.id.msg);
        Toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);

        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessageRight();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });


        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null){
//            Toolbar.setTitle(mBundle.getString("ConatctPhoto"));
            Toolbar.setTitle(mBundle.getString("ContactName"));
        }
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(Toolbar);
    }


    private boolean sendChatMessageRight() {
        side=true;
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        chatText.setText("");
        return true;
    }
    private boolean sendChatMessageLeft() {
        side=false;
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        chatText.setText("");
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle Menu Item's selection...
        switch (item.getItemId()) {
            case R.id.videocall:
                Toast.makeText(this,"Select VideoCall",Toast.LENGTH_SHORT).show();
//                Intent m1 = new Intent(ChatScreen.this, SettingScreen.class);
//                startActivity(m1);
                return true;
            case R.id.call:
                Toast.makeText(this,"Select Call",Toast.LENGTH_SHORT).show();
//                Intent m1 = new Intent(ChatScreen.this, SettingScreen.class);
//                startActivity(m1);
                return true;
            case R.id.newchat:
                Toast.makeText(this,"Select NewChat",Toast.LENGTH_SHORT).show();
//                Intent m1 = new Intent(ChatScreen.this, SettingScreen.class);
//                startActivity(m1);
                return true;
            case R.id.setting:
                Toast.makeText(this,"Select Setting",Toast.LENGTH_SHORT).show();
//                Intent m2 = new Intent(ChatScreen.this, SettingScreen.class);
//                startActivity(m2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
