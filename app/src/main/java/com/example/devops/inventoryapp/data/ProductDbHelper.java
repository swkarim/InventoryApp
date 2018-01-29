package com.example.devops.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.devops.inventoryapp.CatalogActivity;
import com.example.devops.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by DevOps on 6/11/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 1;

    private final static String DATABASE_NAME = "products.db";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create a String that contains the SQL statement to create the products table.
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_IMAGE + " BLOB);";

        //Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void sellOneItem(long itemId, int quantity, Context context){
        SQLiteDatabase db = getWritableDatabase();
        int newQuantity = 0;
        if (quantity > 0) {
            newQuantity = quantity -1;
        }
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, newQuantity);
        String selection = ProductContract.ProductEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(itemId) };
        db.update(ProductContract.ProductEntry.TABLE_NAME,
                values, selection, selectionArgs);

        Intent intent = new Intent(context, CatalogActivity.class);
        context.startActivity(intent);
    }


}
