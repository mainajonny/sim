package com.aw.simitrac.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * Created by AlexBoey on 5/21/2017.
 */

public class API {

   // public static String URL ="https://school.kodinet.net/api/";
    public static String URL ="https://shuleh.com/api/index.php";

    public static Gson mGson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .setPrettyPrinting()
            .setLenient()
            .serializeNulls()
            .create();

    public static void save(Context context ,String key , String value){
        SharedPreferences prefs= context.getSharedPreferences("com.coretec.msacco.pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= prefs.edit();
        editor.putString(key,value);
        editor.apply();

    }

    public static String getValue(Context context ,String key){
        //if 1st time , register the user
        SharedPreferences prefs = context.getSharedPreferences("com.coretec.msacco.pref", Context.MODE_PRIVATE);
        return prefs.getString(key , "0");
    }


    public static void POST(final Context context, final Map<String,String> params, final VolleyCallback callback ) {
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.i("# REQUEST",URL);
        Log.i("# PARAMS",params.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String results) {
                        Log.i("# results",results);
                        callback.onSuccess(results);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){

                return params;
            }
        };
        stringRequest.setShouldCache(false);
        // Wait 30 seconds and don't retry more than once
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

}
