package cn.forward.androids.Image;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import cn.forward.androids.Priority;

/**
 * Created by huangziwei on 2017/3/29.
 */

public class ImageLoaderConfig {

    private static ImageLoaderConfig sDefaultConfig = new ImageLoaderConfig(null);
    private static ImageSetter sDefaultImageSetter = new ImageSetter();
    private static final ImageCacheKeyGenerator DEFAULT_KEY_GENERATOR = new ImageCacheKeyGenerator();

    private ImageCache mImageCache;
    private int mMaxWidth;
    private int mMaxHeight;

    private boolean mNeedCache = true;
    private boolean mLoadOriginal;
    private boolean mAutoRotate; // rotate by exif info

    /**
     * 加载成功显示图片时的动画
     */
    private Animation mAnimation;
    private Drawable mLoadingDrawable;
    private Drawable mLoadFailedDrawable;

    private Bitmap.Config bitmapConfig = Bitmap.Config.RGB_565;

    private ImageSetter mImageSetter = sDefaultImageSetter;

    private Priority mPriority = Priority.DEFAULT;

    /**
     * 是否裁剪缩列图
     */
    private boolean mExtractThumbnail = false;

    private ImageCacheKeyGenerator mCacheKeyGenerator = DEFAULT_KEY_GENERATOR;

    public static ImageSetter getDefaultImageSetter() {
        return sDefaultImageSetter;
    }

    public static void setDefaultImageSetter(ImageSetter defaultImageSetter) {
        sDefaultImageSetter = defaultImageSetter;
    }


    public ImageLoaderConfig() {
        this(null);
    }

    public ImageLoaderConfig(ImageCache imageCache) {
        mImageCache = imageCache;
    }


    public int getMaxWidth() {
        return mMaxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        mMaxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return mMaxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        mMaxHeight = maxHeight;
    }

    public boolean isNeedCache() {
        if (mImageCache == null) {
            return false;
        }
        return mNeedCache;
    }

    public void setNeedCache(boolean needCache) {
        mNeedCache = needCache;
    }

    public boolean isLoadOriginal() {
        return mLoadOriginal;
    }

    public void setLoadOriginal(boolean loadOriginal) {
        mLoadOriginal = loadOriginal;
    }

    public boolean isAutoRotate() {
        return mAutoRotate;
    }

    public void setAutoRotate(boolean autoRotate) {
        mAutoRotate = autoRotate;
    }

    public Animation getAnimation() {
        return mAnimation;
    }

    public void setAnimation(Animation animation) {
        mAnimation = animation;
    }

    public Drawable getLoadingDrawable() {
        return mLoadingDrawable;
    }

    public void setLoadingDrawable(Drawable loadingDrawable) {
        mLoadingDrawable = loadingDrawable;
    }

    public Drawable getLoadFailedDrawable() {
        return mLoadFailedDrawable;
    }

    public void setLoadFailedDrawable(Drawable loadFailedDrawable) {
        mLoadFailedDrawable = loadFailedDrawable;
    }

    public Bitmap.Config getBitmapConfig() {
        return bitmapConfig;
    }

    public void setBitmapConfig(Bitmap.Config bitmapConfig) {
        this.bitmapConfig = bitmapConfig;
    }

    public static ImageLoaderConfig getDefaultConfig() {
        return sDefaultConfig;
    }

    public static void setDefaultConfig(ImageLoaderConfig defaultConfig) {
        sDefaultConfig = defaultConfig;
    }


    public void setImageSetter(ImageSetter imageSetter) {
        mImageSetter = imageSetter;
    }

    public ImageSetter getImageSetter() {
        return mImageSetter;
    }

    public ImageCache getImageCache() {
        return mImageCache;
    }

    public void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    public boolean isExtractThumbnail() {
        return mExtractThumbnail;
    }

    public void setExtractThumbnail(boolean extractThumbnail) {
        mExtractThumbnail = extractThumbnail;
    }

    public static class ImageSetter {
        public void setImage(View view, Bitmap bitmap) {
            if (view == null) {
                return;
            }
            if (view instanceof ImageView) {
                ((ImageView) view).setImageBitmap(bitmap);
            } else {
                view.setBackgroundDrawable(new BitmapDrawable(view.getResources(), bitmap));
            }
        }

        public void setImage(View view, Drawable drawable) {
            if (view == null) {
                return;
            }
            if (view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }

        public Drawable getDrawable(View view) {
            if (view == null) {
                return null;
            }
            if (view instanceof ImageView) {
                return ((ImageView) view).getDrawable();
            } else {
                return view.getBackground();
            }
        }
    }

    public ImageLoaderConfig clone() {
        ImageLoaderConfig config = new ImageLoaderConfig(mImageCache);
        config.setAnimation(getAnimation());
        config.setAutoRotate(isAutoRotate());
        config.setBitmapConfig(getBitmapConfig());
        config.setImageSetter(getImageSetter());
        config.setLoadFailedDrawable(getLoadFailedDrawable());
        config.setLoadingDrawable(getLoadingDrawable());
        config.setLoadOriginal(isLoadOriginal());
        config.setMaxHeight(getMaxHeight());
        config.setMaxWidth(getMaxWidth());
        config.setPriority(getPriority());
        config.setNeedCache(isNeedCache());
        config.setImageCache(getImageCache());
        config.setCacheKeyGenerator(getCacheKeyGenerator());
        config.setExtractThumbnail(isExtractThumbnail());
        return config;
    }

    public void setCacheKeyGenerator(ImageCacheKeyGenerator keyGenerator) {
        mCacheKeyGenerator = keyGenerator;
    }

    public ImageCacheKeyGenerator getCacheKeyGenerator() {
        return mCacheKeyGenerator;
    }

}
