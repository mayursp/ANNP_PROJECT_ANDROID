package com.example.chatserver;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    public static User user;
    public static ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_CONTACTS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.RECORD_AUDIO) ) {

                Toast.makeText(LoginActivity.this,"Please Grant Permissions",Toast.LENGTH_SHORT).show();

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO},
                        12);//MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Toast.makeText(LoginActivity.this,"All Permissions Granted!",Toast.LENGTH_SHORT).show();
        }





        con = LoginActivity.this;

        user = new User();
        final EditText uname = (EditText) findViewById(R.id.uname);
        final EditText paswd = (EditText) findViewById(R.id.paswd);
        final Button btn1 = (Button) findViewById(R.id.btn_login);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(ProgressBar.INVISIBLE);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Toast.makeText(LoginActivity.this,uname.getText()+"ayu",Toast.LENGTH_LONG).show();
                new MyAsyncTask().execute(uname.getText().toString(),paswd.getText().toString());
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
                    dos.writeUTF(strings[0]+"%$%"+strings[1]);
                    if(dis.readUTF().equals("true")){
                        LoginActivity.user.setS(s1);
                        LoginActivity.user.setMno(strings[0]);
                    }
                }

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
            Intent myIntent = new Intent(LoginActivity.con, MainScreen.class);
            LoginActivity.con.startActivity(myIntent);
        }
    }

}
