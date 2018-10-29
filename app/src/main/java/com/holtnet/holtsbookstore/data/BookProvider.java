package com.holtnet.holtsbookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.holtnet.holtsbookstore.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    // TODO: complete insert, update, and delete methods

    private BookDbHelper bookDbHelper;

    // URI Matcher Codes for the Book table and a single Book in the table
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    @Override
    public boolean onCreate() {
        bookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder)  {

        SQLiteDatabase database = bookDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unable to query due to unknown URI:  " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = uriMatcher.match(uri);
        switch(match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is unsupported for:  " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues contentValues) {
        String bookName = contentValues.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        String supplierName = contentValues.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        Integer price = contentValues.getAsInteger(BookEntry.COLUMN_PRICE);
        if (bookName == null) {
            throw new IllegalArgumentException("Book name is required to add a book.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Book must be given a non-negative value.");
        }

        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        long id = database.insert(BookEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e("BookProvider", "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is unsupported for: " + uri);
        }

    }

    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (contentValues.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("You must enter a Book name.");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = bookDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if(rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is unsupported for:  " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
