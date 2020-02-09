package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by sarahaldowihy on 10/11/2017 AD.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public InventoryDbHelper(Context context) {
        super(context, InventoryEntry.DATABASE_NAME, null, InventoryEntry.DATABASE_VERSION);
    }

    /**
     * This is called when database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(InventoryEntry.SQL_CRATE_PRODUCTS_TABLE);
    }

    /**
     * This is called when the database needs to upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(InventoryEntry.SQL_DROP_PRODUCTS_TABLE);
        onCreate(database);
    }
}
