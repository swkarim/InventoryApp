package com.example.devops.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.devops.inventoryapp.data.ProductContract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;

    private byte[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Button saveProducttoDb = (Button) findViewById(R.id.add_product);

        Button saveImagetoDb = (Button) findViewById(R.id.add_image);

        saveProducttoDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });

        saveImagetoDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
    }

    public void saveProduct(){
        EditText inputName = (EditText) findViewById(R.id.input_product_name);
        EditText inputQuantity = (EditText) findViewById(R.id.input_product_quantity);
        EditText inputPrice = (EditText) findViewById(R.id.input_product_price);
        EditText inputSupplier = (EditText) findViewById(R.id.input_product_supplier);

        String productName = inputName.getText().toString().trim();
        String productSupplier = inputSupplier.getText().toString().trim();
        Integer productQuantity;
        Integer productPrice;

        //Data check for Strings
        try{
            if(productName == null || productName.trim().equals("null") || productName.trim()
                    .length() <= 0|| productSupplier == null || productSupplier.trim().equals("null")
                    || productSupplier.trim().length() <= 0 ){
                throw new IllegalArgumentException("Product requires valid name and supplier name");
            }
        } catch(IllegalArgumentException e){
            AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
            messageBox.setTitle("Input Error");
            messageBox.setMessage("Product requires a valid name and a valid supplier name.");
            messageBox.setCancelable(false);
            messageBox.setNeutralButton("OK", null);
            messageBox.show();

            return;
        }

        String productQuantityPreCast = inputQuantity.getText().toString().trim();
        String productPricePreCast = inputPrice.getText().toString().trim();

        if(productQuantityPreCast.length() <= 0 || productPricePreCast.length() <=0){
            AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
            messageBox.setTitle("Input Error");
            messageBox.setMessage("Product requires a valid quantity and a valid price.");
            messageBox.setCancelable(false);
            messageBox.setNeutralButton("OK", null);
            messageBox.show();

            return;
        }

        else{
            productQuantity = Integer.parseInt(productQuantityPreCast);
            productPrice = Integer.parseInt(productPricePreCast);
        }


        //Add the input data into Content Values and pass it as a insert statement
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_NAME, productName);
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, productQuantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, productPrice);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER, productSupplier);
        values.put(ProductContract.ProductEntry.COLUMN_IMAGE, image);

        Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent (this, CatalogActivity.class);
            startActivity(intent);
        }
    }


    public void saveImage(){
        Intent intent = new Intent();

        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                image = getBytes(bitmap);

                ImageView imageView = (ImageView) findViewById(R.id.productImageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // convert from bitmap to byte array
    public byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
