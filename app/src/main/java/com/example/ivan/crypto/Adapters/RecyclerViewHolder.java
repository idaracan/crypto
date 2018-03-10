package com.example.ivan.crypto.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ivan.crypto.R;

/**
 * Created by ivan on 5/12/17.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView nombreMoneda, usdPrice, symbol, percentage;
    public ImageView logoMoneda;
    public RecyclerViewHolder(View itemView) {
        super(itemView);
        nombreMoneda = itemView.findViewById(R.id.name);
        percentage = itemView.findViewById(R.id.percent);
        logoMoneda = itemView.findViewById(R.id.coin_icon);
        usdPrice = itemView.findViewById(R.id.usdvalue);
        symbol = itemView.findViewById(R.id.symbol);
    }
}
