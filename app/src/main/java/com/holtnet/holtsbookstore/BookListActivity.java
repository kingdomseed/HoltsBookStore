package com.holtnet.holtsbookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.holtnet.holtsbookstore.data.BookDbHelper;
import com.holtnet.holtsbookstore.data.BookContract.BookEntry;

public class BookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    BookCursorAdapter bookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookListActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView bookListView = findViewById(R.id.list_view_books);

        View emptyList = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyList);

        Button sellButton = findViewById(R.id.sell_button);

        bookCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(bookCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BookListActivity.this, EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    private void insertBook() {

        Uri uri;
        ContentValues bookValues = new ContentValues();
        bookValues.put(BookEntry.COLUMN_BOOK_NAME, "Moby Dick");
        bookValues.put(BookEntry.COLUMN_SUPPLIER_NAME, "Amazon");
        bookValues.put(BookEntry.COLUMN_PRICE, 10.00);
        bookValues.put(BookEntry.COLUMN_QUANTITY, 45);
        bookValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "4567893210");

        uri = getContentResolver().insert(BookEntry.CONTENT_URI, bookValues);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("Book List Activity", rowsDeleted + " rows deleted from book database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Projection
        String[] project = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY
        };

        return new CursorLoader(this, BookEntry.CONTENT_URI, project, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookCursorAdapter.swapCursor(null);
    }
}
