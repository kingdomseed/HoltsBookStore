package com.holtnet.holtsbookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
import android.widget.Toast;

import com.holtnet.holtsbookstore.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri currentBookUri;
    private boolean bookHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentBookUri = intent.getData();

        if(currentBookUri == null)
        {
            setTitle(R.string.edit_book_title);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.add_book_title);
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }
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

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

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
                finish();
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

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int bookPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int bookQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String bookName = cursor.getString(bookNameColumnIndex);
            double bookPrice = cursor.getDouble(bookPriceColumnIndex);
            int bookQuantity = cursor.getInt(bookQuantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            // Update the views on the screen with the values from the database
            //mNameEditText.setText(name);
            //mBreedEditText.setText(breed);
            //mWeightEditText.setText(Integer.toString(weight));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        //mNameEditText.setText("");
        //mBreedEditText.setText("");
        //mWeightEditText.setText("");
        //mGenderSpinner.setSelection(0); // Select "Unknown" gender
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

    private void saveBook() {
//        String nameString = mNameEditText.getText().toString().trim();
//        String breedString = mBreedEditText.getText().toString().trim();
//        String weightString = mWeightEditText.getText().toString().trim();
//
//        if (currentBookUri == null &&
//                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) &&
//                TextUtils.isEmpty(weightString) && mGender == PetEntry.GENDER_UNKNOWN)
//        {return;}
//
//        ContentValues petValues = new ContentValues();
//        petValues.put(PetEntry.COLUMN_PET_NAME, nameString);
//        petValues.put(PetEntry.COLUMN_PET_BREED, breedString);
//
//        int weight = 0;
//        if (!TextUtils.isEmpty(weightString)) {
//            weight = Integer.parseInt(weightString);
//        }
//        petValues.put(PetEntry.COLUMN_PET_WEIGHT, weight);
//        petValues.put(PetEntry.COLUMN_PET_GENDER, mGender);
//
//        if (currentPetUri == null) {
//            Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, petValues);
//            // Show a toast message depending on whether or not the insertion was successful.
//            if (newUri == null) {
//                // If the new content URI is null, then there was an error with insertion.
//                Toast.makeText(this, getString(R.string.insert_pet_failed_toast),
//                        Toast.LENGTH_SHORT).show();
//            } else {
//                // Otherwise, the insertion was successful and we can display a toast.
//                Toast.makeText(this, getString(R.string.inserting_pet_success),
//                        Toast.LENGTH_SHORT).show();
//            }
//
//        } else {
//            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
//            // and pass in the new ContentValues. Pass in null for the selection and selection args
//            // because mCurrentPetUri will already identify the correct row in the database that
//            // we want to modify.
//            int rowsAffected = getContentResolver().update(currentPetUri, petValues, null, null);
//
//            // Show a toast message depending on whether or not the update was successful.
//            if (rowsAffected == 0) {
//                // If no rows were affected, then there was an error with the update.
//                Toast.makeText(this, getString(R.string.update_pet_fail),
//                        Toast.LENGTH_SHORT).show();
//            } else {
//                // Otherwise, the update was successful and we can display a toast.
//                Toast.makeText(this, getString(R.string.update_pet_success),
//                        Toast.LENGTH_SHORT).show();
//            }
        }
    }
}
