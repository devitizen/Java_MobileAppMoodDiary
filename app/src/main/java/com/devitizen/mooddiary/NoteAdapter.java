package com.devitizen.mooddiary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.FactoryConfigurationError;

/**
 * This is a adapter to implement RecyclerView.
 * Also has a view holder as an inner class.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>
                         implements OnItemClickListener {

    private int layoutType;
    private ArrayList<Note> notes = new ArrayList<>();

    private OnItemClickListener itemClickListener;

    /**
     * Implements RecyclerView.Adapter by creating view holder
     *
     * @param parent view group
     * @param viewType view type
     * @return view holder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.note_item, parent, false);

        return new ViewHolder(itemView, this, layoutType);
    }

    /**
     * Implements RecyclerView.Adapter by binding the existing holder
     *
     * @param holder holder
     * @param position position on the holder
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.setItem(note);
        holder.setLayoutType(layoutType);
    }

    /**
     * Implements RecyclerView.Adapter by getting the number of items
     *
     * @return the number of items
     */
    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * Implements OnNoteItemClickListener
     *
     * @param holder holder
     * @param view view
     * @param position selected position in the holder
     */
    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(itemClickListener != null) {
            itemClickListener.onItemClick(holder, view, position);
        }
    }

    /**
     * Sets layoutType (contents vs. pictures)
     *
     * @param position index of layout type
     */
    public void switchLayout(int position) {
        layoutType = position;
    }

    /**
     * Gets note from the list
     *
     * @param position position of the note
     * @return selected note
     */
    public Note getItem(int position) {
        return notes.get(position);
    }

    /**
     * Sets notes
     *
     * @param notes notes
     */
    public void setItems(ArrayList<Note> notes) {
        this.notes = notes;
    }

    /**
     * Sets OnItemClickListener
     *
     * @param listener OnItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * ************************************************************
     * Inner Class : ViewHolder
     * ************************************************************
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout1, layout2;
        ImageView moodImageView, moodImageView2, pictureExistsImageView, pictureImageView,
                  weatherImageView, weatherImageView2;
        TextView  contentsTextView, contentsTextView2, locationTextView, locationTextView2,
                  dateTextView, dateTextView2;

        /**
         * Constructor
         *
         * @param itemView view
         * @param itemClickListener OnItemClickListener
         * @param layoutType layout type
         */
        public ViewHolder(@NonNull View itemView, final OnItemClickListener itemClickListener,
                                                  int layoutType) {
            super(itemView);

            layout1 = itemView.findViewById(R.id.layout1);
            layout2 = itemView.findViewById(R.id.layout2);
            moodImageView = itemView.findViewById(R.id.moodImageView);
            moodImageView2 = itemView.findViewById(R.id.moodImageView2);
            pictureExistsImageView = itemView.findViewById(R.id.pictureExistsImageView);
            pictureImageView = itemView.findViewById(R.id.pictureImageView);
            weatherImageView = itemView.findViewById(R.id.weatherImageView);
            weatherImageView2 = itemView.findViewById(R.id.weatherImageView2);
            contentsTextView = itemView.findViewById(R.id.contentsTextView);
            contentsTextView2 = itemView.findViewById(R.id.contentsTextView2);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            locationTextView2 = itemView.findViewById(R.id.locationTextView2);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            dateTextView2 = itemView.findViewById(R.id.dateTextView2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(itemClickListener != null) {
                        itemClickListener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });

            // sets layout
            setLayoutType(layoutType);
        }

        /**
         * Sets layout type
         *
         * @param layoutType index of layout type
         */
        public void setLayoutType(int layoutType) {
            if(layoutType == 0) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
            } else if(layoutType == 1) {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Sets the note with data loaded in Fragment1
         *
         * @param note selected note
         */
        public void setItem(Note note) {
            // sets contents
            contentsTextView.setText(note.getContents());
            contentsTextView2.setText(note.getContents());

            // sets address
            locationTextView.setText(note.getAddress());
            locationTextView2.setText(note.getAddress());

            // sets mood
            int moodIndex = Integer.parseInt(note.getMood());
            setMoodImage(moodIndex);

            // sets weather
            setWeatherImage(note.getWeather());

            // sets picture
            String picturePath = note.getPicture();
            if(picturePath != null && !picturePath.equals("")           // not the case path name doesn't exist
                    && BitmapFactory.decodeFile(picturePath) != null) { // not the case file doesn't exist
                pictureExistsImageView.setVisibility(View.VISIBLE);
                pictureImageView.setVisibility(View.VISIBLE);
                pictureExistsImageView.setImageURI(Uri.parse("file://" + picturePath));
                pictureImageView.setImageURI(Uri.parse("file://" + picturePath));
            } else {
                pictureExistsImageView.setVisibility(View.GONE);
                pictureImageView.setVisibility(View.GONE);
                pictureExistsImageView.setImageResource(R.drawable.imagetoset);
                pictureImageView.setImageResource(R.drawable.imagetoset);
            }

            // sets date with changed format (time, weekday, day, month)
            String noteDate = note.getCreateDateStr();
            String outDate = "";
            try {
                Date inDate = AppConstants.dateFormat4.parse(noteDate);
                outDate = AppConstants.dateFormat8.format(inDate);

                if(Locale.getDefault().getLanguage().equals("ko")) {
                    outDate = AppConstants.dateFormat11.format(inDate);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateTextView.setText(outDate);
            dateTextView2.setText(outDate);
        }

        /**
         * Sets mood image
         *
         * @param moodIndex index of mood
         */
        public void setMoodImage(int moodIndex) {
            switch (moodIndex) {
                case 0:
                    moodImageView.setImageResource(R.drawable.smile1_48);
                    moodImageView2.setImageResource(R.drawable.smile1_48);
                    break;
                case 1:
                    moodImageView.setImageResource(R.drawable.smile2_48);
                    moodImageView2.setImageResource(R.drawable.smile2_48);
                    break;
                case 2:
                    moodImageView.setImageResource(R.drawable.smile3_48);
                    moodImageView2.setImageResource(R.drawable.smile3_48);
                    break;
                case 3:
                    moodImageView.setImageResource(R.drawable.smile4_48);
                    moodImageView2.setImageResource(R.drawable.smile4_48);
                    break;
                case 4:
                    moodImageView.setImageResource(R.drawable.smile5_48);
                    moodImageView2.setImageResource(R.drawable.smile5_48);
                    break;
                default:
                    moodImageView.setImageResource(R.drawable.smile3_48);
                    moodImageView2.setImageResource(R.drawable.smile3_48);
                    break;
            }
        }

        /**
         * Sets weather image
         *
         * @param data weather icon id
         */
        public void setWeatherImage(String data) {
            if(data != null) {
                try {
                    Uri iconUri = Uri.parse("android.resource://com.devitizen.mooddiary/drawable/w" + data);
                    weatherImageView.setImageURI(iconUri);
                    weatherImageView2.setImageURI(iconUri);
                } catch (Exception e) {
                    Log.d("NoteAdapter", "Error in setting weather image.");
                    e.getStackTrace();
                }
            }
        }

    }

}
