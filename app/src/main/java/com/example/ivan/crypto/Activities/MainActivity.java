package com.example.ivan.crypto.Activities;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.example.ivan.crypto.Adapters.SearchAdapter;
import com.example.ivan.crypto.Fragments.Content;
import com.example.ivan.crypto.R;
import com.example.ivan.crypto.Storage.Constants;
import com.example.ivan.crypto.Storage.DataBase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> coinIdList, coinNameList, searchedCoinNames, searchedCoinIds;
    ListView listView;
    HashMap<String, String> coin;
    SearchAdapter searchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeFragment(R.id.home);

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
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        searchAdapter.getFilter().filter(s.toString());
                    }
                });
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                changeFragment(itemId);
                return true;
            }
        });
    }

    private void changeFragment(int itemId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, Content.getInstance(itemId));
        fragmentTransaction.commit();
    }

    public Dialog makeDialog(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this,
                    android.R.style.Theme_Material_Light_Dialog_Alert);
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
                            searchedCoinNames = searchAdapter.getFilteredNameList();
                            searchedCoinIds   = searchAdapter.getFilteredIdList();
                            contentValues.put(Constants.coinId,
                                    searchedCoinIds.get(checkedItems.keyAt(i)));
                            contentValues.put(Constants.name,
                                    searchedCoinNames.get(checkedItems.keyAt(i)));
                            db.insert(Constants.myCoins, null, contentValues);
                        }
                    }
                    searchedCoinIds = coinIdList;
                    searchedCoinNames = coinNameList;
                    changeFragment(R.id.home);
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
            case R.id.refresh_data:
                Toast.makeText(this,"Refresh",Toast.LENGTH_SHORT).show();
            break;
            case R.id.refresh_list:
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.refreshing),Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                JsonArrayRequest request = new JsonArrayRequest(Constants.urlAll, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String key, value;
                        for (int i = 0; i < response.length(); i++){
                            try {
                                key = response.getJSONObject(i).getString(Constants.id);
                                value = response.getJSONObject(i).getString(Constants.name);
                                coin.put(key,value);
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
}
