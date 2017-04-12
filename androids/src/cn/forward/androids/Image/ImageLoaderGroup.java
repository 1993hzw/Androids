package cn.forward.androids.Image;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by huangziwei on 2017/3/29.
 */
public class ImageLoaderGroup implements ImageLoader {
    private ImageCache mImageCache;
    private ImageLoaderConfig mImageLoaderConfig;

    private CopyOnWriteArrayList<ImageLoader> mImageLoaders;

    public ImageLoaderGroup(Context context) {
        this(context, (int) Runtime.getRuntime().maxMemory() / 8, 25 * 1024 * 1024);
    }

    public ImageLoaderGroup(Context context, int memoryMaxSize, long diskMaxSize) {
        this(context, null);
        mImageCache = new ImageCache(context, memoryMaxSize, diskMaxSize);
        mImageLoaderConfig = new ImageLoaderConfig(mImageCache);
    }

    public ImageLoaderGroup(Context context, ImageLoaderConfig config) {
        mImageLoaders = new CopyOnWriteArrayList<>();
        mImageLoaderConfig = config;
    }

    public void setImageLoaderConfig(ImageLoaderConfig imageLoaderConfig) {
        mImageLoaderConfig = imageLoaderConfig;
    }

    public ImageLoaderConfig getImageLoaderConfig() {
        return mImageLoaderConfig;
    }

    @Override
    public boolean load(final View view, String path, ImageLoaderConfig config, ImageLoaderListener loaderListener) {
        if (view == null || TextUtils.isEmpty(path)) {
            return false;
        }
        boolean accept = false;
        for (ImageLoader loader : mImageLoaders) {
            if (loader.load(view, path, config, loaderListener)) {
                accept = true;
                break;
            }
        }
        return accept;
    }

    @Override
    public boolean load(String path, ImageLoaderConfig config, ImageLoaderListener loaderListener) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean accept = false;
        for (ImageLoader loader : mImageLoaders) {
            if (loader.load(path, config, loaderListener)) {
                accept = true;
                break;
            }
        }
        return accept;
    }

    public boolean load(final View view, String path) {
        return load(view, path, mImageLoaderConfig, null);
    }

    public boolean load(String path, ImageLoaderListener loaderListener) {
        return load(path, mImageLoaderConfig, loaderListener);
    }

    public void addImageLoader(ImageLoader loader) {
        if (loader == null) {
            return;
        }
        mImageLoaders.add(loader);
    }

    public void removeImageLoader(ImageLoader loader) {
        if (loader == null) {
            return;
        }
        mImageLoaders.remove(loader);
    }

    public boolean containImageLoader(ImageLoader loader) {
        return mImageLoaders.contains(loader);
    }

    public void clearAllImageLoaders() {
        mImageLoaders.clear();
    }

    public ImageCache getImageCache() {
        return mImageCache;
    }
}
