package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by sarahaldowihy on 10/11/2017 AD.
 */

public class InventoryProvider extends ContentProvider {
    /**
     * Tag for LOG message
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    /**
     * URI matcher code for the products table.
     */
    private static final int PRODUCTS = 100;
    /**
     * URI matcher code for a single product in the products table.
     */
    private static final int PRODUCT_ID = 101;
    /**
     * URI matcher
     */
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Static initializer.
     */
    static {
        URI_MATCHER.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PRODUCT_PATH, PRODUCTS);
        URI_MATCHER.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PRODUCT_PATH + "/#", PRODUCT_ID);
    }

    /**
     * Database helper object
     */
    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;

        //Figure out if the URI matcher can match the URI to specific code
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("unKnown URI " + uri);
        }

        //Set notification URI on cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("UnKnown URI " + uri + " with match  " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Failed insert in URI " + uri);
        }
    }

    /**
     * Insert a product into the database.
     * return new content URI for the new row.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Insert the new product with the given values
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);

        //Check if the id = -1 , then the insertion failed.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete product method with check if the user want
     * to delete all products , or
     * single product
     * Return the number of rows deleted
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Track the number of the rows that were deleted
        int rowsDeleted;

        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                //Delete all products match the selection and selection args
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                //Delete single product given by ID in URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed deletion " + uri);
        }

        // If 1 or more rows delete , then notify all listeners.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                //Update all products match the selection and selection args
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                //Update single product given by ID in URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Failed updated " + uri);
        }
    }

    /**
     * Update product in database.
     * Return the number of rows updated
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //If there are no values to update then don't try update database
        if (values.size() == 0) {
            return 0;
        }

        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        //If 1 or more rows updated , then notify all listeners
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

}

