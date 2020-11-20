package com.example.pacer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.RemoteControlClient;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "83bbac4b860942f7813149bdc4093004";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final int REQUEST_CODE = 1337;
    private SpotifyAppRemote mSpotifyAppRemote;
    private static String playlistName = "https://api.spotify.com/v1/search?q=name:"; // GET
    public String access = "";

    Button submit;
    EditText bpmInput, strideLengthInput, goalPaceInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "user-modify-playback-state"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onStart() {
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
                        Log.d("MainActivity", "Connected");

                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                        Toast.makeText(MainActivity.this, "Connection failed. Please restart the app.", Toast.LENGTH_LONG);
                    }
                });

    }

    private void connected() {
        submit = (Button) findViewById(R.id.submit_button);
        strideLengthInput = (EditText) findViewById(R.id.stride_length_edit);
        goalPaceInput = (EditText) findViewById(R.id.goal_pace_edit);
        TextView textView = (TextView) findViewById(R.id.current_song);
        TextView recommendation = (TextView) findViewById(R.id.bpm_recommendation);
        final getPlaylists[] temp = new getPlaylists[1];
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int strideLength = Integer.parseInt(String.valueOf(strideLengthInput.getText()));
                Log.d("StrideLength:", String.valueOf(strideLength));
                int goalPace = Integer.parseInt(String.valueOf(goalPaceInput.getText()));
                Log.d("GoalPace:", String.valueOf(goalPace));
                int bpm = bpmCalc(strideLength, goalPace);
                Log.d("bpm:", String.valueOf(bpm));
                recommendation.setText("To reach this pace, try a " + bpm + " bpm playlist.");
                temp[0] = new getPlaylists(() -> {

                    mSpotifyAppRemote.getPlayerApi().play(temp[0].getPlaylistId());
                    Log.d("PlaylistInfo:", temp[0].getPlaylistId());
                    mSpotifyAppRemote.getPlayerApi()
                            .subscribeToPlayerState()
                            .setEventCallback(playerState -> {
                                final Track track = playerState.track;
                                if (track != null) {
                                    textView.setText(track.name + " by " + track.artist.name);
                                    Log.d("TrackInfo:", track.name + " by " + track.artist.name);
                                }
                            });

                },MainActivity.this, bpm, access);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    protected int bpmCalc(int strideLength, int goalPace){
        return ((63360/(strideLength * goalPace)) + 4) / 5 * 5;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    access = response.getAccessToken();
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

}