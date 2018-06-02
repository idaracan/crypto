package com.example.ivan.crypto.Storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ivan on 14/02/18.
 */

public class DataBase extends SQLiteOpenHelper {

    private static final String tableAllCreateQuery = "create table " + Constants.myCoins + "("
            + Constants.id + " integer primary key autoincrement,"
            + Constants.coinId + " text unique,"
            + Constants.name + " text)";
    private static final String tableFavsCreateQuery = "create table " + Constants.favorites + "("
            + Constants.id + " integer primary key autoincrement,"
            + Constants.coinId + " text unique,"
            + Constants.name + " text)";
    private static final String databaseName = "storedCoins";
    private static final int databaseVersion = 2;
    private static final String deleteAllEntries    = "drop table if exists " + Constants.myCoins;
    private static final String deleteFavsEntries   = "drop table if exists " + Constants.favorites;

    public DataBase(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tableAllCreateQuery);
        db.execSQL(tableFavsCreateQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(deleteAllEntries);
        db.execSQL(deleteFavsEntries);
        onCreate(db);
    }
}
