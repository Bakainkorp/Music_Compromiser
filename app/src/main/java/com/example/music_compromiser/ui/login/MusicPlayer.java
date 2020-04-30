package com.example.music_compromiser.ui.login;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.music_compromiser.CustomJsonRequest;
import com.example.music_compromiser.PlayList;
import com.example.music_compromiser.R;
import com.example.music_compromiser.VotingValues;
import com.example.music_compromiser.ui.login.Connectors.VolleyCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Result;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.LibraryState;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MusicPlayer extends AppCompatActivity {

            //Spotify
            private static final String CLIENT_ID = "";
            private static final String REDIRECT_URI = "musiccompromiser://callback";
            private SpotifyAppRemote mSpotifyAppRemote;
            private String serverid = "";
            private String userid;
            //PlayList


            //UI elements
            private ImageButton mPlayBtn;
            private ImageButton mNextTrackBtn;
            private ImageButton mPreviousTrackbtn;
            private TextView mCurrentArtist;
            private TextView mTextview;
            private ImageView mImage;
            //Other
            private boolean isCurrentlyPlaying = false;
            private  FirebaseDatabase mFirebaseDataBase;
            private boolean wasplayed = false;

            RecyclerView mRecyclerView;

            private SharedPreferences mSharedPreferences;
            private RequestQueue mRequestQueue;
            private String mPlaylistId = "empty";
            private String mPlaylistURI;
            PlayList playList = new PlayList();
            private String mSongUri;
           // private String mUserID;
            ArrayList<Song> mSongs = new ArrayList<>();
            int i = 0;
            boolean x = false;
            int tracknum = 0;
            boolean paused = true;
            Context context = MusicPlayer.this;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_musicplayer);
                mSharedPreferences = getSharedPreferences("SPOTIFY", 0);
                mRequestQueue = Volley.newRequestQueue(MusicPlayer.this);
                mFirebaseDataBase = FirebaseDatabase.getInstance();
                serverid = getIntent().getStringExtra("serverid");
                userid = getIntent().getStringExtra("userid");

                try {
                    getCombinedPlaylist();

                } catch (JSONException e) {
                    Log.d("onerrorresponse2", e.getCause().toString());
                    // Log.d("supererror", "erroror");
                    e.printStackTrace();
                }

        //        try {
        //            makePlaylist();
        //        } catch (JSONException e) {
        //            e.printStackTrace();
        //            Log.d("createplaylisterror", e.getCause().toString());
        //        }


                mRecyclerView = (RecyclerView) findViewById(R.id.song_recyclerview);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MusicPlayer.this));
                // mPlaylistId = getIntent().getStringExtra("playListId");
                //  mPlaylistURI = getIntent().getStringExtra("endPoint");


                mPlayBtn = (ImageButton) findViewById(R.id.playbuttonbottom);
                mTextview = findViewById(R.id.textviewbottom);
                mCurrentArtist = findViewById(R.id.currentArtistBottom);
                mNextTrackBtn = (ImageButton) findViewById(R.id.skipnext);
                mPreviousTrackbtn = (ImageButton) findViewById(R.id.skiprevious);
                mImage = findViewById(R.id.playerimage);


                // mNextTrackBtn = (ImageButton) findViewById(R.id.next_btn);
                // mPreviousTrackbtn = (ImageButton) findViewById(R.id.previous_btn);


                mPlayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (paused) {

                            if (!wasplayed) {
                                mPlayBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                                // mSpotifyAppRemote.getPlayerApi().play(playList.getPlayListURI());
                                mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                                isCurrentlyPlaying = true;
                                wasplayed = true;
                            } else {
                                mPlayBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                                mSpotifyAppRemote.getPlayerApi().resume();
                                isCurrentlyPlaying = true;
                                paused = false;
                            }


                        } else {
                            mPlayBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                            mSpotifyAppRemote.getPlayerApi().pause();
                            isCurrentlyPlaying = false;
                            paused = true;
                        }
                    }
                });

                mNextTrackBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // mSpotifyAppRemote.getPlayerApi().skipNext();

                        Map<String, Object> voteToSkip = new HashMap<>();
                        voteToSkip.put("voteToSkip", 1);
                        //if this deletes other child then use merger option
                        mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(voteToSkip);



                    /*

                        if (tracknum == mSongs.size() - 1) {
                            tracknum = 0;
                            wasplayed = false;
                            mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                        } else {
                            tracknum++;
                            wasplayed = false;
                            mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                        }
                        mPlayBtn.setImageResource(R.drawable.ic_pause_black_24dp);*/
                    }
                });

                mPreviousTrackbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> voteToSkip = new HashMap<>();
                        voteToSkip.put("voteForPrevious", 1);
                        //if this deletes other child then use merger option
                       mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(voteToSkip);
                    }
                });


                mFirebaseDataBase.getReference().child("actionRequested").child(serverid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            long totalVoteToSkip = 0;
                            long totalVoteForPrevious = 0;
                            long userCount = dataSnapshot.getChildrenCount();

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(snapshot.hasChild("voteToSkip")){
                                    totalVoteToSkip += (long) snapshot.child("voteToSkip").getValue();
                                }

                                if(snapshot.hasChild("voteForPrevious")){
                                    totalVoteForPrevious += (long) snapshot.child("voteForPrevious").getValue();
                                }

                            }

                            Log.d("totalvotetoskip", String.valueOf(totalVoteToSkip));
                            Log.d("totalvotetoskipprev", String.valueOf(totalVoteForPrevious));

                            if(totalVoteToSkip > Math.floor(userCount/2)){
                                if (tracknum == mSongs.size() - 1) {
                                    tracknum = 0;
                                    wasplayed = false;
                                    mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                                    Map<String, Object> voteForPrevious = new HashMap<>();
                                    voteForPrevious.put("voteForPrevious", 0);
                                    //if this deletes other child then use merger option
                                    mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(voteForPrevious);
                                    Map<String, Object> voteToSkip = new HashMap<>();
                                    voteToSkip.put("voteToSkip", 0);
                                    //if this deletes other child then use merger option
                                    mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(voteToSkip);
                                } else {
                                    tracknum++;
                                    wasplayed = false;
                                    mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                                    Map<String, Object> voteForPrevious = new HashMap<>();
                                    voteForPrevious.put("voteForPrevious", 0);
                                    //if this deletes other child then use merger option
                                    mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(voteForPrevious);
                                    Map<String, Object> voteToSkip = new HashMap<>();
                                    voteToSkip.put("voteToSkip", 0);
                                    //if this deletes other child then use merger option
                                    mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(voteToSkip);
                                }

                            }
                            else if(totalVoteForPrevious > Math.floor(userCount/2)){


                                if (tracknum == 0) {
                                    tracknum = mSongs.size()-1;
                                    wasplayed = false;
                                    mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                                } else {
                                    tracknum--;
                                    wasplayed = false;
                                    mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                                }

                            }



                            }


                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }



                });







            }

            public void firebaseUpdate(){


                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("testing");
                //myRef.updateChildren()



            }

            public void getCombinedPlaylist() throws JSONException {

                String combinedURL = "http://benjaminlgur.pythonanywhere.com/start";
               // String combinedURL = "http://192.168.1.3:5000/start";
                serverid = getIntent().getStringExtra("serverid");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("serverid", serverid);
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                Log.d( "serverid: ", serverid);
                Log.d("supererror", "erroror");


                CustomJsonRequest jsonArrayRequest = new CustomJsonRequest(Request.Method.POST, combinedURL, jsonObject,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try{

                                for (int i = 0; i < response.length(); i++) {


                                    Log.d("alltracks", "response" + response.getJSONArray(i).toString());

                                    JSONArray jsonArray1 = response.getJSONArray(i);

                                    int s = Integer.parseInt(jsonArray1.getString(0));
                                    Song song = new Song();
                                    song.setNumOfOccurences(s);
                                    String d = jsonArray1.getString(1);
                                    song.setUri(d);
                                    String songname = jsonArray1.getString(2);
                                    song.setName(songname);
                                    String artist = jsonArray1.getJSONArray(3).getString(0);
                                    song.setArtist(artist);
                                    String songowner = jsonArray1.getJSONArray(4).getString(0);
                                    song.setSongOwner(songowner);

                                    Map<String,Object> songsMap = new HashMap<>();
                                    songsMap.put("songName",songname);
                                    songsMap.put("artist",artist);
                                    songsMap.put("songOwner",songowner);
                                    String key =  mFirebaseDataBase.getReference().child("sessionInfo").child(serverid).child("songs").push().getKey();

                                    mFirebaseDataBase.getReference().child("sessionInfo").child(serverid).child("songs").child(key).setValue(songsMap);

                                      mSongs.add(song);


                                    //song.setUri(jsonObject.optString(j));


                                }

                            }catch(Exception e){
                                 e.printStackTrace();

                            }
                                mRecyclerView.setAdapter(new songAdapter(mSongs));


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("onerrorresponse", error.getCause().toString());

                            }
                        }){


                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        String token = mSharedPreferences.getString("token", "");
                        String auth = "Bearer " + token;
                        headers.put("Authorization", auth);
                        return headers;
                    }


                };



                mRequestQueue.add(jsonArrayRequest);




            }

            public void makePlaylist() throws JSONException {


                userid = getIntent().getStringExtra("userid");

                String makePlaylistURL = "https://api.spotify.com/v1/users/" + userid + "/playlists";

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", "Music Compromiser");


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, makePlaylistURL, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                              String json;
                                try {
                                    mPlaylistURI = response.getString("uri");
                                    mPlaylistId = response.getString("id");
                                    playList.setPlayListURI(response.getString("uri"));







                                    String allsongs = "uris=";

                                    String addSongsToPlaylistURL = "https://api.spotify.com/v1/playlists/" + mPlaylistId + "/tracks";

                                    for(int i=0; i< mSongs.size(); i++){
                                        if(i==mSongs.size()-1){
                                            allsongs = allsongs + mSongs.get(i).getUri();
                                        }
                                        else {
                                            allsongs = allsongs + mSongs.get(i).getUri() + ",";
                                        }
                                    }

                                    Log.d("allsongsstring", allsongs);

                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("uris", allsongs);
                                    JSONArray jsonArray = new JSONArray();
                                    jsonArray.put(jsonObject);

                                    Log.d("thissidhishids", mPlaylistId);
                                    JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, addSongsToPlaylistURL+"?"+allsongs, null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {

        //                                            String json;
        //                                            try {
        //                                              //  mPlaylistURI = response.getString("uri");
        //
        //                                            } catch (JSONException e) {
        //                                                e.printStackTrace();
        //                                                Log.d("addingsongserror", e.getCause().toString());
        //                                            }


                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {


                                                }
                                            }){


                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> headers = new HashMap<>();
                                            String token = mSharedPreferences.getString("token", "");
                                            String auth = "Bearer " + token;
                                            headers.put("Authorization", auth);
                                            headers.put("Content-Type", "application/json");

                                            return headers;
                                        }




                                    };
                                    mRequestQueue.add(jsonObjectRequest1);






                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                              //  Log.d("onerrorresponse", error.getMessage().toString());


                            }
                        }){


                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        String token = mSharedPreferences.getString("token", "");
                        String auth = "Bearer " + token;
                        headers.put("Authorization", auth);
                        headers.put("Content-Type", "application/json");

                        return headers;
                    }




                };



                mRequestQueue.add(jsonObjectRequest);




            }


            class SongViewHolder extends RecyclerView.ViewHolder{
                String songId;
                String songUri;
                String songownername;
                TextView songTitle;
                TextView songArtist;
                TextView songOwner;
                int position;

                public SongViewHolder(@NonNull View itemView){
                    super(itemView);
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            //      mSpotifyAppRemote.getPlayerApi().play(mSongs.get(position).getUri());


                           try {


                                   Toast.makeText(MusicPlayer.this, playList.getPlayListURI(), Toast.LENGTH_LONG);
                                 //  mSpotifyAppRemote.getPlayerApi().play(playList.getPlayListURI());
                                   mSpotifyAppRemote.getPlayerApi().play(mSongs.get(position).getUri());
                                    wasplayed = false;
                                    tracknum = position;

                                   isCurrentlyPlaying = true;

        //                       }else if(mPlayerState.track.uri == mSongs.get(position).getUri()){
        //                            mSpotifyAppRemote.getPlayerApi().pause();
        //                            isCurrentlyPlaying = false;
        //                       }

                                   // String x =  mPlayerState.track.name;

                                 //  mSpotifyAppRemote.getPlayerApi().skipToIndex(playList.getPlayListURI(), position);



                               Toast.makeText(MusicPlayer.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
                           }catch (Exception e){

                           }
                                    i = i + 1;


                        }
                    });

                    songTitle = (TextView) itemView.findViewById(R.id.song_name);
                    songArtist = (TextView) itemView.findViewById(R.id.playlist_songArtist);
                    songOwner = (TextView) itemView.findViewById(R.id.nameofsongowner);

                }

                public void onBind(Song songs) {
                    songId = songs.getId();
                    songUri = songs.getUri();
                    songownername = songs.getSongOwner();
                   String songArtistName = songs.getArtist();
                    String name = songs.getName();
                  // name = name.substring(0).toUpperCase() + name.substring(1);
                    songTitle.setText(name);
                    songArtist.setText(songArtistName);
                    songOwner.setText("user: " + songownername);
                    position  = getAdapterPosition();

        //            songTitle.setText(songs.getName());  // null pointer
                   // songArtist.setText(songs.getArtist());
                }


            }


            class songAdapter extends  RecyclerView.Adapter<SongViewHolder> {
                List<Song> listOfSongs;

                public songAdapter(List<Song> list) {
                    listOfSongs = new ArrayList<>(list);
                }

                @NonNull
                @Override
                public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = getLayoutInflater().inflate(R.layout.recyclerview_song_item, parent, false);
                    return new SongViewHolder(v);
                }

                @Override
                public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
                    holder.onBind(listOfSongs.get(position));

                }

                @Override
                public int getItemCount() {
                    return listOfSongs.size();
                }


            }


            @Override
            protected void onStart() {
                super.onStart();

                ConnectionParams connectionParams =
                        new ConnectionParams.Builder(CLIENT_ID)
                                .setRedirectUri(REDIRECT_URI)
                                .showAuthView(true)
                                .build();

                SpotifyAppRemote.connect(this, connectionParams,
                        new Connector.ConnectionListener() {

                            @Override
                            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                                mSpotifyAppRemote = spotifyAppRemote;
                                Log.d("sample", "Connected! Yay!");

                                // Now you can start interacting with App Remote
                                connected();
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.e("sample", throwable.getMessage(), throwable);

                                // Something went wrong when attempting to connect! Handle errors here
                            }
                        });



            }

            private void connected() {


                // Subscribe to PlayerState
                mSpotifyAppRemote.getPlayerApi()
                        .subscribeToPlayerState()
                        .setEventCallback(playerState -> {
                            final Track track = playerState.track;

                            if (track != null) {
                                String current = track.name;
                               // Log.d("duration", "values " + playerState.playbackPosition + " , " + track.duration);
                                if (current.length() > 30) {current = current.substring(0,27) + "...";}
                                mTextview.setText(current);
                                mCurrentArtist.setText(track.artist.name);

                                Map<String, Object> currentlyPlayingSong = new HashMap<>();
                                currentlyPlayingSong.put("currentSong", current);
                                currentlyPlayingSong.put("currentArtist", track.artist.name);
                                mFirebaseDataBase.getReference().child("currentlyPlayingSong").child(serverid).updateChildren(currentlyPlayingSong);

                                mSpotifyAppRemote.getImagesApi().getImage(track.imageUri, Image.Dimension.X_SMALL).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                    @Override
                                    public void onResult(Bitmap bitmap) {
                                        mImage.setImageBitmap(bitmap);

                                        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
                                        if (bitmap != null && !bitmap.isRecycled()) {
                                           // bitmap.recycle(); not having this is very inefficient
                                            //will try to fix later but having bitmap.recycle causes
                                            //errors
                                            bitmap = null;
                                        }

                                        byte[] byteArray = bYtE.toByteArray();
                                        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                                        Map<String, Object> currentImage = new HashMap<>();
                                        currentImage.put("currentImage", encodedImage);
                                        mFirebaseDataBase.getReference().child("currentlyPlayingSong").child(serverid).updateChildren(currentImage);

                                    }
                                });

                                if(playerState.isPaused){
                                    mPlayBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                                    wasplayed = true;
                                    paused = true;

                                    Map<String,Object> votingValues = new HashMap<>();
                                    votingValues.put("isPaused","true");
                                     FirebaseDatabase.getInstance().getReference().child("isPlayerPaused").child(serverid).setValue(votingValues);
                                   // VotingValues votingValues = new VotingValues();
                                  //  votingValues.setIsPaused("true");
                                  //  myRef.setValue(votingValues);

                                }
                                else{
                                    mPlayBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                                    paused = false;
                                    Map<String,Object> votingValues = new HashMap<>();
                                    votingValues.put("isPaused","false");
                                    FirebaseDatabase.getInstance().getReference().child("isPlayerPaused").child(serverid).setValue(votingValues);
                                    /*VotingValues votingValues1 = new VotingValues();
                                    votingValues1.setIsPaused("false");
                                    myRef1.setValue(votingValues1);*/

                                }

                                if(playerState.playbackPosition == track.duration) {

                                    if (tracknum == mSongs.size() - 1) {
                                        tracknum = 0;
                                        mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                                        wasplayed = false;
                                    } else {
                                        tracknum++;
                                        mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                                        wasplayed = false;
                                    }
                                }

                                //Log.d("MainActivity", track.name + " by " + track.artist.name);
                            }
                        });











        //        CallResult<PlayerState> playerStateCall = mSpotifyAppRemote.getPlayerApi().getPlayerState();
        //        Result<PlayerState> playerStateResult = playerStateCall.await(10, TimeUnit.SECONDS);
        //        if (playerStateResult.isSuccessful()) {
        //            PlayerState playerState = playerStateResult.getData();
        //            if(playerState.playbackPosition == playerState.track.duration){
        //                if(tracknum == mSongs.size()-1){
        //                    tracknum = 0;
        //                    mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
        //                }
        //                else{
        //                    tracknum++;
        //                    mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
        //                }
        //            }
        //            // have some fun with playerState
        //        } else {
        //            Throwable error = playerStateResult.getError();
        //            // try to have some fun with the error
        //        }




                // Then we will write some more code here.

            }

            @Override
            protected void onStop() {
                super.onStop();
                mSpotifyAppRemote.getPlayerApi().pause();
                SpotifyAppRemote.disconnect(mSpotifyAppRemote);

                // and we will finish off here.
            }







}
