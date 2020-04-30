package com.example.music_compromiser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.music_compromiser.ui.login.MusicPlayer;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Qrcodelayout extends AppCompatActivity {

    private RequestQueue mRequestQueue;
    private SharedPreferences mSharedPreferences;
    private String username;
    private String userid;
    private String mServerid = ""; // the server id from the server for each user phone
    private String URL;
  //private String mServerURLHost = "https://192.168.1.3:5000/host";
    private String mServerURLHost = "https://benjaminlgur.pythonanywhere.com/host";
    private String mServerURLJoin;
    private Button mContinueButton;
    private String connection = "";
    private String otherphoneid; // the server id for other phone
    private FirebaseDatabase mFirebaseDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcodelayout);
        mSharedPreferences = getSharedPreferences("SPOTIFY", 0);
        mRequestQueue = Volley.newRequestQueue(Qrcodelayout.this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        userid = getIntent().getStringExtra("userid");

//         URL = "";
//
//        username = getIntent().getStringExtra("username");
//        userid = getIntent().getStringExtra("userid");
//
//        mRequestQueue = Volley.newRequestQueue(this);
//
//        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//            }
//
//        }, new Response.ErrorListener(){
//            @Override
//           public void onErrorResponse(VolleyError error){
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("user", username);
//                params.put("userid", userid);
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//
//        };
//        mRequestQueue.add(stringRequest);

        try {
            getServerID();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // after user that pressed join button scans qr code connect and go to
        // new activity ...

        mContinueButton = findViewById(R.id.continuebutton);

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if(connection.equals("True")){


                Map<String, Object> currentlyPlayingSong = new HashMap<>();
                currentlyPlayingSong.put("currentSong", "empty");
                currentlyPlayingSong.put("currentArtist", "empty");
                currentlyPlayingSong.put("currentImage", "empty");
                mFirebaseDatabase.getReference().child("currentlyPlayingSong").child(mServerid).setValue(currentlyPlayingSong);
                    Intent intent = new Intent(Qrcodelayout.this, MusicPlayer.class);
                    intent.putExtra("serverid", mServerid);
                    intent.putExtra("userid", userid);
                    intent.putExtra("username", username);
                    startActivity(intent);
              //  }
               // else{
                //    Toast.makeText(Qrcodelayout.this, "Connection Unsuccessful", Toast.LENGTH_LONG).show();
               // }
            }
        });





    }




    public void getServerID() throws JSONException {

        username = getIntent().getStringExtra("username");
        String topsongs = getIntent().getStringExtra("topsongs");
        JSONArray array = new JSONArray(topsongs);

//        Map<String, String> postParam= new HashMap<String, String>();
//        postParam.put("user", username);
//        postParam.put("data", array.toString());
       // new JSONObject(postParam)


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", username);
        jsonObject.put("data", array);


        mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, mServerURLHost, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //JSONObject jsonObject = response.optJSONObject("user");
                            // serverid = jsonObject.toString();
                            mServerid = response.getString("serverid");
                            Toast.makeText(Qrcodelayout.this,mServerid,Toast.LENGTH_LONG).show();


                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.encodeBitmap(mServerid, BarcodeFormat.QR_CODE, 800, 800);
                            ImageView imageViewQrCode = (ImageView) findViewById(R.id.qrCode);
                            imageViewQrCode.setImageBitmap(bitmap);

                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("voteToSkip", 0);
                            userInfo.put("voteForPrevious", 0);
                            userInfo.put("hasUserVoted","false");
                            mFirebaseDatabase.getReference().child("actionRequested").child(mServerid).child(userid).setValue(userInfo);


                        }catch (Exception e){
                            e.printStackTrace();
                        }

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


        mRequestQueue.add(jsonObjectRequest);
    }


    //check if user that pressed join button connects to phone
    public void userConnected(){
        String url = "";


        mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.optJSONObject("connectionworks");
                            connection = jsonObject.toString();


                        }catch (Exception e){

                        }

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
                    return headers;
                }


        };

        mRequestQueue.add(jsonObjectRequest);


    }


    //tell other user to continue to playlist layout
    public void signalOtherUser(){
        String url = "";
        mRequestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
              new Response.Listener<String>() {

            @Override
            public void onResponse(String response){
                try {

                }catch (Exception e){

                }

            }

        },
             new Response.ErrorListener()  {
                 @Override
                 public void onErrorResponse(VolleyError error) {

                 }

             })  {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Continue", "True");
                params.put("userid", otherphoneid);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = mSharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }


                };



        mRequestQueue.add(stringRequest);

    }



}
