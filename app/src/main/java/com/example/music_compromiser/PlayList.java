package com.example.music_compromiser;


public class PlayList {
        private String PlayListEndPoint;
        private String PlayListTitle;
        private String PlayListId;
        private String numberOfTracks;
        private String imageUrl;


        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getPlayListEndPoint() {
            return PlayListEndPoint;
        }

        public void setPlayListEndPoint(String playListEndPoint) {
            this.PlayListEndPoint = playListEndPoint;
        }

        public String getPlayListTitle() {
            return PlayListTitle;
        }

        public String getPlayListId() {
            return PlayListId;
        }

        public void setPlayListId(String playListId) {
            PlayListId = playListId;
        }

        public void setPlayListTitle(String playListTitle) {
            this.PlayListTitle = playListTitle;
        }

        public String getNumberOfTracks() {
            return numberOfTracks;
        }

        public void setNumberOfTracks(String numberOfTracks) {
            this.numberOfTracks = numberOfTracks;
        }
}


