package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by sarahaldowihy on 10/11/2017 AD.
 */

public class InventoryContract {
    /**
     * Domain name
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    /**
     * The base of all URI's
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Path to table
     */
    public static final String PRODUCT_PATH = "products";

    /**
     * Private constructor to prevent
     * instantiation
     */
    private InventoryContract() {
    }

    public static final class InventoryEntry implements BaseColumns {
        /**
         * The content URI to access the product data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PRODUCT_PATH);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PRODUCT_PATH;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PRODUCT_PATH;
        /**
         * Name of database
         */
        public final static String DATABASE_NAME = "inventory.db";
        /**
         * Name of table
         */
        public final static String TABLE_NAME = "products";
        /**
         * Unique ID number for the products
         * Type: INTEGER
         * it's reference from BaseColumns directly
         */
        /**
         * Name of the product.
         * Type : TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "name";
        /**
         * Quantity of the product.
         * Type : INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        /**
         * Price of the product.
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";
        /**
         * Image of the product.
         * Type : Blob
         */
        public final static String COLUMN_PRODUCT_IMAGE = "image";
        /**
         * Name of the supplier
         * Type:TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        /**
         * The phone number of the supplier
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone";
        /**
         * Crate products table in database use SQL statement
         */
        public final static String SQL_CRATE_PRODUCTS_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + _ID + "  INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                        + COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                        + COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                        + COLUMN_PRODUCT_IMAGE + " Blob , "
                        + COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, "
                        + COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT NOT NULL "
                        + " );";
        /**
         * Drop products table from database use SQL statement
         */
        public final static String SQL_DROP_PRODUCTS_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        /**
         * Database version
         */
        public static final int DATABASE_VERSION = 1;

    }
}
