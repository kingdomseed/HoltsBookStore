package com.holtnet.holtsbookstore;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return null;
        // return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
/*      TextView petNameTextView = view.findViewById(R.id.name);
        TextView summaryTextView = view.findViewById(R.id.summary);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex((PetContract.PetEntry.COLUMN_PET_NAME));
        int breedColumnIndex = cursor.getColumnIndex((PetContract.PetEntry.COLUMN_PET_BREED));

        // Read the pet attributes from the Cursor for the current pet
        String petName = cursor.getString(nameColumnIndex);
        String petBreed = cursor.getString(breedColumnIndex);

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(petBreed)) {
            petBreed = context.getString(R.string.unknown_breed);
        }

        petNameTextView.setText(petName);
        summaryTextView.setText(petBreed);*/
    }
}
