package com.example.ivan.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements RecyclerAdapter.getCoinValuesCallback{
    RecyclerView recyclerView;
    JSONObject coinData;
    List<String> coinIdList = new ArrayList<>();
    List<String> coinNameList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerAdapter adapter = new RecyclerAdapter(this);
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
                Toast.makeText(getApplicationContext(),coinNameList.get(
                        coinNameList.size()-1),Toast.LENGTH_LONG).show();
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
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.refresh:
                Toast.makeText(getApplicationContext(),
                        "Refreshing all data",Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                JsonArrayRequest request = new JsonArrayRequest(Constants.url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("response",response.toString());
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
                        Toast.makeText(getApplicationContext(),"Done refreshing",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(request);
                break;
            default:
                break;
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
    public void getCoinValues(final String coinId, final RecyclerViewHolder viewHolder){
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest request = new JsonArrayRequest
                (Constants.url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        float percentage;
                        Log.v("response",response.toString());
                        coinData = getCoin(coinId, response);
                        try{
                            viewHolder.symbol.setText(
                                    String.format("%s",coinData.getString(Constants.symbol))
                            );
                            viewHolder.usdPrice.setText(
                                    String.format("%s USD",coinData.getString(Constants.usd))
                            );
                            try {
                                percentage = Float.parseFloat(coinData.getString(Constants.percentChange1h));
                                viewHolder.percentage.setText(String.format("%s%%",percentage));
                                if (percentage < 0) {
                                    viewHolder.percentage.setTextColor(
                                            ContextCompat.getColor(getApplicationContext(), R.color.red)
                                    );
                                }else if (percentage > 0){
                                    viewHolder.percentage.setTextColor(
                                            ContextCompat.getColor(getApplicationContext(), R.color.green)
                                    );
                                }
                            } catch (NumberFormatException e){
                                viewHolder.percentage.setText(String.format("%s%%","?"));
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }catch (NullPointerException nullptr){
                            nullptr.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(request);
    }
}
