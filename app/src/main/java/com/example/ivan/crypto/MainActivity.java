package com.example.ivan.crypto;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements RecyclerAdapter.getCoinValuesCallback{
    RecyclerView recyclerView;
    JSONObject coinData;
    List<String> coinIdList, coinNameList, searchedCoinNames, searchedCoinIds;
    ListView listView;
    HashMap<String, String> coin;
    RecyclerAdapter adapter;
    SearchAdapter searchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshRecyclerView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent recievedIntent = getIntent();
        if (recievedIntent.hasExtra(Constants.myCoins)){
            coin = (HashMap<String, String>) recievedIntent.getSerializableExtra(Constants.myCoins);
            coinNameList = new ArrayList<>(coin.values());
            searchedCoinNames = coinNameList;
            coinIdList = new ArrayList<>(coin.keySet());
            searchedCoinIds = coinIdList;
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = makeDialog();
                dialog.show();
                searchAdapter = new SearchAdapter(MainActivity.this,coin);
                listView = dialog.findViewById(R.id.list);
                listView.setAdapter(searchAdapter);
                EditText search = dialog.findViewById(R.id.search);
                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        searchAdapter.getFilter().filter(s.toString());
                        searchedCoinNames = searchAdapter.getFilteredNameList();
                        searchedCoinIds   = searchAdapter.getFilteredIdList();
                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });
    }

    private void refreshRecyclerView() {
        adapter = new RecyclerAdapter(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setCallback(this);
    }

    public Dialog makeDialog(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setView(R.layout.dialog_coinlist);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    DataBase dataBase = new DataBase(getApplicationContext());
                    SQLiteDatabase db = dataBase.getWritableDatabase();
                    ContentValues contentValues;
                    SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                    for (int i = 0; i < checkedItems.size(); i++) {
                        if (checkedItems.get(checkedItems.keyAt(i))) {
                            contentValues = new ContentValues();
                            contentValues.put(Constants.coinId, searchedCoinIds.get(checkedItems.keyAt(i)));
                            contentValues.put(Constants.name, searchedCoinNames.get(checkedItems.keyAt(i)));
                            db.insert(Constants.myCoins, null, contentValues);
                        }
                    }
                    adapter.refresh(getApplicationContext());
                    searchedCoinIds = coinIdList;
                    searchedCoinNames = coinNameList;
                    dialog.dismiss();
                }catch (IndexOutOfBoundsException except){
                    except.printStackTrace();
                    Log.e("error", except.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        builder.setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
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
            case R.id.refresh_data:
                refreshRecyclerView();
            break;
            case R.id.refresh_list:
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.refreshing),Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                JsonArrayRequest request = new JsonArrayRequest(Constants.urlAll, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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
                        Toast.makeText(getApplicationContext(),getResources()
                                .getString(R.string.done),Toast.LENGTH_SHORT).show();
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
                (Constants.url+coinId, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        float percentage;
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
