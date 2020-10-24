package com.example.pacer;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class skipSong {

    public static void skip(Context c) {
        String urlString = "https://api.spotify.com/v1/me/player/next";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection client = null;
        try {
            client = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            client.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        client.disconnect();


//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("skipSong", "That didn't work!");
//            }
//        });
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);

    }

}
