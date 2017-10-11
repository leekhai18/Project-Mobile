package com.example.remembergroup.model;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by chauvansang on 9/25/2017.
 */

public class Helper {
    private static final Helper ourInstance = new Helper();

    public static Helper getInstance() {
        return ourInstance;
    }


    //Get the thumbnail image
    private void dispatchTakePictureIntent(Activity activity,String mCurrentPhotoPath) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(activity,mCurrentPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, Constant.REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile(Activity activity,String mCurrentPhotoPath) throws IOException {
        // Create an image file name
        String timeStamp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String dateToString(Date date)
    {
        Calendar calendar=Calendar.getInstance();
        Date temp=calendar.getTime();
        SimpleDateFormat format;
        if(temp.getYear()==date.getYear())
        {
            if (temp.getMonth()==date.getMonth())
            {

                if(temp.getDate()==date.getDate())
                {
                    format=new SimpleDateFormat("K:mm a");
                }
                else
                {
                    format=new SimpleDateFormat("EEE, K:mm a");
                }
            }
            else
            {
                format=new SimpleDateFormat("dd MMMM, K:mm a");
            }
        }

        return null;
    }
    private Helper() {
    }
}
