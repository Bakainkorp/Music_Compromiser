package com.example.music_compromiser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.music_compromiser.ui.login.MusicPlayer;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoadingPage extends AppCompatActivity {
    private String mUserid = "empty";
    private String mUserName = "empty";
    private String mOtherPhoneServerid;
    private String mTopSongs;
    private RequestQueue mRequestQueue;
    private SharedPreferences mSharedPreferences;
    private FirebaseDatabase mfirebaseDatabase;
    private String success = "empty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);
        mSharedPreferences = getSharedPreferences("SPOTIFY", 0);
        mfirebaseDatabase = FirebaseDatabase.getInstance();

        mUserid = getIntent().getStringExtra("userid");
        mUserName = getIntent().getStringExtra("username");


        try {
            joinServer();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(LoadingPage.this, success, Toast.LENGTH_SHORT);



       // getResponse();

    }

    public void joinServer() throws JSONException {

        String joinURL = "https://benjaminlgur.pythonanywhere.com/join";
      //  String joinURL = "http://192.168.1.3:5000/join";

        mOtherPhoneServerid = getIntent().getStringExtra("userserverid");
        mTopSongs = getIntent().getStringExtra("data");
        JSONArray jsonArray = new JSONArray(mTopSongs);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", mUserName);
        jsonObject.put("serverid", mOtherPhoneServerid);
        jsonObject.put("data", jsonArray);
        Log.d("jsontoptracks", jsonArray.toString());

        Log.d("checkjson", jsonObject.toString());

        mRequestQueue = Volley.newRequestQueue(this);
        Log.d("Jertest1", "testing");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, joinURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            success = response.getString("success");
                            Log.d("success", success);
                            Toast.makeText(LoadingPage.this, success, Toast.LENGTH_SHORT).show();
                            //below goes to activity which displays everything the host sees without functionality

                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("voteToSkip", 0);
                            userInfo.put("voteForPrevious", 0);
                            userInfo.put("hasUserVoted","false");
                            mfirebaseDatabase.getReference().child("actionRequested").child(mOtherPhoneServerid).child(mUserid).setValue(userInfo);


                            Intent intent = new Intent(LoadingPage.this, MusicPlayerJoining.class);
                            intent.putExtra("serverid", mOtherPhoneServerid);
                            intent.putExtra("userid", mUserid);
                            startActivity(intent);



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


    public void getResponse(){
            String url = "";
            mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.optJSONObject("continue");
                            if(jsonObject.toString().equals("True")) {
                                Intent intent = new Intent(LoadingPage.this, PlaylistLayout.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(LoadingPage.this, "Error", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("sample", error.toString());
                    }
                })  {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = mSharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }


        };




    }
}
