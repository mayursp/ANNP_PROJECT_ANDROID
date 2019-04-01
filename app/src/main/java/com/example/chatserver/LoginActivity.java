package com.example.chatserver;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {

    public static Context con;
    public static String tempStr;
    public static User user;
    public static ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        con = LoginActivity.this;

        user = new User();
        final EditText et1 = (EditText) findViewById(R.id.uname);
        final EditText et2 = (EditText) findViewById(R.id.paswd);
        final Button btn1 = (Button) findViewById(R.id.btn_login);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(ProgressBar.INVISIBLE);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Toast.makeText(LoginActivity.this,et1.getText()+"ayu",Toast.LENGTH_LONG).show();
                new MyAsyncTask().execute("1234",et1.getText().toString(),et2.getText().toString());
            }
        });
    }

    // The types specified here are the input data type, the progress type, and the result type
    class MyAsyncTask extends AsyncTask<String, Void, User> {
        protected void onPreExecute() {
            // Runs on the UI thread before doInBackground
            // Good for toggling visibility of a progress indicator
            LoginActivity.pb.setVisibility(ProgressBar.VISIBLE);
        }

        protected User doInBackground(String... strings) {
            try {
//            Socket s1;
//            DataInputStream dis;
//            DataOutputStream dos;
                Socket s1 = new Socket("192.168.43.142", 1234);
                DataInputStream dis = new DataInputStream(s1.getInputStream());
                DataOutputStream dos = new DataOutputStream(s1.getOutputStream());

                dos.writeUTF("$login$");
                if(dis.readUTF().equals("true")){
                    dos.writeUTF(strings[1]+"%$%"+strings[2]);
                    if(dis.readUTF().equals("true")){
                        LoginActivity.user.setS(s1);
                        LoginActivity.user.setMno(strings[1]);
                    }
                }

//            s1 = new Socket("192.168.43.142", 5678);
//            dis = new DataInputStream(s1.getInputStream());
//            dos = new DataOutputStream(s1.getOutputStream());
//
//            dos.writeUTF("$login$");
//            if(dis.readUTF().equals("true")){
//                dos.writeUTF(strings[1]+"%$%"+strings[2]);
//                if(dis.readUTF().equals("true")){
//                    LoginActivity.user.setS2(s1);
//                }
//            }

                return LoginActivity.user;
            } catch (Exception ex) {
                Log.d("EEERRRORRR:",""+ex);
            }
            return null;
        }


        protected void onProgressUpdate() {
            // Executes whenever publishProgress is called from doInBackground
            // Used to update the progress indicator
            //MainActivity.pb.setProgress(values[0]);
        }

        protected void onPostExecute(User u) {
            LoginActivity.pb.setVisibility(ProgressBar.INVISIBLE);
//        Intent myIntent = new Intent(LoginActivity.con, ContactsActivity.class);
            Intent myIntent = new Intent(LoginActivity.con, MainScreen.class);
            LoginActivity.con.startActivity(myIntent);
        }
    }


}
