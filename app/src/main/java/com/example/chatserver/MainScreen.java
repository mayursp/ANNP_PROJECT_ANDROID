package com.example.chatserver;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;

public class MainScreen extends AppCompatActivity {

    int[] imageid = {R.drawable.one,R.drawable.one,R.drawable.one};
    String[] name = {"tapaswi","mayur","sarthak"};
    String[] mno = {"1","2","3"};

    Toolbar mToolbar;
    ListView mListView;
    static MyContact[] syncedContacts;
    static Context context;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        context = MainScreen.this;
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(mToolbar);

        mListView = (ListView)findViewById(R.id.listview);
        MyAdapter myAdapter = new MyAdapter(MainScreen.this, imageid, name, mno);
        mListView.setAdapter(myAdapter);
        //getSyncedContacts();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainScreen.this, ChatScreen.class);
                intent.putExtra("name",syncedContacts[i].name);
                intent.putExtra("mno",syncedContacts[i].mno);
//                intent.putExtra("name",ContactsActivity.syncedContacts[i].name);
//                intent.putExtra("mno",ContactsActivity.syncedContacts[i].mno);
                intent.putExtra("img",imageid[0]);
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
            case R.id.refresh:
                Toast.makeText(this,"Refreshing...",Toast.LENGTH_SHORT).show();

                syncContacts();

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

    public void getSyncedContacts(){

        //Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        String[] columns = new String[] { "name", "mno", "_id"};
        MatrixCursor cursor = new MatrixCursor(columns);
        startManagingCursor(cursor);

        for (int i=0;i<MainScreen.syncedContacts.length;i++){
            cursor.addRow(new Object[] { MainScreen.syncedContacts[i].name,MainScreen.syncedContacts[i].mno,MainScreen.syncedContacts[i].id });
        }

        String[] from = {"name","mno"};
        int[] to = {android.R.id.text1,android.R.id.text2};
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_2,cursor,from,to);
        mListView.setAdapter(simpleCursorAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }


    public void syncContacts(){
        //send file

        final String filename = Environment.getExternalStorageDirectory()
                + "/ChatApp/" + LoginActivity.user.getMno() + ".csv";

        new Thread() {
            public void run() {


                try {

                    exportContactsToCSV();

                    File file = new File(filename);
                    int len = (int)file.length();

                    //Socket socket = new Socket("192.168.43.142",1234);
                    Socket socket = LoginActivity.user.getS();
                    OutputStream outputStream = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(outputStream);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());

                    dos.writeUTF("$sync$%$%"+len+"%$%"+file.getName());
                    byte[] filebytes = new byte[len];

                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    bis.read(filebytes, 0, len);

                    if(dis.readUTF().equals("yes")){
                        int times = len/(64 * 1024);

                        if(len > (64 * 1024)){
                            int curr = 0;
                            for (int i = 0; i < times; i++) {
                                System.out.println("i = "+i);
                                outputStream.write(filebytes, curr, 64*1024);
                                curr += 64*1024;
                            }
                            outputStream.write(filebytes, curr, len-curr);
                        }else{
                            outputStream.write(filebytes, 0, len);
                        }
                        if(dis.readUTF().equals("received")){
                            dos.writeUTF("true");
                            String result = dis.readUTF();
                            Log.d("received", "result: " + result);
                            if (result != null) {
                                try {
                                    //JSONObject jsonObj = new JSONObject(result);
                                    // Getting JSON Array node
//                                    JSONArray contacts = jsonObj.getJSONArray("contacts");
                                    JSONArray contacts = new JSONArray(result);
                                    // looping through All Contacts
                                    syncedContacts = new MyContact[contacts.length()];
                                    for (int i = 0; i < contacts.length(); i++) {
                                        JSONObject c = contacts.getJSONObject(i);
                                        String name = c.getString("name");
                                        String mno = c.getString("mno");
                                        int id = c.getInt("_id");
                                        syncedContacts[i] = new MyContact(name,mno,id);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),
                                                        "Got Contacts",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                } catch (final JSONException e) {
                                    Log.e("eeerror", "Json parsing error: " + e.getMessage());
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(getApplicationContext(),
//                                                    "Json parsing error: " + e.getMessage(),
//                                                    Toast.LENGTH_LONG).show();
//                                        }
//                                    });
                                }
                            }

                        }else{
                            Toast.makeText(MainScreen.context,"Try Again2!!",Toast.LENGTH_LONG);
                        }

                    }else{
                        Toast.makeText(MainScreen.context,"Try Again!!",Toast.LENGTH_LONG);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSyncedContacts();
                        }
                    });

                } catch (Exception e) {
                    Log.d("Except:",""+e);
                }
            }
        }.start();
    }

    public void exportContactsToCSV() throws IOException {
        {

            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/ChatApp");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

            System.out.println("" + var);


            final String filename = folder.toString() + "/" + LoginActivity.user.getMno() + ".csv";

//            // show waiting screen
//            CharSequence contentTitle = getString(R.string.app_name);
//            final ProgressDialog progDailog = ProgressDialog.show(
//                    MainScreen.this, contentTitle, "processing...",
//                    true);//please wait
//            final Handler handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//
//                }
//            };
//            Looper.prepare();
            try {

                FileWriter fw = new FileWriter(filename);

//                        Cursor cursor = db.selectAll();
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[]
                        {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone._ID},null,null,null);
                startManagingCursor(cursor);


                fw.append("Name");
                fw.append(',');

                fw.append("No");
                fw.append(',');

                fw.append("Id");
                fw.append(',');

                fw.append('\n');

                if (cursor.moveToFirst()) {
                    for (int i=0;i<cursor.getColumnCount();i++){
                        fw.append(""+cursor.getColumnName(i));
                        fw.append(',');
                    }
                    fw.append('\n');
                    do {
                        fw.append(cursor.getString(0));
                        fw.append(',');

                        fw.append(cursor.getString(1));
                        fw.append(',');

                        fw.append(cursor.getString(2));
                        fw.append(',');

                        fw.append('\n');

                    } while (cursor.moveToNext());
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                // fw.flush();
                fw.close();

            } catch (Exception e) {
                Log.d("Except:",""+e);
            }
//            handler.sendEmptyMessage(0);
//            progDailog.dismiss();

        }

    }



}