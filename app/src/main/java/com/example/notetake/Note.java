package com.example.notetake;


public class Note {
    public String id, title, content, imageUrl;

    public Note() {}

    public Note(String id, String title, String content, String imageUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}

