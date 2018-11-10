package com.holtnet.holtsbookstore;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.holtnet.holtsbookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        final TextView bookInStockItem;

        Button sellButton = view.findViewById(R.id.sell_button);
        TextView bookNameItem = view.findViewById(R.id.bookNameItem);
        TextView bookPriceItem = view.findViewById(R.id.priceItem);
        bookInStockItem = view.findViewById(R.id.inStockItem);

        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

        final String id = cursor.getString(idColumnIndex);
        String bookName = cursor.getString(bookNameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);

        bookNameItem.setText(bookName);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        bookPriceItem.setText(formatter.format(price));

        bookInStockItem.setText(new StringBuilder().append(context.getString(R.string.in_stock)).append(String.valueOf(quantity)).toString());

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int changeQuantity = quantity;
                if( changeQuantity > 0)
                {
                    changeQuantity--;

                    ContentValues bookValues = new ContentValues();
                    bookValues.put(BookEntry.COLUMN_QUANTITY, changeQuantity);
                    bookInStockItem.setText(new StringBuilder().append(context.getString(R.string.in_stock)).append(String.valueOf(changeQuantity)).toString());

                    Uri selectedBook = Uri.withAppendedPath(BookEntry.CONTENT_URI, id);
                    int rowsAffected = context.getContentResolver().update(selectedBook, bookValues, null, null);

                    if(rowsAffected > 0)
                    {
                        Toast.makeText(context, context.getString(R.string.sold_toast), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.error_selling_book), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, context.getString(R.string.not_in_stock_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
