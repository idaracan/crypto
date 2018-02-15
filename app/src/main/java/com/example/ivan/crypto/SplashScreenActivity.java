package com.example.ivan.crypto;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

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
                Bundle coins = new Bundle();
                ArrayList<String> coinIdList = new ArrayList<>();
                ArrayList<String> coinNameList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++){
                    try {
                        coinIdList.add(
                                response.getJSONObject(i).getString(Constants.id)
                        );
                        coinNameList.add(
                                response.getJSONObject(i).getString(Constants.name)
                        );
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                coins.putStringArrayList(Constants.id,coinIdList);
                coins.putStringArrayList(Constants.name,coinNameList);
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

    private void toMain(Bundle coins) {
        Intent toMain = new Intent(SplashScreenActivity.this,MainActivity.class);
        if (coins != null){
            toMain.putExtra(Constants.myCoins,coins);
        }
        startActivity(toMain);
        finish();
    }
}
