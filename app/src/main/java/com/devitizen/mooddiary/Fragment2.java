package com.devitizen.mooddiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.channguyen.rsv.RangeSliderView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Creates the New menu that sets the current date, current location, current weather.
 * Users can input some text and an image. For the image, a dialog will be popped up to select
 * to take a picture or choose from the album. In the last part, mood can be chosen by the users.
 */
public class Fragment2 extends Fragment implements OnBackPressedListener {

    private static final String TAG = "Fragment2";
    private boolean isPhotoCaptured, isPhotoFileSaved;
    private int selectedPhotoMenu;
    private int mMode = AppConstants.MODE_INSERT;
    private int moodIndex = 2;
    private String weather;

    private TextView dateTextView, locationTextView, weatherDescriptionTextView, numLettersTextView;
    private EditText contentsInput;
    private ImageView pictureImageView, weatherIconImageView;

    private MainActivity mainActivity;
    private Note note;

    private Bitmap resultPhotoBitmap;
    private RangeSliderView moodSliderView;
    private Context context;
    private File file;              // photo file captured


    /**
     * Creates views
     *
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState saved instance state
     * @return view group
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        // creates views
        init(viewGroup);

        // whether to create new note or to load the selected one
        if(note == null) {
            mMode = AppConstants.MODE_INSERT;   // creates new note (mMode = 1)
            mainActivity.getCurrentInfo();
        } else {
            mMode = AppConstants.MODE_MODIFY;   // loads the selected note (mMode = 2)
            loadDataToNote();
        }

        // counts the length of letters the users input
        textWatcher();

        return viewGroup;
    }

    /**
     * Attaches to the activity
     *
     * @param context context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Detaches from the activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if(context != null) {
            context = null;
        }

        // deletes temporary file captured when photo taken
        if(file !=null) {
            deleteTempFile(file.toString());
        }
    }

    /**
     * Resumes on the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        mainActivity.setOnBackPressedListener(this);
    }

    /**
     * Executes when back button is pressed
     */
    @Override
    public void onBackButtonPressed() {
        mainActivity.onMenuSelected(1);
    }


    /**
     * Creates views in onCreateView
     *
     * @param viewGroup view group
     */
    private void init(final ViewGroup viewGroup) {
        mainActivity = (MainActivity) getActivity();

        weatherIconImageView = viewGroup.findViewById(R.id.weatherIcon);
        dateTextView = viewGroup.findViewById(R.id.dateTextView);
        locationTextView = viewGroup.findViewById(R.id.locationTextView);
        weatherDescriptionTextView = viewGroup.findViewById(R.id.weatherDescription);
        numLettersTextView = viewGroup.findViewById(R.id.numLettersTextView);
        contentsInput = viewGroup.findViewById(R.id.contentsInput);

        // sets the button
        Button doneButton = viewGroup.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onHideKeypad();
            }
        });

        // sets the image view
        pictureImageView = viewGroup.findViewById(R.id.pictureImageView);
        pictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPhotoCaptured || isPhotoFileSaved) {
                    showDialog(AppConstants.CONTENT_PHOTO_EX);
                } else {
                    showDialog(AppConstants.CONTENT_PHOTO);
                }
            }
        });

        // sets the slider view
        moodSliderView = viewGroup.findViewById(R.id.sliderView);
        RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                moodIndex = index;
            }
        };
        moodSliderView.setOnSlideListener(listener);
        moodSliderView.setInitialIndex(2);

        ImageView image_mood01 = viewGroup.findViewById(R.id.image_mood01);
        image_mood01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodSliderView.setInitialIndex(0);
                moodIndex = 0;
            }
        });
        ImageView image_mood02 = viewGroup.findViewById(R.id.image_mood02);
        image_mood02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodSliderView.setInitialIndex(1);
                moodIndex = 1;
            }
        });
        ImageView image_mood03 = viewGroup.findViewById(R.id.image_mood03);
        image_mood03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodSliderView.setInitialIndex(2);
                moodIndex = 2;
            }
        });
        ImageView image_mood04 = viewGroup.findViewById(R.id.image_mood04);
        image_mood04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodSliderView.setInitialIndex(3);
                moodIndex = 3;
            }
        });
        ImageView image_mood05 = viewGroup.findViewById(R.id.image_mood05);
        image_mood05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodSliderView.setInitialIndex(4);
                moodIndex = 4;
            }
        });

    }

    /**
     * Alert dialog for pictures
     *
     * @param id whether the existing picture is located
     */
    public void showDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.alert_Message_02);

        selectedPhotoMenu = 0;
        switch (id) {

            // inserts new note
            case AppConstants.CONTENT_PHOTO:
                builder.setSingleChoiceItems(R.array.array_photo, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedPhotoMenu = which;
                    }
                });
                builder.setPositiveButton(R.string.alert_Button_03, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedPhotoMenu == 0) {
                            capturePhotoActivity();                 // takes a picture
                        } else if(selectedPhotoMenu == 1) {
                            selectPhotoActivity();                  // selects a picture
                        }
                    }
                });
                builder.setNegativeButton(R.string.alert_Button_04, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                break;

            // modifies the existing note
            case AppConstants.CONTENT_PHOTO_EX:
                builder.setSingleChoiceItems(R.array.array_photo_ex, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedPhotoMenu = which;
                    }
                });
                builder.setPositiveButton(R.string.alert_Button_03, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedPhotoMenu == 0) {
                            capturePhotoActivity();             // takes a picture
                        } else if(selectedPhotoMenu == 1) {
                            selectPhotoActivity();              // selects a picture
                        } else if(selectedPhotoMenu == 2) {
                            deletePhoto();                      // deletes the picture
                        }
                    }
                });
                builder.setNegativeButton(R.string.alert_Button_04, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                break;

            default:
                break;
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Getter for mMode (insert or modify the note)
     *
     * @return mMode
     */
    public int getMMode() {
        return mMode;
    }

    /**
     * Setter for mMode
     *
     * @param mMode whether to insert or to modify
     */
    public void setMMode(int mMode) {
        this.mMode = mMode;
    }

    /**
     * Setter for isPhotoFileSaved (whether a saved photo exists)
     *
     * @param photoFileSaved photoFileSaved
     */
    public void setPhotoFileSaved(boolean photoFileSaved) {
        isPhotoFileSaved = photoFileSaved;
    }

    /**
     * Setter for note
     *
     * @param note note
     */
    public void setNote(Note note) {
        this.note = note;
    }

    /**
     * Takes a picture, and saves it by the content provider
     * Going to get the result by onActivityResult followed
     */
    public void capturePhotoActivity() {
        if(file == null) {
            File storageDir = Environment.getExternalStorageDirectory();
            String filename = "capture.jpg";
            file = new File(storageDir, filename);
        }

        Uri fileUri = FileProvider.getUriForFile(context,"com.devitizen.mooddiary.fileprovider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if(intent.resolveActivity(context.getPackageManager()) != null ) {
            startActivityForResult(intent, AppConstants.REQ_PHOTO_CAPTURE);
        }
    }

    /**
     * Selects a picture
     * Going to get the result by onActivityResult followed
     */
    public void selectPhotoActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, AppConstants.REQ_PHOTO_SELECTION);
    }

    /**
     * Deletes the picture
     */
    public void deletePhoto() {
        resultPhotoBitmap = null;
        pictureImageView.setImageResource(R.drawable.imagetoset);
        isPhotoFileSaved = false;
        isPhotoCaptured = false;
    }

    /**
     * Deletes the temporary file stored when photo captured
     *
     * @param filePath file path
     */
    public void deleteTempFile(String filePath) {
        File file = new File(filePath);
        boolean deleted = file.delete();
        println("temp file deleted : " + deleted);
    }

    /**
     * Processes the response from showPhotoCaptureActivity and showPhotoSelectionActivity
     *
     * @param requestCode request code
     * @param resultCode result code
     * @param intent intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {

            // Take a picture
            case AppConstants.REQ_PHOTO_CAPTURE:
                resultPhotoBitmap = decodeSampledBitmapFromResource(file,
                                         pictureImageView.getWidth(), pictureImageView.getHeight());
                pictureImageView.setImageBitmap(resultPhotoBitmap);
                isPhotoCaptured = true;
                break;

            // Select a picture
            case AppConstants.REQ_PHOTO_SELECTION:
                if (intent != null) {
                    Uri selectedImage = intent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn,
                                                   null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    resultPhotoBitmap = decodeSampledBitmapFromResource(new File(filePath),
                                         pictureImageView.getWidth(), pictureImageView.getHeight());
                    pictureImageView.setImageBitmap(resultPhotoBitmap);
                    isPhotoFileSaved = true;
                }
                break;

            default:
                break;
        }
    }

    /**
     * Fetches from the database, and sets data to the note
     * Sets an initial value
     */
    public void loadDataToNote() {
        String noteDate = note.getCreateDateStr();
        String outDate = "";
        try {
            Date inDate = AppConstants.dateFormat4.parse(noteDate);
            outDate = AppConstants.dateFormat6.format(inDate);

            if(Locale.getDefault().getLanguage().equals("ko")) {
                outDate = AppConstants.dateFormat9.format(inDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setDateString(outDate);                                 // date

        setWeatherIcon(note.getWeather());                      // weather icon
        setWeatherDescription(note.getWeatherDescription());    // weather description
        setAddress(note.getAddress());                          // address
        setContents(note.getContents());                        // contents
        setMood(note.getMood());                                // mood

        String picturePath = note.getPicture();
        if(picturePath == null || picturePath.equals("")) {
            pictureImageView.setImageResource(R.drawable.imagetoset);
        } else {
            setPicture(note.getPicture(), 1);         // picture
        }

        setNumLetters(note.getContents());                      // number of letters in contents
    }

    /**
     * Sets length of letter
     *
     * @param str the letters entered
     */
    public void setNumLetters(String str) {
        try {
            byte[] bytes = str.getBytes("KSC5601");
            numLettersTextView.setText(bytes.length + " / 180 bytes");
        } catch(UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets textWatcher to count the number of letters
     */
    public void textWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                setNumLetters(str);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                try {
                    byte[] bytes = str.getBytes("KSC5601");
                    if(bytes.length > 180) {
                        s.delete(s.length()-2, s.length()-1);
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        // adds listener
        contentsInput.addTextChangedListener(watcher);
    }

    /**
     * Sets weather icon
     *
     * @param data weather icon id
     */
    public void setWeatherIcon(String data) {
        if(data != null) {
            try {
                Uri iconUri = Uri.parse("android.resource://com.devitizen.mooddiary/drawable/w" + data);
                weatherIconImageView.setImageURI(iconUri);
                weather = data;
            } catch (Exception e) {
                println("Unknown weather index : " + data);
                e.getStackTrace();
            }
        }
    }

    /**
     * Sets weather description
     *
     * @param data weather description
     */
    public void setWeatherDescription(String data) {
        if(data != null) {
            try {
                weatherDescriptionTextView.setText(data);
            } catch (Exception e) {
                println("Unknown weather index : " + data);
                e.getStackTrace();
            }
        }
    }

    /**
     * Sets address
     *
     * @param data address
     */
    public void setAddress(String data) {
        locationTextView.setText(data);
    }

    /**
     * Sets date
     *
     * @param dateString date
     */
    public void setDateString(String dateString) {
        dateTextView.setText(dateString);
    }

    /**
     * Sets contents
     *
     * @param data contents entered
     */
    public void setContents(String data) {
        contentsInput.setText(data);
    }

    /**
     * Sets mood
     *
     * @param mood mood
     */
    public void setMood(String mood) {
        moodIndex = Integer.parseInt(mood);
        moodSliderView.setInitialIndex(moodIndex);
    }

    /**
     * Sets picture
     *
     * @param picturePath picture path
     * @param sampleSize sampled size
     */
    public void setPicture(String picturePath, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        resultPhotoBitmap = BitmapFactory.decodeFile(picturePath, options);

        // in case the file doesn't exist in the storage
        if(resultPhotoBitmap == null) {
            deletePhoto();                                          // treat as deletion of photo
            modifyNote();                                           // update database
        } else {
            pictureImageView.setImageBitmap(resultPhotoBitmap);     // set image to the view
        }
    }

    /**
     * Called when taking a picture or selecting the existing picture
     * This refers to the example in Google Android site
     *
     * @param file picture file path
     * @param reqWidth width of required size
     * @param reqHeight height of required size
     * @return sampled bitmap
     */
    public Bitmap decodeSampledBitmapFromResource(File file, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // Produce bitmap
        Bitmap sampledBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if(sampledBitmap == null) {
            sampledBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imagetoset);
        }

        return rotateBitmap(file.getAbsolutePath(), sampledBitmap);
    }

    /**
     * Calculates the size to sample the picture
     * This refers to the example in Google Android site
     *
     * @param options BitmapFactory option
     * @param reqWidth width of required size
     * @param reqHeight height of required size
     * @return size to sample the picture
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Rotate the bitmap
     *
     * @param path picture file path
     * @param bitmap bitmap to be rotated
     * @return rotated bitmap
     */
    public Bitmap rotateBitmap(String path, Bitmap bitmap) {
        ExifInterface exifInterface;
        int orientation = 0;
        try {
            exifInterface = new ExifInterface(path);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                                        ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:
                break;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Adds a record to database
     */
    public void saveNote() {
        String weatherDescription = weatherDescriptionTextView.getText().toString();
        String address = locationTextView.getText().toString();
        String contents = contentsInput.getText().toString();
        String picturePath = savePicture();

        if(!contents.equals("") || !picturePath.equals("")) {
            if(contents.contains("'")) {
                contents = contents.replaceAll("'", "''");
            }
            String sql = "insert into " + AppConstants.TABLE_NOTE
                    + "(WEATHER, WEATHERDESCRIPTION, ADDRESS, LOCATION_X, LOCATION_Y, CONTENTS, MOOD, PICTURE) values("
                    + "'" + weather + "' ,"
                    + "'" + weatherDescription + "' ,"
                    + "'" + address + "', "
                    + "'" + "" + "', "
                    + "'" + "" + "', "
                    + "'" + contents + "', "
                    + "'" + moodIndex + "', "
                    + "'" + picturePath + "') ";
            println("sql : " + sql);

            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
            Toast.makeText(context, R.string.msg01, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.msg02, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Modifies the selected record in database
     */
    public void modifyNote() {
        if(note != null) {
            String weatherDescription = weatherDescriptionTextView.getText().toString();
            String address = locationTextView.getText().toString();
            String contents = contentsInput.getText().toString();
            String picturePath = savePicture();

            if(contents.contains("'")) {
                contents = contents.replaceAll("'", "''");
            }
            String sql = "update " + AppConstants.TABLE_NOTE
                    + " set "
                    + " WEATHER = '" + weather + "' ,"
                    + " WEATHERDESCRIPTION = '" + weatherDescription + "' ,"
                    + " ADDRESS = '" + address + "', "
                    + " LOCATION_X = '" + "" + "', "
                    + " LOCATION_Y = '" + "" + "', "
                    + " CONTENTS = '" + contents + "', "
                    + " MOOD = '" + moodIndex + "', "
                    + " PICTURE = '" + picturePath + "' "
                    + "where "
                    + " _id = " + note.get_id();
            println("sql : " + sql);

            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
            Toast.makeText(context, R.string.msg03, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Deletes a record from database
     */
    public void deleteNote() {
        if(note != null) {
            String sql = "delete from " + AppConstants.TABLE_NOTE
                    + " where "
                    + " _id = " + note.get_id();
            println("sql : " + sql);

            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
            Toast.makeText(context, R.string.msg04, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Saves picture by setting the location, the name, the format, and the quality
     *
     * @return picture path
     */
    private String savePicture() {
        if(resultPhotoBitmap == null) {
            return "";
        }

        // Set location for the picture given
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        AppConstants.FOLDER_PHOTO = sdcardPath + File.separator + "mood_diary" ;
        File photoFolder = new File(AppConstants.FOLDER_PHOTO);
        if(!photoFolder.isDirectory()) {
            photoFolder.mkdir();
        }

        // Set file name for database
        Date curDate = new Date();
        String photoFilename = AppConstants.dateFormat4.format(curDate.getTime()) + ".jpg";

        // Set full path
        String picturePath = photoFolder + File.separator + photoFilename;

        // Save the picture
        try {
            FileOutputStream outputStream = new FileOutputStream(picturePath);
            resultPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return picturePath;
    }

    /**
     * Prints log
     *
     * @param msg input message
     */
    public void println(String msg) {
        Log.d(TAG, msg);
    }

}
