package com.example.music_compromiser;

public class VotingValues {
    int canSkip = 0;
    int canSkipPrev = 0;
    String isPaused = "true";


    public void setCanSkip(int canSkip){
        this.canSkip = canSkip;
    }

    public int getCanSkip(){
        return canSkip;
    }

    public void setCanSkipPrev(){
        this.canSkipPrev = canSkipPrev;
    }

    public int getCanSkipPrev(){
        return canSkipPrev;
    }


    public void setIsPaused(String isPaused){
        this.isPaused = isPaused;
    }

    public String getIsPaused(){
        return isPaused;
    }
}
