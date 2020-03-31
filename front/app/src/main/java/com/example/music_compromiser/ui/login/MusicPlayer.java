package com.example.music_compromiser.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.music_compromiser.R;
import com.google.gson.Gson;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicPlayer extends AppCompatActivity {

    //Spotify
    private static final String CLIENT_ID = "0fc19e947472492c930bef713d0d5482";
    private static final String REDIRECT_URI = "musiccompromiser://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private PlayerState mPlayerState;
    //PlayList


    //UI elements
    private ImageButton mPlayBtn;
    private ImageButton mNextTrackBtn;
    private ImageButton mPreviousTrackbtn;
    //Other
    private boolean isCurrentlyPlaying = false;

    private boolean wasplayed = false;

    RecyclerView mRecyclerView;

    private SharedPreferences mSharedPreferences;
    private RequestQueue mRequestQueue;
    private String mPlaylistId;
    private String mPlaylistURI;
    private String mSongUri;
    private String mUserID;
    ArrayList<Song> mSongs = new ArrayList<>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayer);
        mSharedPreferences = getSharedPreferences("SPOTIFY", 0);
        mRequestQueue = Volley.newRequestQueue(MusicPlayer.this);



        mRecyclerView = (RecyclerView) findViewById(R.id.song_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MusicPlayer.this));
        mPlaylistId = getIntent().getStringExtra("playListId");
        mPlaylistURI = getIntent().getStringExtra("endPoint");


        getUserSongs();
        mPlayBtn = (ImageButton) findViewById(R.id.play_button);


        mNextTrackBtn = (ImageButton) findViewById(R.id.next_btn);
        mPreviousTrackbtn = (ImageButton) findViewById(R.id.previous_btn);



    }

    public void getUserSongs(){
        String songEndPoint = "https://api.spotify.com/v1/playlists/" + mPlaylistId + "/tracks";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, songEndPoint, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                     //   ArrayList<Song> songs = new ArrayList<>();

                        //logging whole response
                        Log.d("sample", response.toString());
                        Gson gson = new Gson();

                        JSONArray jsonArray = response.optJSONArray("items");
                        for(int i=0; i < jsonArray.length(); i++){
                            try {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                Song song = new Song();
                                JSONObject jsonObject2 = jsonObject.optJSONObject("track");
                                song.setId(jsonObject2.getString("id"));
                                song.setUri(jsonObject2.getString("uri"));
                                song.setName(jsonObject2.getString("name"));

                               // Log.d("sample",+song.getUri());
                              //  Log.d("sample",song.getId());


                                mSongs.add(song);


                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        mRecyclerView.setAdapter(new songAdapter(mSongs));





                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log.d("Error.Response", response);
                        Log.d("sample", error.toString());

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = mSharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };

        // add it to the RequestQueue
        mRequestQueue.add(getRequest);




    }


    class SongViewHolder extends RecyclerView.ViewHolder{
        String songId;
        String songUri;
        TextView songTitle;
        TextView songArtist;
        int position;

        public SongViewHolder(@NonNull View itemView){
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    //      mSpotifyAppRemote.getPlayerApi().play(mSongs.get(position).getUri());


                   try {

                       if(!isCurrentlyPlaying) {
                           mSpotifyAppRemote.getPlayerApi().play(mPlaylistURI);
                           isCurrentlyPlaying = true;

                       }else if(mPlayerState.track.uri == mSongs.get(position).getUri()){
                            mSpotifyAppRemote.getPlayerApi().pause();
                            isCurrentlyPlaying = false;
                       }

                     // String x =  mPlayerState.track.name;
                       if(mPlayerState.track.uri != mSongs.get(position).getUri()) {
                           mSpotifyAppRemote.getPlayerApi().skipToIndex(mPlaylistURI, position);
                       }

                       Toast.makeText(MusicPlayer.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
                   }catch (Exception e){

                   }
                            i = i + 1;


                }
            });

            songTitle = (TextView) itemView.findViewById(R.id.song_name);
           // songArtist = (TextView) itemView.findViewById(R.id.playlist_songArtist);

        }

        public void onBind(Song songs) {
            songId = songs.getId();
            songUri = songs.getUri();
            String name = songs.getName();
          // name = name.substring(0).toUpperCase() + name.substring(1);
            songTitle.setText(name);
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
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });


        // Then we will write some more code here.

    }

    @Override
    protected void onStop() {
        super.onStop();

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        // and we will finish off here.
    }



    // make playlists for all users
    public void createPlaylist(){
        String URL = "https://api.spotify.com/v1/users/{user_id}/playlists";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String token = mSharedPreferences.getString("token", "");
                    String auth = "Bearer " + token;
                    headers.put("Authorization", auth);
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> Params = new HashMap<String, String>();
                        Params.put("name", "Music_Compromiser1");
                        return super.getParams();
                }
        };

          mRequestQueue.add(jsonObjectRequest);

    }



}
