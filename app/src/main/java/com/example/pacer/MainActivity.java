package com.example.pacer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.RemoteControlClient;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import com.spotify.android.*;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "83bbac4b860942f7813149bdc4093004";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final String ACCESS_TOKEN = "BQCpHBjAKtSNK1GsgTSe5KGsW7Wi3lz6LFe0xz1DwUB3X8g59hmYJSYMgkPNDeyLgAtl7jhki-1rPViRboN8NDgcA1Ub_XyGB52_ts_DDDEyVcJXMYlZXSaFoZR4uIriqQgNOgMgaGKxVbd6cMjlY0h1";
    private static final String REFRESH_TOKEN = "AQBEA4FNjqAksqCJuZIZ5Wp8whyPpF5kcwNKa" + // There used to be newline here... dont know if that was necessary
            "5N-PWj79Csn8FN6Ss4g1lCJ9HVa8kN64kvPhlxxR-t2gb5gyGEm42xihrKo1IX5uZX3AEVgdSuPy6qwDoKY0VZiOWfrZ7g";
    private static final int REQUEST_CODE = 1337;
    private SpotifyAppRemote mSpotifyAppRemote;

    //TextView textView = (TextView) findViewById(R.id.current);
    Button pause;
    TextView pauseState;
    Button skip;
    Button submit;
    EditText bpmInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The only thing that's different is we added the 5 lines below.
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "user-modify-playback-state"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onStart() {

        skip = (Button) findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                skipSong.skip(MainActivity.this);
//                Intent pauseSpotify = new Intent("com.spotify.mobile.android.ui.widget.PAUSE");
//                pauseSpotify.setPackage("com.spotify.music");
//                sendBroadcast(pauseSpotify);
            }
        });

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

    }

    private void connected() {
        // Play a playlist
        /*
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
         */
        submit = (Button) findViewById(R.id.submit_button);
        bpmInput = (EditText) findViewById(R.id.editBpm);
        final getPlaylists[] temp = {null};
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int bpm = Integer.parseInt(String.valueOf(bpmInput.getText()));
                try {
                    temp[0] = new getPlaylists(bpm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        pause = (Button) findViewById(R.id.pause);
        pauseState = (TextView) findViewById(R.id.pause_state);
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSpotifyAppRemote.getPlayerApi()
                        .subscribeToPlayerState()
                        .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                            @Override
                            public void onEvent(PlayerState playerState) {
                                if(playerState.isPaused) {
                                    pauseState.setText("Paused");
                                }else{
                                    pauseState.setText("Not paused");
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}