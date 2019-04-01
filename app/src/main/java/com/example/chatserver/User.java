package com.example.chatserver;

import java.net.Socket;

public class User {
    private Socket s;
    private String mno;



    public User(){
    }


    public Socket getS(){
        return s;
    }

    public void setS(Socket s){
        this.s = s;
    }

    public String getMno(){
        return mno;
    }

    public void setMno(String mno){
        this.mno = mno;
    }

}
