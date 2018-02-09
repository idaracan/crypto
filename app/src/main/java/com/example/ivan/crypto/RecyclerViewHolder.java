package com.example.ivan.crypto;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ivan on 5/12/17.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    TextView nombreMoneda, valorMoneda;
    ImageView logoMoneda;
    public RecyclerViewHolder(View itemView) {
        super(itemView);
        nombreMoneda = itemView.findViewById(R.id.name);
        valorMoneda = itemView.findViewById(R.id.value);
        logoMoneda = itemView.findViewById(R.id.coin_icon);
    }
}
