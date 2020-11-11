package com.example.pacer;

import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.HttpConnection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class getPlaylists {
    private static String playlistName = "https://api.spotify.com/v1/search?q="; // GET
    private String playlistid = "";

    public getPlaylists(VolleyCallBack callBack, Context context, int bpm, String token) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String endpoint = playlistName + String.valueOf(bpm) + "&type=playlist";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    JSONObject playlists = response.optJSONObject("playlists");
                    JSONArray items = playlists.optJSONArray("items");
                    int found = 0;
                    boolean notDone = true;
                    for (int n = 0; n < items.length(); n++) {
                        try {
                            JSONObject item = items.getJSONObject(n);
                            if(String.valueOf(item.get("name")).contains("bpm") && notDone){
                                found = n;
                                notDone = false;
                            }
                            Log.d("getPlaylists", String.valueOf(item.get("name")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    playlistid = (String) items.optJSONObject(found).opt("uri");
                    callBack.onSuccess();
                }, error -> {
                    Log.e("getPlaylist Error:", String.valueOf(error));
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    public String getPlaylistId(){
        return playlistid;
    }

}