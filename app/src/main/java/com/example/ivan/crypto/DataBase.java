package com.example.ivan.crypto;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ivan on 14/02/18.
 */

public class DataBase extends SQLiteOpenHelper {

    private static final String createQuery = "create table " + Constants.myCoins + "("
            + Constants.id + " integer primary key autoincrement,"
            + Constants.coinId + " text unique,"
            + Constants.name + " text)";
    public static final String databaseName = "storedCoins";
    public static final int databaseVersion = 1;
    public static final String deleteEntries = "drop table if exists" + Constants.myCoins;

    public DataBase(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(deleteEntries);
        onCreate(db);
    }
}
