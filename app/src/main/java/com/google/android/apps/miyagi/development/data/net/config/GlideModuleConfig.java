package com.google.android.apps.miyagi.development.data.net.config;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by Lukasz on 04.01.2017.
 */

public class GlideModuleConfig implements GlideModule {

    private static int DEFAULT_DISK_CACHE_SIZE = 100 * 1024 * 1024;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder
                .setDiskCache(new InternalCacheDiskCacheFactory(context, DEFAULT_DISK_CACHE_SIZE));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
    }
}
