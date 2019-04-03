package com.example.chatserver;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.StringTokenizer;

public class ChatScreen extends AppCompatActivity {

    Toolbar Toolbar;

    static String mno = "";
    static int img;

    private MediaRecorder recorder = null;
    public static MediaPlayer player = null;
    String fileName;

    static boolean chatReceiver = true;
    ImageButton btnSend,btnSendAudio;
//    static EditText txtMsg;
    static CustomEditText txtMsg;
    static Socket s1;
    static DataInputStream dis;
    static DataOutputStream dos;
    static String username;
    static Context con;
    static MsgReceiver mr;
    static String audioFilePath;



    public static ChatArrayAdapter chatArrayAdapter;
    public static ListView listView;
//    private EditText chatText;
//    private ImageButton buttonSend,backbutton;
    private boolean side;
//    android.support.v7.widget.Toolbar Toolbar;
//    TextView mTextView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);


        con = this;
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/ChatApp");
        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();
        System.out.println(""+var);
        folder = new File(Environment.getExternalStorageDirectory()
                + "/ChatApp/Audio");
        var = false;
        if (!folder.exists())
            var = folder.mkdir();
        System.out.println(""+var);
        folder = new File(Environment.getExternalStorageDirectory()
                + "/ChatApp/Audio/Sent");
        var = false;
        if (!folder.exists())
            var = folder.mkdir();
        System.out.println(""+var);
        folder = new File(Environment.getExternalStorageDirectory()
                + "/ChatApp/Image");
        var = false;
        if (!folder.exists())
            var = folder.mkdir();
        System.out.println(""+var);
        folder = new File(Environment.getExternalStorageDirectory()
                + "/ChatApp/Image/Sent");
        var = false;
        if (!folder.exists())
            var = folder.mkdir();
        System.out.println(""+var);

        fileName = Environment.getExternalStorageDirectory() + "/ChatApp"; //"/ChatApp/Audio/audiorecordtest.3gp";

        Toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        //mTextView = (TextView)findViewById(R.id.txtConatctName);
        final ImageView dp = (ImageView)findViewById(R.id.userimage);

        Bundle mBundle = getIntent().getExtras();
        try {

            if (mBundle != null) {
                Toolbar.setTitle("     "+mBundle.getString("name"));
                mno = mBundle.getString("mno");
                img = mBundle.getInt("img");
            }
            //Send to User
            username = mno;
        }catch (Exception ex){
            Log.d("xxxxxxxxxx3",""+ex);
        }

        dp.setImageResource(img);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(Toolbar);

        /////////////////////////////
        try{

            btnSend = (ImageButton) findViewById(R.id.btnSend);
            //btnLogout = (Button) findViewById(R.id.btn_logout);
            btnSendAudio = (ImageButton) findViewById(R.id.btnRecord);

//            btnPlayFile = (Button) findViewById(R.id.btnPlayFile);
//            txtChat = (TextView) findViewById(R.id.txtChat);

            txtMsg = (CustomEditText) findViewById(R.id.txtMsg);



            txtMsg.setDrawableClickListener(new DrawableClickListener() {
                public void onClick(DrawablePosition target) {
                    switch (target) {
                        case LEFT:
                            Toast.makeText(ChatScreen.this,getDateTimeStamp(),Toast.LENGTH_SHORT).show();
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            Uri uri  = Uri.parse("/raw//storage/emulated/0/photo.jpg");
//                            Uri uri  = Uri.parse(Environment.getExternalStorageDirectory()+"photo.jpg");
                            takePicture.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(takePicture, 0);//zero can be replaced with any action code

                            break;
                        case RIGHT:
                            Toast.makeText(ChatScreen.this,"RIGHT",Toast.LENGTH_SHORT).show();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 1);//one can be replaced with any action code

                            break;
                        case BOTTOM:
                            Toast.makeText(ChatScreen.this,"BOTTOM",Toast.LENGTH_SHORT).show();
                            break;
                        case TOP:
                            Toast.makeText(ChatScreen.this,"TOP",Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ChatScreen.this,"Default",Toast.LENGTH_SHORT).show();
                    }
                }

            });



            try{
                if(LoginActivity.user.getS() != null){
                    s1 = LoginActivity.user.getS();
                    dis = new DataInputStream(s1.getInputStream());
                    dos = new DataOutputStream(s1.getOutputStream());

                    mr = new MsgReceiver(s1,con);
                    // Create a new Thread with this object.
                    mr.t = new Thread(mr);
                    mr.t.start();

                }else{
                    Log.d("xxxxxxxxxx1","else");
                }
            }catch (Exception e){
                Log.d("xxxxxxxxxx2",""+e);
            }


        listView = findViewById(R.id.msgview);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);


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




            txtMsg.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!s.equals("") ) {
                        //do your work here
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                public void afterTextChanged(Editable s) {
                    if(txtMsg.getText().equals("")){
                    }
                }
            });

//            btnPlayFile.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(ChatScreen.this,"started playing",Toast.LENGTH_SHORT).show();
//                    startPlaying();
//                }
//            });




            btnSendAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


            btnSendAudio.setOnTouchListener(new View.OnTouchListener() {


                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch(motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // PRESSED
                            Toast.makeText(ChatScreen.this,"recording",Toast.LENGTH_SHORT).show();
                            //btnSendAudio.setBackgroundResource(R.drawable.buttontry);
                            btnSendAudio.setBackgroundResource(R.drawable.ic_mic_black_96dp);
                            audioFilePath = fileName+"/Audio/sent/"+"AUD"+getDateTimeStamp()+".3gp";
                            startRecording(audioFilePath);
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            // RELEASED
                            Toast.makeText(ChatScreen.this,"recording stopped",Toast.LENGTH_SHORT).show();
                            btnSendAudio.setBackgroundResource(R.drawable.button);
                            stopRecording();
                            try{
                                File file = new File(audioFilePath);
                                new FileSendTask().execute("192.168.43.142",LoginActivity.user.getMno(),username,""+file.length(),"Audio/sent/"+file.getName(),"Audio");
                                //new FileSendTask().execute("",username,""+fi.length(),"Audio/audiorecordtest.3gp");
//                                    sendFile();
                                Log.d("GAYUU","msg:"+txtMsg.getText());
                                //setChatText("me: Sent Audio File \n ");
                                chatArrayAdapter.add(new ChatMessage(true, "Audio", "Audio/Sent/"+file.getName(), "File Sent"));
                            }catch (Exception e){
                                Toast.makeText(ChatScreen.this,"erroraiyaaa:"+e,Toast.LENGTH_LONG).show();
                            }
                            return true; // if you want to handle the touch event
                    }
                    return true;
                }
            });


            //        btnLogout.setOnClickListener(new View.OnClickListener() {
            //            @Override
            //            public void onClick(View v) {
            //                stopReceiveFlag = true;
            //                try {
            //                    new ChatSendTask().execute("192.168.43.142","logout");
            //                    Intent myIntent = new Intent(ChatScreen.this, LoginActivity.class);
            //                    startActivity(myIntent);
            //                } catch (Exception e) {
            //                    Toast.makeText(ChatScreen.this,"error:"+e,Toast.LENGTH_LONG).show();
            //                }
            //            }
            //        });


            btnSend.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    if(!(txtMsg.getText().equals(""))){
                        //setChatText("me: "+txtMsg.getText()+"\n ");
                        Toast.makeText(ChatScreen.this,"msg:"+txtMsg.getText(),Toast.LENGTH_LONG).show();
                        try{
                            new ChatSendTask().execute("192.168.43.142",username,txtMsg.getText().toString());
                            Log.d("GAYUU","msg:"+txtMsg.getText());
                            sendChatMessageRight();
                        }catch (Exception e){
                            Toast.makeText(ChatScreen.this,"erroraiyaaa:"+e,Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });


            ///////////////////
        }catch (Exception ex){
            Log.d("xxxxxxxxxx",""+ex);
        }


//        buttonSend = (ImageButton) findViewById(R.id.send);
//        listView = (ListView) findViewById(R.id.msgview);
//        chatText = (EditText) findViewById(R.id.msg);
//        Toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
//
//        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//
//
//        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
//        listView.setAdapter(chatArrayAdapter);
//
//        buttonSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                sendChatMessageRight();
//            }
//        });
//
//        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//        listView.setAdapter(chatArrayAdapter);
//
//        //to scroll the list view to bottom on data change
//        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                listView.setSelection(chatArrayAdapter.getCount() - 1);
//            }
//        });
//
//
//        Bundle mBundle = getIntent().getExtras();
//        if (mBundle != null){
////            Toolbar.setTitle(mBundle.getString("ConatctPhoto"));
//            Toolbar.setTitle(mBundle.getString("ContactName"));
//        }
//        // Setting toolbar as the ActionBar with setSupportActionBar() call
//        setSupportActionBar(Toolbar);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){

                    String path = Environment.getExternalStorageDirectory()+"/photo.jpg";
                    Toast.makeText(ChatScreen.con,""+path,Toast.LENGTH_SHORT).show();
                    chatArrayAdapter.add(new ChatMessage(true, "Image", ""+path, "File Sent"));

                    //Uri selectedImage = imageReturnedIntent.getData();
//                    if(selectedImage==null){
//                        Toast.makeText(ChatScreen.con,"isnull",Toast.LENGTH_SHORT).show();
//                    }
                    //Toast.makeText(ChatScreen.con,""+selectedImage.getPath(),Toast.LENGTH_SHORT).show();
                    //File fi = new File(selectedImage.getPath());
                    //chatArrayAdapter.add(new ChatMessage(true, "Image", ""+selectedImage.getPath(),"Image 6e"));

                    //imageview.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String path = selectedImage.getPath();
                    path = path.replaceAll("/raw/","");
                    //File fi = new File(selectedImage.getPath());
                    //"Image/sent/"+file.getName()
                    Toast.makeText(ChatScreen.con,""+path,Toast.LENGTH_SHORT).show();
                    File src = new File(path);
                    String extension = src.getName().substring(src.getName().lastIndexOf("."));
                    File dst = new File(fileName+"/Image/sent/IMG"+getDateTimeStamp()+extension);
                    try{
                        copy(src,dst);
                    }catch (Exception e){
                        Log.d("Exxx",""+e);
                    }

                    new FileSendTask().execute("192.168.43.142",LoginActivity.user.getMno(),username,""+dst.length(),"Image/sent/"+dst.getName(),"Image");
                    chatArrayAdapter.add(new ChatMessage(true, "Image", "Image/sent/"+dst.getName(), "File Sent"));

                    //Toast.makeText(ChatScreen.con,""+Environment.getExternalStorageDirectory(),Toast.LENGTH_SHORT).show();
//                    imageview.setImageURI(selectedImage);
                }
                break;
        }
    }

    private boolean sendChatMessageRight() {
        side=true;
        chatArrayAdapter.add(new ChatMessage(side, "Chat", txtMsg.getText().toString()));
        txtMsg.setText("");
        return true;
    }
    public boolean sendChatMessageLeft() {
        side=false;
        chatArrayAdapter.add(new ChatMessage(side, "Chat", txtMsg.getText().toString()));
        txtMsg.setText("");
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





//    public static void setChatText(String reply){
//        txtChat.setText(txtChat.getText()+reply);
//    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }


    public void startRecording(String fileN) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileN);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("aaaaaaaa", "prepare() failed");
        }

        recorder.start();
    }

    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public String getDateTimeStamp(){
        String AM_PM;
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.AM_PM)==0){
            AM_PM = "AM";
        }else{
            AM_PM = "PM";
        }
        Toast.makeText(ChatScreen.con,""+cal.get(Calendar.DATE)+(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.YEAR),Toast.LENGTH_SHORT).show();
        return ""+cal.get(Calendar.DATE)+(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.YEAR)+"_"+cal.get(Calendar.HOUR_OF_DAY)+cal.get(Calendar.MINUTE)+cal.get(Calendar.SECOND);
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

}


class ChatSendTask extends AsyncTask<String , Void, Void>
{
    @Override
    protected Void doInBackground(String... strs)
    {
        try {
            String request = "$chat$%$%"+strs[1]+"%$%"+strs[2];
            ChatScreen.dos.writeUTF(request);
            Log.d("ChatSendTaskTry:",""+request);
        } catch (Exception ex) {
            //Toast.makeText(MainActivity.con,"ex2222"+ex,Toast.LENGTH_LONG).show();
            Log.d("ChatSendTaskCatch:",""+ex);
        }
        return null;
    }
}

class FileSendTask extends AsyncTask<String , Void, Void>
{
    @Override
    protected Void doInBackground(String... strs)
    {
        try {
            StringTokenizer st = new StringTokenizer(strs[4],"/");
            st.nextToken();
            st.nextToken();
            String fname = st.nextToken();

            String request = "$file$%$%"+strs[1]+"%$%"+strs[2]+"%$%"+strs[3]+"%$%"+strs[5]+"/"+fname;

            //sender,recipient,filesize,filename
            Log.d("recev:",""+request);
            int len = Integer.parseInt(strs[3]);
            ChatScreen.dos.writeUTF(request);
            byte[] filebytes = new byte[len];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory() + "/ChatApp/"+strs[4]));
            bis.read(filebytes, 0, len);
            bis.close();
            OutputStream os = ChatScreen.s1.getOutputStream();

            //if(ChatScreen.dis2.readUTF().equals("yes")){
            Log.d("disRead","ayu aiya");
            int times = len/(64 * 1024);

            if(len > (64 * 1024)){
                int curr = 0;
                for (int i = 0; i < times; i++) {
                    System.out.println("i = "+i);
                    os.write(filebytes, curr, 64*1024);
                    curr += 64*1024;
                }
                os.write(filebytes, curr, len-curr);
            }else{
                os.write(filebytes, 0, len);
            }
            Log.d("dosWrite","sent file");
//            }else{
//                Log.d("aaass","received no");
//            }
        } catch (Exception ex) {
            //Toast.makeText(MainActivity.con,"ex2222"+ex,Toast.LENGTH_LONG).show();
            Log.d("FileSendTaskCatch:",""+ex);
        }
        return null;
    }

}





class MsgReceiver implements Runnable
{
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    Context c;
    boolean isloggedin;
    Thread t;

    // constructor
    public MsgReceiver(Socket s,Context c) throws IOException {
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
        this.s = s;
        this.isloggedin=true;
        this.c = c;
        System.out.println("started receiver...");
    }

    @Override
    public void run() {

        String received;
        while (ChatScreen.chatReceiver)
        {
            try
            {
                // receive the string
                received = dis.readUTF();
                Log.d("received",""+received);
                if(received.contains("$chat$")){
                    StringTokenizer st = new StringTokenizer(received, "%$%");
                    st.nextToken();
                    String sender = st.nextToken();
                    String msg = st.nextToken();

                    final String temp = msg;
                    System.out.println("Rec:"+received);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            ChatScreen.chatArrayAdapter.add(new ChatMessage(false, "Chat", temp));
                            //ChatScreen.setChatText(temp + "\n");
                        }
                    });
                }

                if (received.contains("$file$")) {

                    Log.d("FILLEEEE:","AYU");
                    StringTokenizer st = new StringTokenizer(received, "%$%");
                    st.nextToken();
                    String sender = st.nextToken();
                    int fileSize = Integer.parseInt(st.nextToken());
                    final String filename = st.nextToken();
                    //byte[] filebytes = new byte[fileSize];
                    System.out.println("sender:" + sender + ",filename:" + filename + ",size:" + fileSize);
                    st = new StringTokenizer(filename,"/");
                    final String filetype = st.nextToken();
                    if (fileSize < (10000 * 1024)) {
                        //ddos.writeUTF("yes");
                        receiveFile(filename, fileSize, s.getInputStream());
                        final String temp = "from:"+sender+"file:"+filename;
                        System.out.println("Rec:"+received);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                //ChatScreen.setChatText(temp + "\n");
                                ChatScreen.chatArrayAdapter.add(new ChatMessage(false, ""+filetype, ""+filename, "File Received"));
                            }
                        });

                    } else {
                        //ddos.writeUTF("no");
                        Log.d("dosWrite","NO");
                    }
                }

//                if (received.contains("$call$")) {
//                    StringTokenizer st = new StringTokenizer(received, "%$%");
//                    st.nextToken();
//                    String sender = st.nextToken();
//                    int buffSize = Integer.parseInt(st.nextToken());
//                    //String filename = st.nextToken();
//                    //byte[] filebytes = new byte[fileSize];
//                    System.out.println("sender:" + sender + "buffSize:" + buffSize);
//
//                    ChatScreen.audioCall.startCall();
//                    Log.d("aaaa","su thayu?");
//                }





                Thread.sleep(1000);

//                if(ChatScreen.stopReceiveFlag){
//                    System.out.println("breaked!!");
//                    break;
//                }
            } catch (IOException e) {
                System.out.println(""+e);
                break;
            } catch (InterruptedException ex) {
                System.out.println(""+ex);
            }

        }
//        try
//        {
//            // closing resources
//            this.dis.close();
//            this.dos.close();
//
//        }catch(IOException e){
//            e.printStackTrace();
//        }
    }

    public void receiveFile(String filename, int fileSize, InputStream inputStream) throws FileNotFoundException, IOException {

        try{

            byte[] filebytes = new byte[fileSize];
            final String saveFile = Environment.getExternalStorageDirectory()
                    + "/ChatApp/" + filename;

            FileOutputStream fos = new FileOutputStream(saveFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            int bytesRead = 1;
            int curr = 0;
            while (bytesRead > 0) {
                System.out.println("a");
                bytesRead = inputStream.read(filebytes, curr, filebytes.length - curr);
                System.out.println("byteRead " + bytesRead);
                curr += bytesRead;
            }
            bos.write(filebytes, 0, filebytes.length);
            bos.flush();
            bos.close();
            System.out.println("File downloaded..." + filebytes.length);





        }catch (Exception e){
            System.out.println("ahan"+e);
        }
    }
}
