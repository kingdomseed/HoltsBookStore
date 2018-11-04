package com.holtnet.holtsbookstore;

import android.content.Context;
import android.database.Cursor;
import android.icu.text.NumberFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView bookNameItem = view.findViewById(R.id.bookNameItem);
        TextView supplierNameItem = view.findViewById(R.id.supplierNameItem);
        TextView bookPriceItem = view.findViewById(R.id.priceItem);
        TextView bookInStockItem = view.findViewById(R.id.inStockItem);

        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

        String bookName = cursor.getString(bookNameColumnIndex);
        String supplierName = cursor.getString(supplierNameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);

        bookNameItem.setText(bookName);
        supplierNameItem.setText(supplierName);

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        bookPriceItem.setText(formatter.format(price));
        if(quantity > 0)
        {
            bookInStockItem.setText(context.getString(R.string.in_stock));
        } else
        {
            bookInStockItem.setText(context.getString(R.string.out_of_stock));
        }

    }
}
