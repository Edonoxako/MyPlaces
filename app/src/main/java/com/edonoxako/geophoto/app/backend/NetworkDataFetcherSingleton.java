package com.edonoxako.geophoto.app.backend;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class NetworkDataFetcherSingleton {

    private static NetworkDataFetcherSingleton instance;
    private RequestQueue queue;
    private static Context context;

    private NetworkDataFetcherSingleton(Context ctx) {
        context = ctx;
        queue = getRequestQueue();
    }

    public static synchronized NetworkDataFetcherSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkDataFetcherSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return queue;
    }

    public <T> void AddToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
