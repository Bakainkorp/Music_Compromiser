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
import android.graphics.Color;
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
import com.spotify.protocol.client.Subscription;
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
            private static final String CLIENT_ID = "0fc19e947472492c930bef713d0d5482";
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
            private TextView mNextCountVote;
            private TextView mPreviousCountVote;

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

                mRecyclerView = (RecyclerView) findViewById(R.id.song_recyclerview);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MusicPlayer.this));

                mPlayBtn = (ImageButton) findViewById(R.id.playbuttonbottom);
                mTextview = findViewById(R.id.textviewbottom);
                mCurrentArtist = findViewById(R.id.currentArtistBottom);
                mNextTrackBtn = (ImageButton) findViewById(R.id.skipnext);
                mPreviousTrackbtn = (ImageButton) findViewById(R.id.skiprevious);
                mImage = findViewById(R.id.playerimage);
                mNextCountVote = findViewById(R.id.ID_nextCountVote);
                mPreviousCountVote = findViewById(R.id.ID_previousCount);



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


                        // check tomorrow
                        mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("voteToSkip")){
                                   long x = (long) dataSnapshot.child("voteToSkip").getValue();
                                   if(x == 1){
                                       Map<String, Object> changevoteToSkip = new HashMap<>();
                                       changevoteToSkip.put("voteToSkip", 0);
                                       mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(changevoteToSkip);
                                       mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                   }
                                   else if(x == 0){
                                       Map<String, Object> changevoteToSkip = new HashMap<>();
                                       changevoteToSkip.put("voteToSkip", 1);
                                       mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(changevoteToSkip);
                                       mNextTrackBtn.setBackgroundColor(Color.parseColor("#FF0000"));

                                   }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                         // original if top doesnt work change back to this
//                        Map<String, Object> voteToSkip = new HashMap<>();
//                        voteToSkip.put("voteToSkip", 1);
//                        //if this deletes other child then use merger option
//                        mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(voteToSkip);
//                        mNextTrackBtn.setBackgroundColor(Color.parseColor("#FF0000"));



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

                        mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("voteForPrevious")){
                                    long x = (long) dataSnapshot.child("voteForPrevious").getValue();
                                    if(x == 1){
                                        Map<String, Object> changevoteToSkip = new HashMap<>();
                                        changevoteToSkip.put("voteForPrevious", 0);
                                        mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(changevoteToSkip);
                                        mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                    }
                                    else if(x == 0){
                                        Map<String, Object> changevoteToSkip = new HashMap<>();
                                        changevoteToSkip.put("voteForPrevious", 1);
                                        mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(changevoteToSkip);
                                        mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#FF0000"));

                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });





//                        Map<String, Object> voteToSkip = new HashMap<>();
//                        voteToSkip.put("voteForPrevious", 1);
//                        //if this deletes other child then use merger option
//                       mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(voteToSkip);
//                        mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#FF0000"));
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

                            mPreviousCountVote.setText(String.valueOf(totalVoteForPrevious) + '/' + String.valueOf((int)Math.floor(userCount/2) + 1));
                            mNextCountVote.setText(String.valueOf(totalVoteToSkip) + '/' + String.valueOf((int)Math.floor(userCount/2) + 1));

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
                                    mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                    mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));

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
                                    mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                    mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                }

                            }
                            else if(totalVoteForPrevious > Math.floor(userCount/2)){


                                if (tracknum == 0) {
                                    tracknum = mSongs.size()-1;
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
                                    mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                    mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                } else {
                                    tracknum--;
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
                                    mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                    mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                }

                            }



                            }


                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }



                });


            }



            public void getCombinedPlaylist() throws JSONException {

                String combinedURL = "https://Bakainkorp.pythonanywhere.com/start";
               // String combinedURL = "https://benjamin/gur.pythonanywhere.com/start";
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
                                    Log.d("song " + i, ", " + d);
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
                               Log.d("spotify uri", mSongs.get(position).getUri());
                                   mSpotifyAppRemote.getPlayerApi().play(mSongs.get(position).getUri());
                                    wasplayed = false;
                                    tracknum = position;

                                   isCurrentlyPlaying = true;
                               mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                               mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));

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
                    if (name.length() > 25) {name = name.substring(0,22) + "...";}
                    songTitle.setText(name);
                    if (songArtistName.length() > 25) {songArtistName = songArtistName.substring(0,22) + "...";}
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
                                if (current.length() > 12) {current = current.substring(0,9) + "...";}
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



                                    if(playerState.playbackPosition == 0) {

                                        Map<String, Object> voteForPrevious = new HashMap<>();
                                        voteForPrevious.put("voteForPrevious", 0);
                                        //if this deletes other child then use merger option
                                        mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(voteForPrevious);
                                        Map<String, Object> voteToSkip = new HashMap<>();
                                        voteToSkip.put("voteToSkip", 0);
                                        //if this deletes other child then use merger option
                                        mFirebaseDataBase.getReference().child("actionRequested").child(serverid).child(userid).updateChildren(voteToSkip);
                                        mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                        mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));

                                        if (tracknum == mSongs.size() - 1) {


                                            tracknum = 0;
                                            mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                                            wasplayed = false;
                                            mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                            mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                        } else {
                                            tracknum++;
                                            mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                                            wasplayed = false;
                                            mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                            mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));
                                        }
                                    }





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



                                     int x = 0;


                                    Map<String,Object> votingValues = new HashMap<>();
                                    votingValues.put("isPaused","false");
                                    FirebaseDatabase.getInstance().getReference().child("isPlayerPaused").child(serverid).setValue(votingValues);
                                    /*VotingValues votingValues1 = new VotingValues();
                                    votingValues1.setIsPaused("false");
                                    myRef1.setValue(votingValues1);*/

                                }


                                //Log.d("MainActivity", track.name + " by " + track.artist.name);
                            }
                        });


            }

            @Override
            protected void onStop() {
                super.onStop();
                mSpotifyAppRemote.getPlayerApi().pause();
                SpotifyAppRemote.disconnect(mSpotifyAppRemote);

                // and we will finish off here.
            }







}
