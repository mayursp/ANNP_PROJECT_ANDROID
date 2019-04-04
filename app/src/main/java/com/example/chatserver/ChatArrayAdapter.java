package com.example.chatserver;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {
    private TextView chatText;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;
    private ImageButton btnPlay;
    private MediaPlayer player = null;
    private String fileN;
    private ImageView imageView;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        player = ChatScreen.player;
        fileN = Environment.getExternalStorageDirectory() + "/ChatApp/";
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.right) {
            switch (chatMessageObj.type){
                case "Chat":
                    row = inflater.inflate(R.layout.right, parent, false);
                    chatText = (TextView) row.findViewById(R.id.msgr);
                    chatText.setText(chatMessageObj.message);
                    break;
                case "Audio":
                    row = inflater.inflate(R.layout.filesent, parent, false);
                    chatText = (TextView) row.findViewById(R.id.msgr);
                    chatText.setText(chatMessageObj.message);
                    btnPlay = (ImageButton) row.findViewById(R.id.btnPlayr);
                    final String temp = chatMessageObj.fileName;
                    btnPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startPlaying(fileN + temp);
                        }
                    });
                    break;
                case "Image":
                    row = inflater.inflate(R.layout.imagesent, parent, false);
                    imageView = (ImageView) row.findViewById(R.id.msgImg);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(fileN+chatMessageObj.fileName));//"/Image/imagetest.jpg"));
                    break;
                case "Video":

                    break;
                default:

            }
//            if(chatMessageObj.isAudio){
//                row = inflater.inflate(R.layout.filesent, parent, false);
//            }else{
//                row = inflater.inflate(R.layout.right, parent, false);
//            }
        }else{
            switch (chatMessageObj.type){
                case "Chat":
                    row = inflater.inflate(R.layout.left, parent, false);
                    chatText = (TextView) row.findViewById(R.id.msgr);
                    chatText.setText(chatMessageObj.message);
                    break;
                case "Audio":
                    row = inflater.inflate(R.layout.filereceive, parent, false);
                    chatText = (TextView) row.findViewById(R.id.msgr);
                    chatText.setText(chatMessageObj.message);
                    btnPlay = (ImageButton) row.findViewById(R.id.btnPlayr);
                    final String temp = chatMessageObj.fileName;
                    btnPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startPlaying(fileN + temp);
                        }
                    });
                    break;
                case "Image":
                    row = inflater.inflate(R.layout.imagereceive, parent, false);
                    imageView = (ImageView) row.findViewById(R.id.msgImg);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(fileN + chatMessageObj.fileName));//fileN+chatMessageObj.fileName));
                    break;
                case "Video":
                    break;
                default:

            }
//            if(chatMessageObj.isAudio){
//                row = inflater.inflate(R.layout.filereceive, parent, false);
//            }else{
//                row = inflater.inflate(R.layout.left, parent, false);
//            }
        }
//        if(chatMessageObj.isAudio){
//            btnPlay = (ImageButton) row.findViewById(R.id.btnPlayr);
//            btnPlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startPlaying();
//                }
//            });
//        }

        return row;
    }



    public void startPlaying(String fileName) {
        player = new MediaPlayer();
        try {
            //player.setDataSource(fileName+"/Audio/audiorecordtest.3gp");
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("aaaaaaaa", "prepare() failed"+e);
        }
    }

    public void stopPlaying() {
        player.release();
        player = null;
    }

    public String getTimeStamp(){
        String AM_PM;
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.AM_PM)==0){
            AM_PM = "AM";
        }else{
            AM_PM = "PM";
        }
        return ""+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+" "+AM_PM;
    }
}
