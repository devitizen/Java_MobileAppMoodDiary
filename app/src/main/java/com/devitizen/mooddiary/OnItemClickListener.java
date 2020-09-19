package com.devitizen.mooddiary;

import android.view.View;

/**
 * Handles the event when the item in the list is selected by users.
 */
public interface OnItemClickListener {
    void onItemClick(NoteAdapter.ViewHolder holder, View view, int position);
}
