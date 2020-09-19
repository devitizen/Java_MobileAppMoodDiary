package com.devitizen.mooddiary;

import android.app.Application;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Makes requests for the Volley library (com.android.volley)
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private static RequestQueue requestQueue;


    /**
     * Makes requesting queue through Volley
     */
    @Override
    public void onCreate() {
        super.onCreate();

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack() {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    HttpURLConnection connection = super.createConnection(url);
                    connection.setInstanceFollowRedirects(false);

                    return connection;
                }
            });
        }
    }

    /**
     * Terminates request
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * Send requests through Volley
     *
     * @param requestCode request code
     * @param requestMethod request method
     * @param url   URL
     * @param params    parameters
     * @param listener listener
     */
    public static void send(final int requestCode, final int requestMethod, final String url,
                            final Map<String, String> params, final OnResponseListener listener) {
        StringRequest request = new StringRequest(
                requestMethod,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(listener != null) {
                            listener.processResponse(requestCode, 200, response);
                        }
                        Log.d(TAG, "Response for " + requestCode + " -> " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(listener != null) {
                            listener.processResponse(requestCode, 400, error.getMessage());
                        }
                        Log.d(TAG, "Error for " + requestCode + " -> " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        request.setShouldCache(false);
        request.setRetryPolicy( new DefaultRetryPolicy(10*1000, 0,
                                                          DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.requestQueue.add(request);
        Log.d(TAG, "Request sent: " + requestCode);
        Log.d(TAG, "Request url: " + url);
    }


    /**
     * ************************************************************
     * Inner Interface for OnResponseListener
     * ************************************************************
     */
    public interface OnResponseListener {
        void processResponse(int requestCode, int responseCode, String response);
    }

}
