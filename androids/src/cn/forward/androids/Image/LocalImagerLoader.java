package cn.forward.androids.Image;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import cn.forward.androids.SimpleAsyncTask;
import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.utils.Util;

import static cn.forward.androids.utils.ImageUtils.computeBitmapSimple;

/**
 * Created by huangziwei on 2017/3/29.
 */

public class LocalImagerLoader implements ImageLoader {
    private Context mContext;

    public LocalImagerLoader(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public boolean load(final View view, final String path, ImageLoaderConfig config) {
        if (config == null) {
            throw new RuntimeException("ImageLoaderConfig is null!");
        }

        if (!path.startsWith("/") && !path.startsWith("assets/")) {
            return false;
        }

        int[] size = ImageUtils.optimizeMaxSizeByView(view, config.getMaxWidth(), config.getMaxHeight());
        final int width = size[0];
        final int height = size[1];
        final String key = "" + path + "=" + width + "_" + height;

        if (config.isNeedCache()) {
            // 从内存中获取
            Bitmap bitmap = config.getImageCache().getBimapMemoryCache(key);
            if (bitmap != null) {
                config.getImageSetter().setImage(view, bitmap);
                return true;
            }
        }

        if (cancelUselessImageLoadTask(view, path, config) == null) {
            ImageLoadTask task = new ImageLoadTask(view, path, width, height, config, key);
            config.getImageSetter().setImage(view, new AsyncDrawable(config.getLoadingDrawable(), task));
            task.executePriority(config.getPriority());
        }

        return true;
    }

    public static Bitmap getBitmapFromDisk(FileDescriptor fileDescriptor, int maxWidth, int maxHeight, ImageCache imageCache, String key, Bitmap.CompressFormat format) {

        Bitmap bitmap = null;
        //获取缓存的图片
        if (imageCache != null) {
            //从本地缓存中获取
            bitmap = imageCache.getBitmapDiskCache(key);
            if (bitmap != null) {
                return bitmap;
            }
        }

        BitmapFactory.Options options = null;
        //获取图片缩略图
        try {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            int h = options.outHeight;
            int w = options.outWidth;
            options.inSampleSize = computeBitmapSimple(w * h, maxWidth * maxHeight);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inInputShareable = true;
            options.inPurgeable = true;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        } catch (Throwable e) {
            LogUtil.i("get bitmap error");
            return null;
        }
        //缓存图片
        if (imageCache != null && bitmap != null) {
            //缓存到本地
            //根据后缀名判断文件类型
            imageCache.saveBitmapDiskCache(bitmap, key, format);
        }
        return bitmap;
    }

    private static class ImageLoadTask extends SimpleAsyncTask<String, Integer, Bitmap> {

        private WeakReference<View> mViewRef;
        private String mPath;
        private int mMaxWidth, mMaxHeight;
        private ImageLoaderConfig mConfig;
        private String mKey;

        public ImageLoadTask(View view, String path, int maxWidth, int maxHeight, ImageLoaderConfig config, String key) {
            mViewRef = new WeakReference<View>(view);
            mPath = path;
            mMaxWidth = maxWidth;
            mMaxHeight = maxHeight;
            mConfig = config;
            mKey = key;
        }

        private boolean abort() {
            if (mViewRef.get() == null || isCancelled()) {
                return true;
            }
            return false;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (abort()) {
                return null;
            }
            try {
                View view = mViewRef.get();
                if (view == null) {
                    return null;
                }

                FileInputStream fileInputStream = null;
                try {
                    if (mPath.startsWith("/")) {
                        fileInputStream = new FileInputStream(mPath);
                    } else if (mPath.startsWith("assets/")) {
                        AssetFileDescriptor assetFileDescriptor = view.getContext().getAssets().openFd(mPath.substring(7, mPath.length()));
                        fileInputStream = assetFileDescriptor.createInputStream();
                    }
                    Bitmap bm = getBitmapFromDisk(fileInputStream.getFD(), mMaxWidth, mMaxHeight, mConfig.isNeedCache() ? mConfig.getImageCache() : null, mKey,
                            mPath.toLowerCase().endsWith(".png") ?
                                    Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG);

                    if (abort()) {
                        return null;
                    }

                    if (bm != null) {
                        if (mConfig.isAutoRotate()) {
                            //旋转图片
                            bm = ImageUtils.rotateBitmapByExif(bm, mPath, true);
                        }
                        if (mConfig.isNeedCache()) {
                            mConfig.getImageCache().saveBitmapMemoryCache(bm, mKey);
                        }
                    }
                    return bm;
                } catch (Throwable e) {
                    LogUtil.e("open file failed:" + mPath);
                } finally {
                    Util.closeQuietly(fileInputStream);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            View view = mViewRef.get();
            if (view != null && bitmap != null) {
                mConfig.getImageSetter().setImage(view, bitmap);
            } else {
                mConfig.getImageSetter().setImage(view, mConfig.getLoadFailedDrawable());
            }
        }

        public View getView() {
            return mViewRef.get();
        }

        public String getPath() {
            return mPath;
        }

        public int getMaxWidth() {
            return mMaxWidth;
        }

        public int getMaxHeight() {
            return mMaxHeight;
        }

        public ImageLoaderConfig getConfig() {
            return mConfig;
        }

        public String getKey() {
            return mKey;
        }
    }

    /**
     * 取消无用的任务，并返回已存在的相同的任务
     *
     * @param view
     * @param key
     * @param config
     * @return 返回已经存在的相同的任务
     */
    private static ImageLoadTask cancelUselessImageLoadTask(View view, String key, ImageLoaderConfig config) {
        final ImageLoadTask oldLoadTask = getLoadTaskFromContainer(view, config.getImageSetter());

        if (oldLoadTask != null) {
            final String oldKey = oldLoadTask.getKey();
            if (!TextUtils.isEmpty(oldKey) && oldKey.equals(key)) {
                return oldLoadTask; // exist
            } else {
                oldLoadTask.cancel(true);
                return null;
            }
        }
        return null;
    }

    private static ImageLoadTask getLoadTaskFromContainer(View view, ImageLoaderConfig.ImageSetter setter) {
        if (view != null) {
            final Drawable drawable = setter.getDrawable(view);
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                SimpleAsyncTask task = asyncDrawable.getBitmapWorkerTask();
                if (task instanceof ImageLoadTask) {
                    return (ImageLoadTask) task;
                }
            }
        }
        return null;
    }
}
