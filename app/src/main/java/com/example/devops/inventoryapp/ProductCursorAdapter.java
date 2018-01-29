package com.example.devops.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.devops.inventoryapp.data.ProductContract.ProductEntry;
import com.example.devops.inventoryapp.data.ProductDbHelper;


/**
 * Created by DevOps on 6/11/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {

    Context passContext;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        passContext = context;

        //Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.list_item_product_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.list_item_product_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.list_item_product_price);

        //Find the columns of pet attributes that we are interested in
        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);

        //Read the pet attributes from the Cursor for the current pet
        final int itemId = cursor.getInt(idColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText("Product Name: " + productName);
        quantityTextView.setText("Quantity in Stock: " + String.valueOf(productQuantity));
        priceTextView.setText("Price per Unit: " + String.valueOf(productPrice));

        Button sellSingleItem = (Button) view.findViewById(R.id.sell_one_button);
        sellSingleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProductDbHelper dbHelper = new ProductDbHelper(context);
                dbHelper.sellOneItem(itemId, productQuantity, context);

            }
        });

    }
}
