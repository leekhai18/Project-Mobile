package com.example.remembergroup.chat_app;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by Khai Lee on 12/31/2017.
 */

public class MemoryManager {
    private static MemoryManager instance = new MemoryManager();
    public static MemoryManager getInstance() {return instance;}

    private LruCache<String, Bitmap> mMemoryCache;

    private MemoryManager(){
        addToolKeepMemory();
    }

    private void addToolKeepMemory() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
            mMemoryCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
