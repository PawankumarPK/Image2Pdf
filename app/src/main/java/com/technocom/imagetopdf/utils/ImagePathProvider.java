package com.technocom.imagetopdf.utils;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by janesh on 6/22/2018 : 4:50 PM.
 */

public class ImagePathProvider {

    /**
     * Getting All Images Path.
     *
     * @param context the activity
     * @return ArrayList with images Path
     */
    public static ArrayList<String> getAllImagesPath(Context context) {
        ArrayList<String> listOfAllImages = new ArrayList<>();
        listOfAllImages.addAll(getExternalImagesPath(context));
        listOfAllImages.addAll(getInternalImagesPath(context));
        return listOfAllImages;
    }

    /**
     * Getting All External Images Path.
     *
     * @param context the context
     * @return ArrayList with external images Path
     */
    private static ArrayList<String> getExternalImagesPath(Context context) {
        return getImagesPathFromUri(context, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    /**
     * Getting All Internal Images Path.
     *
     * @param context the context
     * @return ArrayList with internal images Path
     */
    private static ArrayList<String> getInternalImagesPath(Context context) {
        return getImagesPathFromUri(context, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    }

    private static ArrayList<String> getImagesPathFromUri(Context context, Uri uri) {
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                listOfAllImages.add(absolutePathOfImage);
            }
            cursor.close();
        }
        return listOfAllImages;
    }
}