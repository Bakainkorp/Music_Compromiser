package com.example.music_compromiser.ui.login;

import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.Volley;
import com.example.music_compromiser.MainActivity;
import com.example.music_compromiser.R;
//import com.example.music_compromiser.SongService;
import com.example.music_compromiser.ui.login.Connectors.UserService;
import com.example.music_compromiser.ui.login.LoginViewModel;
import com.example.music_compromiser.ui.login.LoginViewModelFactory;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.error.AuthenticationFailedException;
import com.spotify.sdk.android.auth.app.SpotifyAuthHandler;
import com.spotify.*;
import com.android.volley.RequestQueue;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "musiccompromiser://callback";
    private static final String SCOPES = "streaming,user-read-currently-playing,user-read-playback-position,app-remote-control,user-read-recently-played,user-library-modify,user-read-email,user-read-private,playlist-read-collaborative,playlist-read-private,user-top-read,playlist-modify,playlist-modify-public,playlist-modify-private,user-library-modify";
    private static final String CLIENT_ID = "0fc19e947472492c930bef713d0d5482";



    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private Call mCall;
    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;


    private RequestQueue queue;




    private void authenticateSpotify() {
                AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
                builder.setScopes(new String[]{SCOPES});
                AuthorizationRequest request = builder.build();
                AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().setTitle(String.format(
                Locale.US, "Spotify Auth Sample %s", com.spotify.sdk.android.auth.BuildConfig.VERSION_NAME));
        setContentView(R.layout.activity_login2);
        authenticateSpotify();
        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    Log.d("STARTING", "GOT AUTH TOKEN");
                    editor.apply();
                    waitForUserInfo();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }


    private void waitForUserInfo() {
        UserService userService = new UserService(queue, msharedPreferences);
        userService.get(() -> {
            User user = userService.getUser();
            editor = getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.id);
            Log.d("STARTING", "GOT USER INFORMATION");
            // We use commit instead of apply because we need the information stored immediately
            editor.commit();
            startMainActivity();
        });
    }

    private void startMainActivity() {
        Intent newintent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(newintent);

    }




    final Request request = new Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer " + mAccessToken)
            .build();

}
