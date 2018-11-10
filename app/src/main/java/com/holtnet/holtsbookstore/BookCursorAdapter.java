package com.holtnet.holtsbookstore;

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

import com.holtnet.holtsbookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    private int quantity = 0;
    private TextView bookInStockItem;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        Button sellButton = view.findViewById(R.id.sell_button);
        TextView bookNameItem = view.findViewById(R.id.bookNameItem);
        TextView bookPriceItem = view.findViewById(R.id.priceItem);
        bookInStockItem = view.findViewById(R.id.inStockItem);

        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

        String bookName = cursor.getString(bookNameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        quantity = cursor.getInt(quantityColumnIndex);

        bookNameItem.setText(bookName);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        bookPriceItem.setText(formatter.format(price));

        updateQuantityTextView(quantity, bookInStockItem, context);

        final int columnIndex = cursor.getColumnIndex(BookEntry._ID);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri selectedBookUri = ContentUris.withAppendedId (BookEntry.CONTENT_URI, columnIndex);
                ContentValues values = new ContentValues();
                quantity--;
                values.put ( BookEntry.COLUMN_QUANTITY, quantity);
                int rowsAffected = context.getContentResolver().update(selectedBookUri, values, null, null);
                if(rowsAffected == 0)
                {

                } else {

                }
                updateQuantityTextView(quantity, bookInStockItem, context);
            }
        });
    }

    private void updateQuantityTextView(int quantity, TextView bookInStockItem, Context context)
    {
        if(quantity > 0)
        {
            StringBuilder stringBuilder = new StringBuilder(context.getString(R.string.in_stock));
            bookInStockItem.setText(stringBuilder.append(quantity));
        } else
        {
            bookInStockItem.setText(context.getString(R.string.out_of_stock));
        }
    }
}
