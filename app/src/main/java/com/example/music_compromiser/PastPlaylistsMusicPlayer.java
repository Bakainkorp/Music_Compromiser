package com.example.music_compromiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.music_compromiser.ui.login.MusicPlayer;
import com.example.music_compromiser.ui.login.Song;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Track;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastPlaylistsMusicPlayer extends AppCompatActivity {



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
    private FirebaseDatabase mFirebaseDataBase;
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
    Context context = PastPlaylistsMusicPlayer.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_playlists_music_player);
        mSharedPreferences = getSharedPreferences("SPOTIFY", 0);
        mRequestQueue = Volley.newRequestQueue(PastPlaylistsMusicPlayer.this);


        mRecyclerView = (RecyclerView) findViewById(R.id.past_song_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PastPlaylistsMusicPlayer.this));
        // mPlaylistId = getIntent().getStringExtra("playListId");
        //  mPlaylistURI = getIntent().getStringExtra("endPoint");


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
                        if (tracknum == mSongs.size() - 1) {
                            tracknum = 0;
                            wasplayed = false;
                            mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                        } else {
                            tracknum++;
                            wasplayed = false;
                            mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                        }
                        mPlayBtn.setImageResource(R.drawable.ic_pause_black_24dp);
            }
        });


        mPreviousTrackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tracknum == 0) {
                    tracknum = mSongs.size()-1;
                    wasplayed = false;
                    mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                } else {
                    tracknum--;
                    wasplayed = false;
                    mSpotifyAppRemote.getPlayerApi().play(mSongs.get(tracknum).getUri());
                }
                mPlayBtn.setImageResource(R.drawable.ic_pause_black_24dp);

            }
        });


    }



    public void getSongs() throws JSONException {


        String pastURL = "https://Bakainkorp.pythonanywhere.com/pastplay";

        String username = getIntent().getStringExtra("username");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Serverid", serverid);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, pastURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                              String x = response.getString("test");


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("errorjer", "error" );
                        Log.d("data", error.getCause().toString());
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


                        Toast.makeText(PastPlaylistsMusicPlayer.this, playList.getPlayListURI(), Toast.LENGTH_LONG);
                        //  mSpotifyAppRemote.getPlayerApi().play(playList.getPlayListURI());
                        Log.d("spotify uri", mSongs.get(position).getUri());
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



                        Toast.makeText(PastPlaylistsMusicPlayer.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
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
            if (name.length() > 30) {name = name.substring(0,27) + "...";}
            songTitle.setText(name);
            if (songArtistName.length() > 30) {songArtistName = songArtistName.substring(0,27) + "...";}
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
            View v = getLayoutInflater().inflate(R.layout.recyclerview_past_playlists_song_item, parent, false);
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

                        mSpotifyAppRemote.getImagesApi().getImage(track.imageUri, Image.Dimension.X_SMALL).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                            @Override
                            public void onResult(Bitmap bitmap) {
                                mImage.setImageBitmap(bitmap);

                            }
                        });

                        if(playerState.isPaused){
                            mPlayBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                            wasplayed = true;
                            paused = true;



                            if(playerState.playbackPosition == 0) {


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
                        }
                        else{
                            mPlayBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                            paused = false;



                            int x = 0;
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
