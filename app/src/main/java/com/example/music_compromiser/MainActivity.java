package com.example.music_compromiser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.google.zxing.integration.*;



import com.spotify.protocol.client.Result;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.spotify.sdk.android.auth.AccountsQueryParameters.CLIENT_ID;


public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "c0380c5b6c1a454e98fab306bb0a01a2";
    private static final String REDIRECT_URI = "musiccompromiser://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private Button playlistbutton;
    private Button joinbutton;
    private Button hostbutton;
    private Button Pastplaylistsbutton;
    private RequestQueue mRequestQueue;
    private SharedPreferences mSharedPreferences;
    private String username;
    private String userid;
    private String userserverid;
    private RequestQueue mRequestQueue2;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = getSharedPreferences("SPOTIFY", 0);
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue2 = Volley.newRequestQueue(MainActivity.this);

        playlistbutton = findViewById(R.id.button4);
        joinbutton = findViewById(R.id.button2);
        hostbutton = findViewById(R.id.button);
        Pastplaylistsbutton = findViewById(R.id.button3);


        playlistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newintent = new Intent(MainActivity.this, PlaylistLayout.class);
                startActivity(newintent);
            }
        });


        joinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator joinIntent = new IntentIntegrator(MainActivity.this);
                joinIntent.setCameraId(0);
                joinIntent.setOrientationLocked(false);
                joinIntent.initiateScan();
            }

        });


        hostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrcodeIntent = new Intent(MainActivity.this, Qrcodelayout.class);
                qrcodeIntent.putExtra("username", username);
                qrcodeIntent.putExtra("userid", userid);
                startActivity(qrcodeIntent);
            }
        });

        //getUserName();
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //get id from the server from the other phone
 /*

                userserverid = result.getContents();
                Toast.makeText(MainActivity.this, "You are connected!", Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                Intent userIntent = new Intent(MainActivity.this, AcceptConnection.class);
                userIntent.putExtra("userserverid", userserverid);
                startActivity(userIntent);

                */


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getUserName(){
        String userDetails = "https://api.spotify.com/v1/me";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, userDetails, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject jsonObject = response.optJSONObject("display_name");
                        username = jsonObject.toString();
                        jsonObject = response.optJSONObject("id");
                        userid = jsonObject.toString();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log.d("Error.Response", response);
                        Log.d("sample", error.toString());

                    }
                }){   @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            String token = mSharedPreferences.getString("token", "");
            String auth = "Bearer " + token;
            headers.put("Authorization", auth);
            return headers;
        }
    };
        mRequestQueue2.add(jsonObjectRequest);


    }

// tell other phone that this phone was able to connect
    public void sendConnectionWorks(){
        String url = "";

       StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {

           }
       },
               new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {

                   }
               }) {
           @Override
           protected Map<String, String> getParams() {
               Map<String, String> params = new HashMap<String, String>();
               params.put("connectionworked", "True");

               return params;
           }

           @Override
           public Map<String, String> getHeaders() throws AuthFailureError {
               Map<String, String> params = new HashMap<String, String>();

               return params;
           }

       };

       mRequestQueue.add(stringRequest);


    }




}
