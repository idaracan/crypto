package com.example.ivan.crypto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by ivan on 5/12/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private String[] monedas = {"bitcoin","ethereum"};
    private Context context;
    private LayoutInflater layoutInflater;
    private getCoinValuesCallback callback;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) v.getTag();
            int pos = viewHolder.getAdapterPosition();
            String[] values = callback.getCoinValues(monedas[pos]);
            viewHolder.symbol.setText(values[2]);
            viewHolder.usdPrice.setText(String.format("%s USD", values[3]));
        }
    };

    RecyclerAdapter(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.nombreMoneda.setText(monedas[position]);
        holder.logoMoneda.setOnClickListener(onClickListener);
        holder.logoMoneda.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return monedas.length;
    }

    void setCallback(getCoinValuesCallback callback) {
        this.callback = callback;
    }

    public interface getCoinValuesCallback{
        String[] getCoinValues(String coin);
    }
}
