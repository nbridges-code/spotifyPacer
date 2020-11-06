package com.example.pacer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.RemoteControlClient;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "83bbac4b860942f7813149bdc4093004";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final String REFRESH_TOKEN = "AQBEA4FNjqAksqCJuZIZ5Wp8whyPpF5kcwNKa" + // There used to be newline here... dont know if that was necessary
            "5N-PWj79Csn8FN6Ss4g1lCJ9HVa8kN64kvPhlxxR-t2gb5gyGEm42xihrKo1IX5uZX3AEVgdSuPy6qwDoKY0VZiOWfrZ7g";
    private static final int REQUEST_CODE = 1337;
    private SpotifyAppRemote mSpotifyAppRemote;
    private static String playlistName = "https://api.spotify.com/v1/search?q=name:"; // GET
    public String access = "";

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
        submit = (Button) findViewById(R.id.submit_button);
        bpmInput = (EditText) findViewById(R.id.editBpm);
        final getPlaylists[] temp = new getPlaylists[1];
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int bpm = Integer.parseInt(String.valueOf(bpmInput.getText()));
                temp[0] = new getPlaylists(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        mSpotifyAppRemote.getPlayerApi().play(temp[0].getPlaylistId());
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
                    }
                },MainActivity.this, bpm, access);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    /*
    private void getPlaylists(Integer bpm) throws IOException {
        String tempName = playlistName + bpm + "&type=playlist";
        URL url = new URL(tempName);
        // Instantiate the RequestQueue.
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setRequestMethod("GET");
            String responseMessage = urlConnection.getResponseMessage();
            Log.d("responseMessage: ", responseMessage);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

    }
     */

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