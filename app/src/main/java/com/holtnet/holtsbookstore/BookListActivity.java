package com.holtnet.holtsbookstore;

import android.app.LoaderManager;
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
import android.widget.Toast;

import com.holtnet.holtsbookstore.data.BookDbHelper;
import com.holtnet.holtsbookstore.data.BookContract.BookEntry;

public class BookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    BookCursorAdapter bookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
    }

    private void insertBook()
    {
//        Uri uri;
//        ContentValues petValues = new ContentValues();
//        petValues.put(PetEntry.COLUMN_PET_NAME, "Miliani");
//        petValues.put(PetEntry.COLUMN_PET_BREED, "Shepherd");
//        petValues.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_FEMALE);
//        petValues.put(PetEntry.COLUMN_PET_WEIGHT, 45);
//
//        uri = getContentResolver().insert(PetEntry.CONTENT_URI, petValues);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        // Projection
        String[] project = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_SUPPLIER_NAME };

        return new CursorLoader(this, BookEntry.CONTENT_URI, project, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        bookCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookCursorAdapter.swapCursor(null);
    }
}
