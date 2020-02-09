package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

/**
 * Allows user to create a new product
 * or edit an existing one.
 */
public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /* Identifier for the product data loader */
    private static final int EXISTING_PRODUCT_LOADER = 0;
    /* Product image */
    private static final int IMAGE_PICKER_CODE = 7;
    /* URI for the current product */
    private Uri mCurrentProductUri;
    /* Boolean flag that keeps track of whether the product has been edited */
    private boolean mProductHasChanged = false;
    /* EditText field to enter product's information */
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    /* Quantity buttons to increase and decrease */
    private Button mQuantityIncreaseButton;
    private Button mQuantityDecreaseButton;
    /* Order button from supplier */
    private Button mOrder;
    private byte[] mImageByteArray;
    private ImageView mProductImageView;
    private Button mUploadButton;
    /* Boolean flag to know if the user in Edit product or in Add New Product */
   private boolean mEditMood = false;

    /**
     * OnTouchListener that listens for user touches on a View, implying that they are modifying
     * the view, so change the mProductHasChanged to true
     * and check if the user in Add mood or Edit mood.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(mEditMood){ // If the user in Edit product mood change the title
            setTitle(getString(R.string.details_activity_title_edit_product));}
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        //Find the order button
        mOrder = (Button) findViewById(R.id.order_button);

        // If the intent is null add new product otherwise View the current product
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.details_activity_title_new_product));
            //No need to show the order button
            mOrder.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.details_activity_title_view_product));
            //Show the order button
            mOrder.setVisibility(View.VISIBLE);
            //Now when user press any filed change the title to Edit Product
            mEditMood = true;
            // Initialize a loader to reade the product data.
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all filed of product's information
        mNameEditText = (EditText) findViewById(R.id.product_name);
        mPriceEditText = (EditText) findViewById(R.id.product_price);
        mQuantityEditText = (EditText) findViewById(R.id.product_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.product_supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.product_supplier_phone);
        //Find all buttons
        mQuantityIncreaseButton = (Button) findViewById(R.id.increase_quantity);
        mQuantityDecreaseButton = (Button) findViewById(R.id.decrease_quantity);


        // Defined Quantity increase and decrease buttons
        mQuantityIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantity();
                mProductHasChanged = true;
            }
        });


        mQuantityDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuantity();
                mProductHasChanged = true;
            }
        });

        // Defined Order button on user click it open the phone app
        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhoneEditText.getText().toString().trim()));
                startActivity(intent);
            }
        });

        // Defined the image view and hide it
        mProductImageView = (ImageView) findViewById(R.id.product_image);
        mProductImageView.setVisibility(View.GONE);

        // Defined the upload button
        mUploadButton = (Button) findViewById(R.id.image_upload_button);

        /**
         * Setup upload image
         * Several helpful multi resources :
         * http://stackoverflow.com/questions/5309190/android-pick-images-from-gallery
         * http://stackoverflow.com/questions/10618325/how-to-create-a-blob-from-bitmap-in-android-activity
         */
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent chooserIntent = Intent.createChooser(intent, "Select an Image");
                startActivityForResult(chooserIntent, IMAGE_PICKER_CODE);
            }
        });

        /**
         * Setup OnTouchListeners on all input fields
         */
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

    }

    /**
     * Increase Quantity
     */
    private void increaseQuantity() {
        String currentQuantityString = mQuantityEditText.getText().toString().trim();
        int currentQuantity;
        if (currentQuantityString.isEmpty()) {
            currentQuantity = 0;
        } else {
            currentQuantity = parseInt(currentQuantityString);
        }
        mQuantityEditText.setText(String.valueOf(currentQuantity + 1));

    }

    /**
     * Decrease Quantity
     */
    private void decreaseQuantity() {
        String currentQuantityString = mQuantityEditText.getText().toString().trim();
        int currentQuantity;
        if (currentQuantityString.isEmpty()) {
            return;
        } else if (currentQuantityString.equals("0")) {
            return;
        }
        currentQuantity = parseInt(currentQuantityString);
        mQuantityEditText.setText(String.valueOf(currentQuantity - 1));
    }

    /**
     * Save the product into database.
     */
    private void saveProduct() {
        //Get the product's information from method getStringFromEditText()
        HashMap<String, String> information = getStringFromEditText();
        String name = information.get("name");
        String priceString = information.get("priceString");
        String quantityString = information.get("quantityString");
        String supplierName = information.get("supplier_name");
        String supplierPhone = information.get("supplier_phone");

        //Convert the priceString and quantityString to integer
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = parseInt(priceString);
        }
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = parseInt(quantityString);
        }

        //Create a ContentValues
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, name);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE, mImageByteArray);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);

        //Check if current URI is null ,then add new product otherwise update the current product
        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                //if the NEW URI is null so there an error on insert
                toastMessage(getString(R.string.insert_product_failed));
            } else {
                toastMessage(getString(R.string.insert_product_successful));
            }
        } else {
            int rowsUpdated = getContentResolver().update(mCurrentProductUri, values, null, null);
            // if rowsUpdated = 0 then the updated failed otherwise is successful
            if (rowsUpdated == 0) {
                toastMessage(getString(R.string.update_product_failed));
            } else {
                toastMessage(getString(R.string.insert_product_successful));
            }

        }
    }


    /**
     * Setup Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // option of menu
        switch (item.getItemId()) {
            case R.id.action_save:
                //Before save the product check if all field is valid values.
                if (isValid()) {
                    saveProduct();
                    //after save the product Exit the activity
                    finish();
                }
                return true;
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Check if there any changes before leaving the activity
                // if there not so the user can leave otherwise
                // Pop up confirmation dialog for notifies the user they have unsaved changes.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Loader Setup
     */

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that contains all columns from the product table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_IMAGE,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            //Find the columns
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_IMAGE);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIdex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            // Extract out the value from the cursor
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIdex);
            mImageByteArray = cursor.getBlob(imageColumnIndex);

            // Update the view
            mNameEditText.setText(name);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);

            // Convert the image from Blob to Bitmap
            Bitmap productImage = SetupImage.convertBlobToBitmap(mImageByteArray);

            // set Image
            if (productImage != null) {
                mProductImageView.setImageBitmap(productImage);
                mProductImageView.setVisibility(View.VISIBLE);
            } else {
                mProductImageView.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    /**
     * Show dialog if the user unsaved changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.unsaved_dialog);
        dialog.setPositiveButton(R.string.discard, discardButtonClickListener);
        dialog.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    /**
     * Show dialog if the user try to delete product
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.delete_dialog);
        dialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //if user click the delete then the product will deleted
                deleteProduct();
            }
        });

        //otherwise cancel the delete
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only delete if this is the existing product.
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // if rowsDeleted = 0 then there an error with delete otherwise successful delete.
            if (rowsDeleted == 0) {
                toastMessage(getString(R.string.delete_product_failed));
            } else {
                toastMessage(getString(R.string.delete_product_successful));
            }
        }
        // Exit the activity
        finish();
    }


    /**
     * Convent the input fields to String
     * Except the image
     * then save them into Hash map array to easy use it.
     */
    private HashMap<String, String> getStringFromEditText() {
        HashMap<String, String> productInformation = new HashMap<>();
        productInformation.put("name", mNameEditText.getText().toString().trim());
        productInformation.put("priceString", mPriceEditText.getText().toString().trim());
        productInformation.put("quantityString", mQuantityEditText.getText().toString().trim());
        productInformation.put("supplier_name", mSupplierNameEditText.getText().toString().trim());
        productInformation.put("supplier_phone", mSupplierPhoneEditText.getText().toString().trim());
        return productInformation;
    }

    /**
     * Check all the input is valid or not
     * if not show the error in edit text
     */

    private boolean isValid() {

        HashMap<String, String> productInformation = getStringFromEditText();

        if (productInformation.get("name").isEmpty()) {
            toastMessage(getString(R.string.null_name));
            return false;
        }


        String priceString = productInformation.get("priceString");
        int price = 0;

        if (priceString.isEmpty()) {
            toastMessage(getString(R.string.null_price));
            return false;
        }

        if (!TextUtils.isEmpty(priceString)) {
            try {
                price = parseInt(priceString);
                if (price < 0) {
                    toastMessage(getString(R.string.null_price));
                    return false;
                }
            } catch (NumberFormatException e) {
                Log.e("Price error ", "NumberFormatException");
            }

        }

        String quantityString = productInformation.get("quantityString");
        int quantity = 0;
        if (quantityString.isEmpty()) {
            toastMessage(getString(R.string.null_quantity));
            return false;
        }
        if (!TextUtils.isEmpty(quantityString)) {
            try {
                quantity = parseInt(quantityString);
                if (quantity < 0) {
                    toastMessage(getString(R.string.null_quantity));
                    return false;
                }
            } catch (NumberFormatException ex) {
                Log.e("Quantity error ", "NumberFormatException");
            }

        }

        //Check if the user upload image or not
        if (mImageByteArray == null) {
            toastMessage(getString(R.string.null_image));
            return false;
        }

        if (productInformation.get("supplier_name").isEmpty()) {
            toastMessage(getString(R.string.null_supplier_name));
            return false;
        }

        String supplierPhone = productInformation.get("supplier_phone");
        if (supplierPhone.isEmpty() || !supplierPhone.matches("\\d{10}")) {
            toastMessage(getString(R.string.null_supplier_phone));
            return false;
        }


        return true;
    }


    /**
     * toastMessage method to handling all toast messages on app.
     */

    private void toastMessage(String message) {
        Toast.makeText(DetailsActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Setup upload image
     * Several helpful multi resources :
     * http://stackoverflow.com/questions/5309190/android-pick-images-from-gallery
     * http://stackoverflow.com/questions/10618325/how-to-create-a-blob-from-bitmap-in-android-activity
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            // Otherwise get image out of data
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                mProductImageView.setImageBitmap(selectedImage);
                // Temp code to store image
                mImageByteArray = SetupImage.convertBitmapToBlob(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                toastMessage("Something went wrong");
            }
        } else if (requestCode == IMAGE_PICKER_CODE) {
            toastMessage("You haven't picked an image");
        }
    }


}
