package com.example.ivan.crypto;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 5/12/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    public List<String> myCoins;
    public List<String> myCoinNames;
    private LayoutInflater layoutInflater;
    private getCoinValuesCallback callback;
    private Context context;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) v.getTag();
            int pos = viewHolder.getAdapterPosition();
            callback.getCoinValues(myCoins.get(pos), viewHolder);
        }
    };
    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) v.getTag();
            int pos = viewHolder.getAdapterPosition();
            DataBase dataBase = new DataBase(context);
            SQLiteDatabase liteDatabase = dataBase.getWritableDatabase();
            String[] whereArgs = {myCoins.get(pos)};
            liteDatabase.delete(Constants.myCoins, Constants.coinId + " =?", whereArgs);
            refresh(context);
            return false;
        }
    };

    RecyclerAdapter(Context context){
        this.context = context;
        refresh(context);
        layoutInflater = LayoutInflater.from(context);
    }

    public void refresh(Context context) {
        myCoins = new ArrayList<>();
        myCoinNames = new ArrayList<>();
        DataBase dataBase = new DataBase(context);
        SQLiteDatabase liteDatabase = dataBase.getWritableDatabase();
        Cursor cursor = liteDatabase.rawQuery("select * from "+ Constants.myCoins,null);
        if (cursor.moveToFirst()){
            do {
                myCoins.add(cursor.getString(cursor.getColumnIndex(Constants.coinId)));
                myCoinNames.add(cursor.getString(cursor.getColumnIndex(Constants.name)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.nombreMoneda.setText(myCoinNames.get(position));
        holder.logoMoneda.setOnClickListener(onClickListener);
        holder.logoMoneda.setTag(holder);
        holder.logoMoneda.isLongClickable();
        holder.logoMoneda.setOnLongClickListener(onLongClickListener);
        holder.logoMoneda.setTag(holder);
        callback.getCoinValues(myCoins.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return myCoins.size();
    }

    void setCallback(getCoinValuesCallback callback) {
        this.callback = callback;
    }

    public interface getCoinValuesCallback{
        void getCoinValues(String coin, RecyclerViewHolder viewHolder);
    }
}
