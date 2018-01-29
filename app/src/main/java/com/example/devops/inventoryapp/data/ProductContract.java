package com.example.devops.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DevOps on 6/11/2017.
 */

public class ProductContract {

    //Default constructor
    private ProductContract(){}

    //Content Authority
    public final static String CONTENT_AUTHORITY = "com.example.devops.inventoryapp";

    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public final static String PATH_PRODUCTS = "products";

    public final static class ProductEntry implements BaseColumns{

        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        //Name of the database for products
        public final static String TABLE_NAME = "products";

        //Now we list the columns that our database will contain.

        //Unique ID for the product (only for use in the database table). TYPE: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Name of product. TYPE: TEXT
        public final static String COLUMN_NAME = "name";

        //Quantity of product in stock. TYPE: INTEGER
        public final static String COLUMN_QUANTITY = "quantity";

        //Price of product. TYPE: INTEGER
        public final static String COLUMN_PRICE = "price";

        //Supplier of product. TYPE: TEXT
        public final static String COLUMN_SUPPLIER = "supplier";

        //Image of product. TYPE: BLOB
        public final static String COLUMN_IMAGE = "image";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


    }
}
