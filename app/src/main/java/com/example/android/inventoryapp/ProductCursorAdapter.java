package com.example.android.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by sarahaldowihy on 10/11/2017 AD.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0/* flags */);
    }

    public View newView(Context context, Cursor cursor, ViewGroup group) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, group, false);
    }

    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        Button saleButton = (Button) view.findViewById(R.id.saleButton);

        // Read the product from cursor for current product use the column index
        String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        String price = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
        final int _ID = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));

        // Create URI for the current product use the _ID
        final Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, _ID);

        /**
         * when the user click the Sale button
         * reduces the quantity for the current product by one
         */

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                // Check if quantity > 0 to can reduces it.
                if (quantity > 0) {
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);
                    resolver.update(uri, values, null, null);
                    context.getContentResolver().notifyChange(uri, null);
                }
            }
        });

        // Update the TextViews for the current product
        nameTextView.setText(name);
        priceTextView.setText(price + " " + context.getString(R.string.price_units));
        quantityTextView.setText(quantity + " " + context.getString(R.string.quantity_units));
    }
}
