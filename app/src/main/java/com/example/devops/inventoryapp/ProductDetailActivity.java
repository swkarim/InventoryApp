package com.example.devops.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devops.inventoryapp.data.ProductContract.ProductEntry;

public class ProductDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //The quantity variable that we will be modifying should the user choose to do so.
    int quantity = 0;

    String name = "";
    String supplier = "";

    private static final int EXISTING_PET_LOADER = 1;

    private Uri mCurrentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        getSupportLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the products table

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_SUPPLIER,
                ProductEntry.COLUMN_IMAGE
        };


        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }



        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)

        if (cursor.moveToFirst()) {

            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE);


            // Extract out the value from the Cursor for the given column index
            name = cursor.getString(nameColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            supplier = cursor.getString(supplierColumnIndex);
            byte[] image = cursor.getBlob(imageColumnIndex);

            // Update the views on the screen with the values from the database
            TextView mProductDetailName = (TextView) findViewById(R.id.product_detail_name);
            TextView mProductDetailQuantity = (TextView) findViewById(R.id.product_detail_quantity);
            TextView mProductDetailPrice = (TextView) findViewById(R.id.product_detail_price);
            TextView mProductDetailSupplier = (TextView) findViewById(R.id.product_detail_supplier);
            ImageView mProductDetailImage = (ImageView) findViewById(R.id.productDisplay);

            mProductDetailName.setText("Product Name: " + name);
            mProductDetailQuantity.setText("Quantity in Stock: " + String.valueOf(quantity));
            mProductDetailPrice.setText("Price Per Unit: " + String.valueOf(price));
            mProductDetailSupplier.setText("Supplier Name: " + String.valueOf(supplier));

            //If the user has added an image then
            //get the bitmap from bytes array and set it to the image
            if(image != null && image.length > 0){
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                mProductDetailImage.setImageBitmap(bitmap);
            }

            //Else skip showing the image.
        }

        //Hook up the add and subtract buttons to the input dialog.
        Button addQuantityButton = (Button) findViewById(R.id.addQuantity);
        Button subtractQuantityButton = (Button) findViewById(R.id.subtractQuantity);


        //Create the onClickListener for both the buttons. The difference will be whether we want to
        //add or subtract quantity.

        View.OnClickListener modifyQuantity = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ProductDetailActivity.this);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ProductDetailActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                TextView toDisplay = (TextView) promptsView.findViewById(R.id.user_input_for_quantity);

                AlertDialog alertDialog;


                switch(v.getId())
                {
                    case R.id.addQuantity:

                        toDisplay.setText(R.string.user_add_quantity);

                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                ContentValues values = new ContentValues();
                                                int newQuantity = quantity + Integer.parseInt(userInput.getText().toString());

                                                values.put(ProductEntry.COLUMN_QUANTITY, newQuantity);

                                                int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

                                                // Show a toast message depending on whether or not the update was successful.
                                                if (rowsAffected == 0) {
                                                    // If no rows were affected, then there was an error with the update.
                                                    Toast.makeText(ProductDetailActivity.this, getString(R.string.editor_update_product_failed),
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Otherwise, the update was successful and we can display a toast.
                                                    Toast.makeText(ProductDetailActivity.this, getString(R.string.editor_update_product_successful),
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                         alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                        break;
                    case R.id.subtractQuantity:
                        //Remove Quantity
                        toDisplay.setText(R.string.user_subtract_quantity);
                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                ContentValues values = new ContentValues();
                                                int newQuantity = quantity - Integer.parseInt(userInput.getText().toString());
                                                if(newQuantity < 0){
                                                    Toast.makeText(ProductDetailActivity.this, getString(R.string.negative_quantity),
                                                            Toast.LENGTH_SHORT).show();
                                                    dialog.cancel();
                                                    return;
                                                }
                                                values.put(ProductEntry.COLUMN_QUANTITY, newQuantity);

                                                int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

                                                // Show a toast message depending on whether or not the update was successful.
                                                if (rowsAffected == 0) {
                                                    // If no rows were affected, then there was an error with the update.
                                                    Toast.makeText(ProductDetailActivity.this, getString(R.string.editor_update_product_failed),
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Otherwise, the update was successful and we can display a toast.
                                                    Toast.makeText(ProductDetailActivity.this, getString(R.string.editor_update_product_successful),
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                        break;
                }
            }
        };

        addQuantityButton.setOnClickListener(modifyQuantity);
        subtractQuantityButton.setOnClickListener(modifyQuantity);




        //Set up the action to take on the Delete Button
        Button deleteProduct = (Button) findViewById(R.id.deleteProduct);

        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        //Set up the Button to order more
        Button orderMore = (Button) findViewById(R.id.emailSupplier);
        orderMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","abc@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Request for Delivery of " + name);
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello " + supplier + "\n We would like to " +
                        "place an order for " + name + ".");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Nothing here for now.
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }

            // Close the activity
            finish();
        }
    }
}
