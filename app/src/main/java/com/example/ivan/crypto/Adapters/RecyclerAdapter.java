package com.example.ivan.crypto.Adapters;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.example.ivan.crypto.Constants;
import com.example.ivan.crypto.DataBase;
import com.example.ivan.crypto.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 5/12/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<String> myCoins;
    private List<String> myCoinNames;
    private LayoutInflater layoutInflater;
    private getCoinValuesCallback callback;
    private Context context;

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Dialog dialog = makeDialog(v);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
            return false;
        }
    };

    public RecyclerAdapter(Context context){
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

    public void setCallback(getCoinValuesCallback callback) {
        this.callback = callback;
    }

    public interface getCoinValuesCallback{
        void getCoinValues(String coin, RecyclerViewHolder viewHolder);
    }

    public Dialog makeDialog(final View v){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context,
                    android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setItems(R.array.options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){ dialog.dismiss(); }
                DataBase dataBase = new DataBase(context);
                SQLiteDatabase liteDatabase = dataBase.getWritableDatabase();
                RecyclerViewHolder viewHolder = (RecyclerViewHolder) v.getTag();
                int pos = viewHolder.getAdapterPosition();
                switch (which){
                    case 0:
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Constants.coinId,myCoins.get(pos));
                        contentValues.put(Constants.name,myCoins.get(pos));
                        liteDatabase.insert(Constants.favorites,null, contentValues);
                        Toast.makeText(context,"Added to favorites",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        String[] whereArgs = {myCoins.get(pos)};
                        liteDatabase.delete(Constants.myCoins,
                                Constants.coinId + " =?", whereArgs);
                        refresh(context);
                        dialog.dismiss();
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });
        return builder.create();
    }

}
