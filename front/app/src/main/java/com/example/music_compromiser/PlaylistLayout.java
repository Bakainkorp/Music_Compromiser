package com.example.music_compromiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.music_compromiser.R;
import com.example.music_compromiser.PlayList;
import com.example.music_compromiser.ui.login.MusicPlayer;
import com.google.gson.Gson;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;


import com.spotify.protocol.types.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;

public class PlaylistLayout extends AppCompatActivity {


    RecyclerView mRecyclerview;

    private SharedPreferences mSharedPreferences;
    private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlistlayout);
        mSharedPreferences = getSharedPreferences("SPOTIFY", 0);
        mRequestQueue = Volley.newRequestQueue(PlaylistLayout.this);

        mRecyclerview = (RecyclerView) findViewById(R.id.playList_recyclerview);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(PlaylistLayout.this));
        getUserPlayLists();


    }


    public void getUserPlayLists() {
        String endpoint = "https://api.spotify.com/v1/me/playlists";
        // String endpoint =  "https://api.spotify.com/v1/me/player/recently-played";


        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<PlayList> userPlayLists = new ArrayList<>();

                        //logging whole response
                        Log.d("sample", response.toString());
                        Gson gson = new Gson();

                        //breaking JsonArray into items
                        JSONArray jsonArray = response.optJSONArray("items");
                        for (int n = 0; n < jsonArray.length(); n++) {
                            try {

                                JSONObject object = jsonArray.getJSONObject(n);
                                PlayList tempPlayList = new PlayList();

                                tempPlayList.setPlayListId(object.getString("id"));

                                tempPlayList.setPlayListTitle(object.getString("name"));
                                tempPlayList.setPlayListEndPoint(object.getString("uri"));

                                Log.d("sample", object.getString("name"));


                                //second object within first
                                JSONObject object2 = object.optJSONObject("tracks");
                                tempPlayList.setNumberOfTracks(object2.getString("total"));

                                userPlayLists.add(tempPlayList);
                                Log.d("sample", Integer.toString(userPlayLists.size()) + " is the size");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mRecyclerview.setAdapter(new PlayListAdapter(userPlayLists));


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


    class playListViewHolder extends RecyclerView.ViewHolder {
        String playListId;
        String playListEndPoint;
        TextView playListTitle;
        TextView playListTracks;

        public playListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(PlaylistLayout.this, MusicPlayer.class);
                    i.putExtra("playListId",playListId);
                    i.putExtra("endPoint",playListEndPoint);
                    startActivity(i);
                }
            });

            playListTitle = (TextView) itemView.findViewById(R.id.playlist_name);
            playListTracks = (TextView) itemView.findViewById(R.id.playlist_trackNumber);
        }

        public void onBind(PlayList playList) {
            playListId = playList.getPlayListId();
            playListEndPoint = playList.getPlayListEndPoint();

            playListTitle.setText(playList.getPlayListTitle());
            playListTracks.setText(playList.getNumberOfTracks() + " Tracks");
        }

    }


    class PlayListAdapter extends RecyclerView.Adapter<playListViewHolder> {

        List<PlayList> listOfPlayList;

        public PlayListAdapter(List<PlayList> list) {
            listOfPlayList = new ArrayList<>(list);
        }

        @NonNull
        @Override
        public playListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.recyclerview_playlist_item, parent, false);
            return new playListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull playListViewHolder holder, int position) {
            holder.onBind(listOfPlayList.get(position));

        }

        @Override
        public int getItemCount() {
            return listOfPlayList.size();
        }
    }




}
