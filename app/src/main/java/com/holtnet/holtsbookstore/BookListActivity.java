package com.holtnet.holtsbookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.holtnet.holtsbookstore.data.BookContract;
import com.holtnet.holtsbookstore.data.BookDbHelper;
import com.holtnet.holtsbookstore.data.BookContract.BookEntry;

public class BookListActivity extends AppCompatActivity {

    private BookDbHelper bookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        bookDbHelper = new BookDbHelper(this);
        insertBook();
        displayDatabaseInfo();
    }

    private void insertBook()
    {
        String bookName = "Moby Dick";
        String bookSupplier = "Amazon";
        float bookPrice = 10.50f;
        long bookSupplierPhone = 8882804331L;

        SQLiteDatabase db = bookDbHelper.getWritableDatabase();
        ContentValues bookValues = new ContentValues();
        bookValues.put(BookEntry.COLUMN_PRODUCT_NAME, bookName);
        bookValues.put(BookEntry.COLUMN_SUPPLIER_NAME, bookSupplier);
        bookValues.put(BookEntry.COLUMN_PRICE, bookPrice);
        bookValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, bookSupplierPhone);

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, bookValues);

        if (newRowId == -1)
        {
            Toast.makeText(this, "There was an error saving this book.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book saved with row ID of: " + newRowId, Toast.LENGTH_SHORT).show();
        }


    }

    private void displayDatabaseInfo() {

        SQLiteDatabase db = bookDbHelper.getReadableDatabase();

        // Projection
        String[] bookProjection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                bookProjection,
                null,
                null,
                null,
                null,
                null
        );

        StringBuilder displayText = new StringBuilder("This book table contains " + cursor.getCount() + " books.\n\n");
        displayText.append(BookEntry._ID + " ** " + BookEntry.COLUMN_PRODUCT_NAME + " ** " +
                BookEntry.COLUMN_SUPPLIER_NAME + " ** " + BookEntry.COLUMN_PRICE + " ** " +
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER +  "\n\n");

        try {

            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int bookPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentBookNameColumnIndex = cursor.getString(bookNameColumnIndex);
                String currentSupplierNameColumnIndex = cursor.getString(supplierNameColumnIndex);
                String currentBookPriceColumnIndex = cursor.getString(bookPriceColumnIndex);
                String currentSupplierPhoneColumnIndex = cursor.getString(supplierPhoneColumnIndex);
                displayText.append(currentID + " ** " + currentBookNameColumnIndex + " ** " +
                        currentSupplierNameColumnIndex + " ** " + currentBookPriceColumnIndex + " ** " +
                        currentSupplierPhoneColumnIndex + "\n\n");
            }
            Log.i("DISPLAY DATA", displayText.toString());
        } finally {
            cursor.close();
        }
    }
}
