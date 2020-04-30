package com.example.music_compromiser.ui.login;

public class Song {

    private String id;
    private String name;
    private String uri;
    private String songOwner;
    private String artist;
    private int numOfOccurences;


//    public Song(String id, String name) {
//        this.name = name;
//        this.id = id;
//    }
    public void setNumOfOccurences(int numOfOccurences){
        this.numOfOccurences = numOfOccurences;
    }

    public int getNumOfOccurences(){
        return numOfOccurences;
    }

    public void setSongOwner(String songOwner){
        this.songOwner = songOwner;
    }

    public String getSongOwner(){
        return songOwner;
    }

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
