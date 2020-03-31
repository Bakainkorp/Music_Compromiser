package com.example.music_compromiser;

import androidx.appcompat.app.AppCompatActivity;

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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Qrcodelayout extends AppCompatActivity {

    private RequestQueue mRequestQueue;
    private SharedPreferences mSharedPreferences;
    private String username;
    private String userid;
    private String mServerid = ""; // the server id from the server for each user phone
    private String URL;
    private String mServerURL = "https://benjaminlgur.pythonanywhere.com/serverid?serverid=1";
    private Button mContinueButton;
    private String connection = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcodelayout);
        mSharedPreferences = getSharedPreferences("SPOTIFY", 0);

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

        getServerID();


        // after user that pressed join button scans qr code connect and go to
        // new activity ...

        mContinueButton = findViewById(R.id.continuebutton);

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connection.equals("True")){
                    Intent intent = new Intent(Qrcodelayout.this, PlaylistLayout.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Qrcodelayout.this, "Connection Unsuccessful", Toast.LENGTH_LONG).show();
                }
            }
        });





    }




    public void getServerID(){

        mRequestQueue = Volley.newRequestQueue(this);
        StringRequest serverStringRequest = new StringRequest(Request.Method.POST, mServerURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            //JSONObject jsonObject = response.optJSONObject("user");
                           // serverid = jsonObject.toString();
                            mServerid =  response;

                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.encodeBitmap(mServerid, BarcodeFormat.QR_CODE, 800, 800);
                            ImageView imageViewQrCode = (ImageView) findViewById(R.id.qrCode);
                            imageViewQrCode.setImageBitmap(bitmap);


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


        Log.d("test", "getServerID: ");
        Log.d("serverid", mServerid);
        mRequestQueue.add(serverStringRequest);
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
                            JSONObject jsonObject = response.optJSONObject("connectionworked");
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




    }
}
