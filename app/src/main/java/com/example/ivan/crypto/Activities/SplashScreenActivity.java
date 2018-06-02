package com.example.ivan.crypto.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ivan.crypto.R;
import com.example.ivan.crypto.Storage.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by ivan on 15/02/18.
 */

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestQueue queue = Volley.newRequestQueue(SplashScreenActivity.this);
        JsonArrayRequest request = new JsonArrayRequest(Constants.urlAll, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                LinkedHashMap<String, String> coins = new LinkedHashMap<>();
                String key, value;
                for (int i = 0; i < response.length(); i++){
                    try {
                        key     = response.getJSONObject(i).getString(Constants.id);
                        value   = response.getJSONObject(i).getString(Constants.name);
                        coins.put(key,value);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                toMain(coins);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
                toMain(null);
            }
        });
        queue.add(request);
    }

    private void toMain(LinkedHashMap coins) {
        Intent toMain = new Intent(SplashScreenActivity.this,MainActivity.class);
        if (coins != null){
            HashMap<String, String> hashMap = (HashMap<String, String>) coins;
            toMain.putExtra(Constants.myCoins,hashMap);
        }
        startActivity(toMain);
        finish();
    }
}
