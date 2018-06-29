package com.technocom.imagetopdf.utils;
/*
Created by Pawan kumar
 */


import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.technocom.imagetopdf.interfaces.ImagefileInterface;

import java.util.Stack;

public class BitmapPool {

    private final int width;
    private final int height;
    private final Bitmap.Config config;
    private final Stack<Bitmap> bitmaps = new Stack<Bitmap>();
    private boolean isRecycled;

    private final Handler handler = new Handler();

    /**
     * Construct a Bitmap pool with desired Bitmap parameters
     */
    public BitmapPool(int bitmapWidth,
                      int bitmapHeight,
                      Bitmap.Config config)
    {
        this.width = bitmapWidth;
        this.height = bitmapHeight;
        this.config = config;
    }

    /**
     * Destroy the pool. Any leased IManagedBitmap items remain valid
     * until they are recycled.
     */
    public void recycle() {
        isRecycled = true;
        for (Bitmap bitmap : bitmaps) {
            bitmap.recycle();
        }
        bitmaps.clear();
    }

    /**
     * Get a Bitmap from the pool or create a new one.
     * @return a managed Bitmap tied to this pool
     */
    public ImagefileInterface getBitmap() {
        return new LeasedBitmap(bitmaps.isEmpty()
                ? Bitmap.createBitmap(width, height, config) : bitmaps.pop());
    }

    private class LeasedBitmap implements ImagefileInterface {
        private int referenceCounter = 1;
        private final Bitmap bitmap;

        private LeasedBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @NonNull
        @Override
        public Bitmap getBitmap() {
            return bitmap;
        }

        @Override
        public void recycle() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (--referenceCounter == 0) {
                        if (isRecycled) {
                            bitmap.recycle();
                        } else {
                            bitmaps.push(bitmap);
                        }
                    }
                }
            });
        }

        @NonNull
        @Override
        public ImagefileInterface retain() {
            ++referenceCounter;
            return this;
        }
    }
}

