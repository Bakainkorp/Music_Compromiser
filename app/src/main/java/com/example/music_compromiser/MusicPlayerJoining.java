package com.example.music_compromiser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.music_compromiser.ui.login.Song;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MusicPlayerJoining extends AppCompatActivity {

    //UI elements
    private ImageButton mPlayBtn;
    private ImageButton mNextTrackBtn;
    private ImageButton mPreviousTrackbtn;
    private TextView mCurrentArtist;
    private TextView mTextview;
    private ImageView mImage;
    private TextView mNextVoteCount;
    private TextView mPreviousVoteCount;
    //Other
    RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseBaseDatabase;
    private SongAdapter mSongAdapter;

    private SharedPreferences mSharedPreferences;
    private RequestQueue mRequestQueue;
    private String mPlaylistId = "empty";
    private String mPlaylistURI;
    PlayList playList = new PlayList();
    ArrayList<Song> mSongs = new ArrayList<>();
    boolean paused = true;
    Context context = MusicPlayerJoining.this;
    private String mServerID;
    private String mUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player_joining);
        mFirebaseBaseDatabase = FirebaseDatabase.getInstance();
        mSharedPreferences = getSharedPreferences("SPOTIFY", 0);
        mRequestQueue = Volley.newRequestQueue(MusicPlayerJoining.this);


        mRecyclerView = (RecyclerView) findViewById(R.id.song_recyclerviewjoin);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MusicPlayerJoining.this));
        mServerID = getIntent().getStringExtra("serverid");
        mUserID = getIntent().getStringExtra("userid");
        // mPlaylistId = getIntent().getStringExtra("playListId");
        //  mPlaylistURI = getIntent().getStringExtra("endPoint");


        mPlayBtn = (ImageButton) findViewById(R.id.playbuttonbottomjoin);
        mTextview = findViewById(R.id.textviewbottomjoin);
        mCurrentArtist = findViewById(R.id.currentArtistBottomjoin);
        mNextTrackBtn = (ImageButton) findViewById(R.id.skipnextjoin);
        mPreviousTrackbtn = (ImageButton) findViewById(R.id.skipreviousjoin);
        mImage = findViewById(R.id.playerimagejoin);
        mNextVoteCount = findViewById(R.id.ID_nextCountVote);
        mPreviousVoteCount = findViewById(R.id.ID_previousCount);


        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mNextTrackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("voteToSkip")){
                            long x = (long) dataSnapshot.child("voteToSkip").getValue();
                            if(x == 1){
                                Map<String, Object> changevoteToSkip = new HashMap<>();
                                changevoteToSkip.put("voteToSkip", 0);
                                mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).updateChildren(changevoteToSkip);
                                mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));
                            }
                            else if(x == 0){
                                Map<String, Object> changevoteToSkip = new HashMap<>();
                                changevoteToSkip.put("voteToSkip", 1);
                                mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).updateChildren(changevoteToSkip);
                                mNextTrackBtn.setBackgroundColor(Color.parseColor("#FF0000"));

                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



//                Map<String, Object> voteToSkip = new HashMap<>();
//                voteToSkip.put("voteToSkip", 1);
//                //if this deletes other child then use merger option
//                mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).updateChildren(voteToSkip);
//                mNextTrackBtn.setBackgroundColor(Color.parseColor("#FF0000"));


            }
        });

        mPreviousTrackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("voteForPrevious")){
                            long x = (long) dataSnapshot.child("voteForPrevious").getValue();
                            if(x == 1){
                                Map<String, Object> changevoteToSkip = new HashMap<>();
                                changevoteToSkip.put("voteForPrevious", 0);
                                mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).updateChildren(changevoteToSkip);
                                mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                            }
                            else if(x == 0){
                                Map<String, Object> changevoteToSkip = new HashMap<>();
                                changevoteToSkip.put("voteForPrevious", 1);
                                mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).updateChildren(changevoteToSkip);
                                mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#FF0000"));

                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






//                Map<String, Object> voteToSkip = new HashMap<>();
//                voteToSkip.put("voteForPrevious", 1);
//                //if this deletes other child then use merger option
//                mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).updateChildren(voteToSkip);
//                mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#FF0000"));


            }
        });


        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        // DatabaseReference isPausedRef = database.getReference("test");
        mFirebaseBaseDatabase.getInstance().getReference("isPlayerPaused").child(mServerID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Toast.makeText(MusicPlayerJoining.this, "isPaused IS HERE", Toast.LENGTH_LONG);
                    if (dataSnapshot.hasChild("isPaused")) {
                        String isPausedValue = dataSnapshot.child("isPaused").getValue().toString();

                        if (isPausedValue.contains("true")) {
                            mPlayBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        } else {
                            //player is NOT paused
                            mPlayBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                        }

                    } else {
                        Toast.makeText(MusicPlayerJoining.this, "isPaused Not HERE", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mFirebaseBaseDatabase.getReference().child("sessionInfo").child(mServerID).child("songs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(MusicPlayerJoining.this,"SessionInfo_ok",Toast.LENGTH_LONG).show();
                    ArrayList<Song> tempSongs = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Song song = new Song();
                        if (snapshot.hasChild("artist")) {
                            song.setArtist(snapshot.child("artist").getValue().toString());
                        }

                        if(snapshot.hasChild("songName")){
                            song.setName(snapshot.child("songName").getValue().toString());
                        }

                        if(snapshot.hasChild("songOwner")){
                            song.setSongOwner(snapshot.child("songOwner").getValue().toString());
                        }

                        tempSongs.add(song);
                    }
                    mSongAdapter = new SongAdapter(tempSongs);
                    mRecyclerView.setAdapter(mSongAdapter);
                }else {
                    Toast.makeText(MusicPlayerJoining.this,"SessionInfo_NOT Found",Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).addValueEventListener(new ValueEventListener() {
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

                    mPreviousVoteCount.setText(String.valueOf(totalVoteForPrevious) + '/' + String.valueOf((int)Math.floor(userCount/2) + 1));
                    mNextVoteCount.setText(String.valueOf(totalVoteToSkip) + '/' + String.valueOf((int)Math.floor(userCount/2) + 1));

                    Log.d("totalvotetoskip", String.valueOf(totalVoteToSkip));
                    Log.d("totalvotetoskipprev", String.valueOf(totalVoteForPrevious));




                    if(totalVoteToSkip > Math.floor(userCount/2) || totalVoteForPrevious > Math.floor(userCount/2)){



                            Map<String, Object> voteForPrevious = new HashMap<>();
                            voteForPrevious.put("voteForPrevious", 0);
                            //if this deletes other child then use merger option
                            mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).updateChildren(voteForPrevious);
                            Map<String, Object> voteToSkip = new HashMap<>();
                            voteToSkip.put("voteToSkip", 0);
                            //if this deletes other child then use merger option
                            mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).updateChildren(voteToSkip);
                        mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                        mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mFirebaseBaseDatabase.getReference("currentlyPlayingSong").child(mServerID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d("currentartist", dataSnapshot.child("currentSong").getValue().toString());
                    mCurrentArtist.setText(dataSnapshot.child("currentArtist").getValue().toString());
                    mTextview.setText(dataSnapshot.child("currentSong").getValue().toString());
                    if(!dataSnapshot.child("currentImage").getValue().toString().equals("empty")) {
                        byte[] decodedString = Base64.decode(dataSnapshot.child("currentImage").getValue().toString(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        mImage.setImageBitmap((decodedByte));
                    }
                    Map<String, Object> voteForPrevious = new HashMap<>();
                    voteForPrevious.put("voteForPrevious", 0);
                    //if this deletes other child then use merger option
                    mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).updateChildren(voteForPrevious);

                    Map<String, Object> voteToSkip = new HashMap<>();
                    voteToSkip.put("voteToSkip", 0);
                    //if this deletes other child then use merger option
                    mFirebaseBaseDatabase.getReference().child("actionRequested").child(mServerID).child(mUserID).updateChildren(voteToSkip);
                    mPreviousTrackbtn.setBackgroundColor(Color.parseColor("#000000FF"));
                    mNextTrackBtn.setBackgroundColor(Color.parseColor("#000000FF"));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }


    class SongViewHolder extends RecyclerView.ViewHolder {
        //  String songId;
        // String songUri;
        String songownername;
        TextView songTitle;
        TextView songArtist;
        TextView songOwner;
        int position;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);


          songTitle = (TextView) itemView.findViewById(R.id.song_name);
            songArtist = (TextView) itemView.findViewById(R.id.playlist_songArtist);
            songOwner = (TextView) itemView.findViewById(R.id.nameofsongowner);

        }

        public void onBind(Song songs) {

            songownername = songs.getSongOwner();
            String songArtistName = songs.getArtist();
            String name = songs.getName();
            // name = name.substring(0).toUpperCase() + name.substring(1);
            if (name.length() > 25) {name = name.substring(0,22) + "...";}
            songTitle.setText(name);
            if (songArtistName.length() > 25) {songArtistName = songArtistName.substring(0,22) + "...";}
            songArtist.setText(songArtistName);
            songOwner.setText("user: " + songownername);
            position = getAdapterPosition();

//            songTitle.setText(songs.getName());  // null pointer
            // songArtist.setText(songs.getArtist());
        }


    }


    class SongAdapter extends RecyclerView.Adapter<MusicPlayerJoining.SongViewHolder> {
        List<Song> listOfSongs;

        public SongAdapter(List<Song> list) {
            listOfSongs = new ArrayList<>(list);
        }

        @NonNull
        @Override
        public MusicPlayerJoining.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.recyclerview_join_song_item, parent, false);
            return new MusicPlayerJoining.SongViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull MusicPlayerJoining.SongViewHolder holder, int position) {
            holder.onBind(listOfSongs.get(position));

        }

        @Override
        public int getItemCount() {
            return listOfSongs.size();
        }


    }


}
