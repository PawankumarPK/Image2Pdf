package com.technocom.camera;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by janesh on 6/30/2018 : 11:56 AM.
 */
public class SquareTransformation extends BitmapTransformation {

    private static final String ID = "com.technocom.camera.SquareTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);


    @Override
    protected Bitmap transform(
            @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int size = Math.min(toTransform.getWidth(), toTransform.getHeight());
        int x = (toTransform.getWidth() - size) / 2;
        int y = (toTransform.getHeight() - size) / 2;
        return Bitmap.createBitmap(toTransform, x, y, size, size);
    }


    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
