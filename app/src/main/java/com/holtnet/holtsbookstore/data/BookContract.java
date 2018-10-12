package com.holtnet.holtsbookstore.data;

import android.provider.BaseColumns;

public class BookContract {

    public static final class BookEntry implements BaseColumns {
        public final static String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "book";
        public static final String COLUMN_SUPPLIER_NAME = "supplier";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "phone";
    }
}
