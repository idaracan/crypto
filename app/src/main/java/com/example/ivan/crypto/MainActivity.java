package com.example.ivan.crypto;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements RecyclerAdapter.getCoinValuesCallback{
    RecyclerView recyclerView;
    JSONObject coinData;
    String[] coinValues = new String[5];
    String[] coinValueKeys = {Constants.id, Constants.name, Constants.symbol, Constants.usd,
            Constants.percentChange1h};
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerAdapter adapter = new RecyclerAdapter(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setCallback(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public JSONObject getCoin(String coinId, JSONArray coins){
        JSONObject coin;
        String idCoin;
        for (int i = 0; i < coins.length(); i++){
            try {
                coin = coins.getJSONObject(i);
                idCoin = coin.getString("id");
                if (idCoin.equals(coinId)){
                    return coin;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String[] getCoinValues(final String coinId){
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest request = new JsonArrayRequest
                (Constants.url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("response",response.toString());
                        coinData = getCoin(coinId, response);
                        try {
                            for (int i = 0; i < coinValues.length; i++ ) {
                                coinValues[i] = coinData.getString(coinValueKeys[i]);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("JSONerror", error.toString());
                    }
                });
        queue.add(request);
        return coinValues;
    }
}
