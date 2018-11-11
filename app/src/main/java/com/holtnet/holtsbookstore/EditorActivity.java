package com.holtnet.holtsbookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.holtnet.holtsbookstore.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri currentBookUri;
    private boolean bookHasChanged = false;

    private EditText editBookNameText;
    private EditText editBookPriceText;
    private EditText editBookQuantityText;
    private EditText editSupplierNameText;
    private EditText editSupplierPhoneText;

    private Button addButton;
    private Button subtractButton;
    private Button callSupplierButton;

    private boolean toBeOrNotToBeValid = false;

    private int quantity = 0;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentBookUri = intent.getData();

        if (currentBookUri == null) {
            setTitle(R.string.edit_book_title);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.add_book_title);
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        editBookNameText = findViewById(R.id.editBookName);
        editBookPriceText = findViewById(R.id.editTextPrice);
        editBookQuantityText = findViewById(R.id.editTextQuantity);
        editSupplierNameText = findViewById(R.id.editSupplierName);
        editSupplierPhoneText = findViewById(R.id.editSupplyNumber);
        addButton = findViewById(R.id.addButton);
        subtractButton = findViewById(R.id.subtractButton);
        callSupplierButton = findViewById(R.id.callButton);

        editBookNameText.setOnTouchListener(touchListener);
        editBookPriceText.setOnTouchListener(touchListener);
        editBookQuantityText.setOnTouchListener(touchListener);
        editSupplierNameText.setOnTouchListener(touchListener);
        editSupplierPhoneText.setOnTouchListener(touchListener);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                editBookQuantityText.setText(String.valueOf(quantity));
            }
        });

        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    quantity--;
                    editBookQuantityText.setText(String.valueOf(quantity));
                } else {
                    Toast.makeText(EditorActivity.this, getString(R.string.quantity_too_small), Toast.LENGTH_SHORT).show();
                }
            }
        });

        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editSupplierPhoneText.getText())) {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(("tel:" + editSupplierPhoneText.getText().toString())));
                    if (intent.resolveActivity(getPackageManager()) != null)
                    {
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(EditorActivity.this, getString(R.string.enter_phone_first), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                    saveBook();
                    if(toBeOrNotToBeValid)
                    {
                        finish();
                    }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
        };

        return new CursorLoader(this,
                currentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int bookPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int bookQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String bookName = cursor.getString(bookNameColumnIndex);
            double bookPrice = cursor.getDouble(bookPriceColumnIndex);
            quantity = cursor.getInt(bookQuantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            // Update the views on the screen with the values from the database
            editBookNameText.setText(bookName);

            editBookPriceText.setText(String.valueOf(bookPrice));

            editBookQuantityText.setText(String.valueOf(quantity));
            editSupplierNameText.setText(supplierName);
            editSupplierPhoneText.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        editBookNameText.setText("");
        editBookPriceText.setText("");
        editBookPriceText.setText("");
        editSupplierNameText.setText("");
        editSupplierPhoneText.setText("");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_this_book);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (currentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.error_deleting_book),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.book_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private boolean saveBook() {

        String bookNameString = editBookNameText.getText().toString().trim();
        String bookPriceString = editBookPriceText.getText().toString().trim();
        String bookQuantityString = editBookQuantityText.getText().toString().trim();
        String supplierNameString = editSupplierNameText.getText().toString().trim();
        String supplierNumberString = editSupplierPhoneText.getText().toString().trim();

        if (currentBookUri == null &&
                TextUtils.isEmpty(bookNameString) && TextUtils.isEmpty(bookPriceString) &&
                TextUtils.isEmpty(bookQuantityString) && TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierNumberString)) {
            return toBeOrNotToBeValid;
        }

        ContentValues bookValues = new ContentValues();
        if (!TextUtils.isEmpty(bookNameString)) {
            bookValues.put(BookEntry.COLUMN_BOOK_NAME, bookNameString);
        } else {
            Toast.makeText(EditorActivity.this, getString(R.string.enter_book_name), Toast.LENGTH_SHORT).show();
            toBeOrNotToBeValid = false;
            return toBeOrNotToBeValid;
        }

        if (!TextUtils.isEmpty(supplierNameString)) {
            bookValues.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        } else {
            Toast.makeText(EditorActivity.this, getString(R.string.insertSupplierName), Toast.LENGTH_SHORT).show();
            toBeOrNotToBeValid = false;
            return toBeOrNotToBeValid;
        }

        if (!TextUtils.isEmpty(supplierNumberString) && supplierNameString.length() > 9) {
            bookValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierNumberString);
        } else {
            Toast.makeText(EditorActivity.this, getString(R.string.enter_phone_first), Toast.LENGTH_SHORT).show();
            toBeOrNotToBeValid = false;
            return toBeOrNotToBeValid;
        }

        if (!TextUtils.isEmpty(bookPriceString) && Double.parseDouble(bookPriceString) >= 0) {
            double price;
            price = Double.parseDouble(bookPriceString);
            bookValues.put(BookEntry.COLUMN_PRICE, price);
        } else {
            Toast.makeText(EditorActivity.this, getString(R.string.starting_price), Toast.LENGTH_SHORT).show();
            toBeOrNotToBeValid = false;
            return toBeOrNotToBeValid;
        }

        if (!TextUtils.isEmpty(bookQuantityString) && Integer.parseInt(bookQuantityString) >= 0) {
            quantity = Integer.parseInt(bookQuantityString);
            bookValues.put(BookEntry.COLUMN_QUANTITY, quantity);
        } else {
            Toast.makeText(EditorActivity.this, getString(R.string.start_quantity), Toast.LENGTH_SHORT).show();
            toBeOrNotToBeValid = false;
            return toBeOrNotToBeValid;
        }

        if (currentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, bookValues);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_inserting),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.book_insert_success),
                        Toast.LENGTH_SHORT).show();
            }

        } else {

            int rowsAffected = getContentResolver().update(currentBookUri, bookValues, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.update_book_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.update_book_success),
                        Toast.LENGTH_SHORT).show();
            }
        }

        toBeOrNotToBeValid = true;
        return toBeOrNotToBeValid;
    }

}
