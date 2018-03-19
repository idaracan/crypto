package com.example.ivan.crypto.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ivan.crypto.Adapters.RecyclerAdapter;
import com.example.ivan.crypto.Adapters.RecyclerViewHolder;
import com.example.ivan.crypto.Constants;
import com.example.ivan.crypto.MainActivity;
import com.example.ivan.crypto.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.ivan.crypto.R.layout.fragment_layout;

/**
 * Created by ivan on 10/03/18.
 */

public class Content extends Fragment implements RecyclerAdapter.getCoinValuesCallback{
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(fragment_layout,container,false);
        Bundle args = getArguments();
        RecyclerAdapter adapter;
        RecyclerView recyclerView   = view.findViewById(R.id.mRecyclerView);
        if (args != null){
            switch (args.getInt("position")) {
                case R.id.home:
                    adapter     = new RecyclerAdapter(getActivity(),R.id.home);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    adapter.setCallback(this);
                    break;
                case R.id.favorites:
                    adapter     = new RecyclerAdapter(getActivity(),R.id.favorites);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    adapter.setCallback(this);
                    break;
                case R.id.highlights:
                    break;
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public static Content getInstance(int selectedView){
        Content content = new Content();
        Bundle args = new Bundle();
        args.putInt("position",selectedView);
        content.setArguments(args);
        return content;
    }
    JSONObject coinData;
    @Override
    public void getCoinValues(final String coinId, final RecyclerViewHolder viewHolder){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest
                (Constants.url+coinId, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        float percentage;
                        try{
                            JSONObject singleCoin;
                            String idCoin;
                            for (int i = 0; i < response.length(); i++){
                                singleCoin = response.getJSONObject(i);
                                idCoin = singleCoin.getString(Constants.id);
                                if(idCoin.equals(coinId)){
                                    coinData = singleCoin;
                                    break;
                                }
                            }
                            viewHolder.symbol.setText(
                                    String.format("%s",coinData.getString(Constants.symbol))
                            );
                            viewHolder.usdPrice.setText(
                                    String.format("%s USD",coinData.getString(Constants.usd))
                            );
                            try {
                                percentage = Float.parseFloat(coinData.
                                        getString(Constants.percentChange1h));
                                viewHolder.percentage.setText(String.format("%s%%",percentage));
                                if (percentage < 0) {
                                    viewHolder.percentage.setTextColor(
                                            ContextCompat.getColor(
                                                    getContext(), R.color.red)
                                    );
                                }else if (percentage > 0){
                                    viewHolder.percentage.setTextColor(
                                            ContextCompat.getColor(
                                                    getContext(), R.color.green)
                                    );
                                }
                            } catch (NumberFormatException e){
                                viewHolder.percentage.setText(String.format("%s%%","?"));
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }catch (NullPointerException nullptr){
                            nullptr.printStackTrace();
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getContext(),getResources().getString(R.string.error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(request);
    }
}
