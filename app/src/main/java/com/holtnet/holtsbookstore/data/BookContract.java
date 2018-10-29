package com.holtnet.holtsbookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {

    // TODO: finish hooking up COLUMN_QUANTITY

    // Intentionally private empty constructor to ensure BookContract is never instantiated.
    private BookContract() {}

    public static final String CONTENT_AUTHORITY = "com.holtnet.holtsbookstore.books";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        // MIME types for books
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        // Books table
        public final static String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "book";
        public static final String COLUMN_SUPPLIER_NAME = "supplier";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "phone";
    }
}
