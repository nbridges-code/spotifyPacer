package com.example.pacer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;


public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "83bbac4b860942f7813149bdc4093004";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private SpotifyAppRemote mSpotifyAppRemote;

//    TextView textView = (TextView) findViewById(R.id.current);
//    Button pause = (Button) findViewById(R.id.pause);
//    TextView pauseState = (TextView) findViewById(R.id.pause_state);
//    Button skip = (Button) findViewById(R.id.skip_button);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {

//        skip.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                skipSong.skip(MainActivity.this);
////                Intent pauseSpotify = new Intent("com.spotify.mobile.android.ui.widget.PAUSE");
////                pauseSpotify.setPackage("com.spotify.music");
////                sendBroadcast(pauseSpotify);
//            }
//        });

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
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

        requestAuth.requestAuth(MainActivity.this);

    }

    private void connected() {
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX0XUsuxWHRQd");
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                            //textView.setText(track.name + " by " + track.artist.name);
                            Log.d("MainActivity", track.name + " by " + track.artist.name);
                        }
                    }
                });

//        pause.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                mSpotifyAppRemote.getPlayerApi()
//                        .subscribeToPlayerState()
//                        .setEventCallback(new Subscription.EventCallback<PlayerState>() {
//                            @Override
//                            public void onEvent(PlayerState playerState) {
//                                if(playerState.isPaused) {
//                                    //pauseState.setText("Paused");
//                                }else{
//                                    //pauseState.setText("Not paused");
//                                }
//                            }
//                        });
//            }
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}