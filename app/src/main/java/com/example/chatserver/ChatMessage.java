package com.example.chatserver;

public class ChatMessage {
    public boolean right;
    public String type;
    public String message;
    public String fileName;

    public ChatMessage(boolean right, String type, String message) {
        super();
        this.right = right;
        this.type = type;
        this.message = message;
    }
    public ChatMessage(boolean right, String type, String fileName, String message) {
        super();
        this.right = right;
        this.type = type;
        this.fileName = fileName;
        this.message = message;
    }
}
