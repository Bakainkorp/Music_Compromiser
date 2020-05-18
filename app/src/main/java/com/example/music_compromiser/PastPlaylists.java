package com.example.music_compromiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.music_compromiser.ui.login.MusicPlayer;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastPlaylists extends AppCompatActivity {

    RecyclerView mRecyclerview;

    private SharedPreferences mSharedPreferences;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_playlists);
        mSharedPreferences = getSharedPreferences("SPOTIFY", 0);
        mRequestQueue = Volley.newRequestQueue(PastPlaylists.this);

        mRecyclerview = (RecyclerView) findViewById(R.id.playList_recyclerview);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(PastPlaylists.this));

        try {
            getPastPlaylists();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPastPlaylists() throws JSONException {




        String username = getIntent().getStringExtra("username");
        String pastURL = "https://Bakainkorp.pythonanywhere.com/select" + "/" + username;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("User", username);

        CustomJsonRequest jsonObjectRequest = new CustomJsonRequest(Request.Method.POST, pastURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            for (int i=0; i < response.length(); i++){
                                JSONArray jsonArray1 = response.getJSONArray(i);
                                JSONObject jsonObject1 = new JSONObject();

                                jsonObject1.put("arrayName",jsonArray1);
                               String x = jsonObject1.getString("ServerID");
                               Log.d("Serveridfromdatabase", x);

                            }


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
                    Intent i = new Intent(PastPlaylists.this, PastPlaylistsMusicPlayer.class);
                    i.putExtra("playListId",playListId);
                    i.putExtra("endPoint",playListEndPoint);
                    startActivity(i);
                }
            });

            playListTitle = (TextView) itemView.findViewById(R.id.playlist_name);
            playListTracks = (TextView) itemView.findViewById(R.id.playlist_trackNumber);
        }

        public void onBind(PlayList playList) {
            playListId = playList.getPlayListURI();
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
