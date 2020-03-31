package com.example.music_compromiser.ui.login;

public class Song {

    private String id;
    private String name;
    private String uri;
    private String artist;


//    public Song(String id, String name) {
//        this.name = name;
//        this.id = id;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri){
        this.uri = uri;
    }

    public String getUri(){
        return uri;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public String getArtist(){
        return artist;
    }



}
