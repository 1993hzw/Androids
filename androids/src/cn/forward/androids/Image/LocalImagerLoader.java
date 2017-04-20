package cn.forward.androids.Image;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import cn.forward.androids.SimpleAsyncTask;
import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.utils.ThreadUtil;
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
    public boolean load(final View view, final String path, ImageLoaderConfig config, ImageLoaderListener loaderListener) {
        if (config == null) {
            throw new RuntimeException("ImageLoaderConfig is null!");
        }

        if (!path.startsWith("/") && !path.startsWith("assets/")) {
            return false;
        }

        int[] size = ImageUtils.optimizeMaxSizeByView(view, config.getMaxWidth(), config.getMaxHeight());
        final int width = size[0];
        final int height = size[1];
        final String key = config.isNeedCache() ?
                config.getCacheKeyGenerator().generateCacheKey(size, path, config) : null;

        if (config.isNeedCache()) {
            // 从内存中获取
            Bitmap bitmap = config.getImageCache().getBitmapMemoryCache(key);
            if (bitmap != null) {
                if (loaderListener != null) {
                    loaderListener.onLoadStarted(path, config);
                }

                if (view != null) {
                    config.getImageSetter().setImage(view, bitmap);
                }

                if (loaderListener != null) {
                    loaderListener.onLoadCompleted(path, config, bitmap);
                }
                return true;
            }
        }

        if (view != null) {
            // 绑定view的时候需要取消相同的任务
            if (cancelUselessImageLoadTask(view, path, config) == null) {
                ImageLoadTask task = new ImageLoadTask(mContext, view, path, width, height, config, key, loaderListener);
                config.getImageSetter().setImage(view, new AsyncDrawable(config.getLoadingDrawable(), task));
                task.executePriority(config.getPriority());
            } else {
                if (loaderListener != null) {
                    loaderListener.onLoadFailed(path, config, ImageLoaderListener.FAILED_TASK_EXISTS);
                }
            }
        } else {
            ImageLoadTask task = new ImageLoadTask(mContext, null, path, width, height, config, key, loaderListener);
            task.executePriority(config.getPriority());
        }

        return true;
    }

    @Override
    public boolean load(String path, ImageLoaderConfig config, ImageLoaderListener loaderListener) {
        return load(null, path, config, loaderListener);
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
            options.inSampleSize = computeBitmapSimple(w * h, maxWidth * maxHeight * 2);
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

    private static class ImageLoadTask extends SimpleAsyncTask<String, Object, Bitmap> {

        private static final int PROGRESS_LOAD_STARTED = 0;
        private static final int PROGRESS_LOADING = 1;

        private Context mContext;
        private WeakReference<View> mViewRef;
        private String mPath;
        private int mMaxWidth, mMaxHeight;
        private ImageLoaderConfig mConfig;
        private String mKey;
        private ImageLoaderListener mLoaderListener;

        public ImageLoadTask(Context context, View view, String path, int maxWidth, int maxHeight, ImageLoaderConfig config, String key, ImageLoaderListener loaderListener) {
            mContext = context.getApplicationContext();
            mViewRef = view == null ? null : new WeakReference<View>(view); // 为空表示不用绑定View
            mPath = path;
            mMaxWidth = maxWidth;
            mMaxHeight = maxHeight;
            mConfig = config;
            mKey = key;
            mLoaderListener = loaderListener;
        }

        private boolean abort() {
            if (mViewRef != null) {
                if (mViewRef.get() == null || isCancelled()) {
                    return true;
                }

                final ImageLoadTask oldLoadTask = getLoadTaskFromContainer(mViewRef.get(), mConfig.getImageSetter());
                if (this != oldLoadTask) { // 被其他任务替换
                    return true;
                }
            } else {
                if (isCancelled()) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (abort()) {
                return null;
            }
            try {
                start();

                FileInputStream fileInputStream = null;
                try {
                    if (mPath.startsWith("/")) {
                        fileInputStream = new FileInputStream(mPath);
                    } else if (mPath.startsWith("assets/")) {
                        AssetFileDescriptor assetFileDescriptor = mContext.getAssets().openFd(mPath.substring(7, mPath.length()));
                        fileInputStream = assetFileDescriptor.createInputStream();
                    }
                    Bitmap bm = getBitmapFromDisk(fileInputStream.getFD(),
                            mConfig.isLoadOriginal() ? 0 : mMaxWidth, mConfig.isLoadOriginal() ? 0 : mMaxHeight,
                            mConfig.isNeedCache() ? mConfig.getImageCache() : null, mKey,
                            mPath.toLowerCase().endsWith(".png") ?
                                    Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG);

                    if (abort()) {
                        return null;
                    }

                    if (bm != null) {
                        if (mConfig.isExtractThumbnail()) {
                            float scale;
                            if (bm.getWidth() < bm.getHeight()) {
                                scale = mMaxWidth / (float) bm.getWidth();
                            } else {
                                scale = mMaxHeight / (float) bm.getHeight();
                            }
                            if (scale <= 1) { // 缩小图片才需要裁剪
                                bm = ThumbnailUtils.extractThumbnail(bm, mMaxWidth, mMaxHeight,
                                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                            }
                        }

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
            if (abort()) {
                if (mLoaderListener != null) {
                    mLoaderListener.onLoadFailed(mPath, mConfig, ImageLoaderListener.FAILED_TASK_CANCELLED);
                }
                return;
            }
            if (mViewRef != null) { // 需要绑定view
                View view = mViewRef.get();
                if (view != null) {
                    if (bitmap != null) {
                        mConfig.getImageSetter().setImage(view, bitmap);
                        animationDisplay(view, mConfig.getAnimation());
                        if (mLoaderListener != null) {
                            mLoaderListener.onLoadCompleted(mPath, mConfig, bitmap);
                        }
                    } else {
                        mConfig.getImageSetter().setImage(view, mConfig.getLoadFailedDrawable());
                        if (mLoaderListener != null) {
                            mLoaderListener.onLoadFailed(mPath, mConfig, ImageLoaderListener.FAILED_BITMAP_ERROR);
                        }
                    }
                } else {
                    if (mLoaderListener != null) {
                        mLoaderListener.onLoadFailed(mPath, mConfig, ImageLoaderListener.FAILED_TASK_CANCELLED);
                    }
                }
            } else { // 不用绑定view
                if (bitmap != null) {
                    if (mLoaderListener != null) {
                        mLoaderListener.onLoadCompleted(mPath, mConfig, bitmap);
                    }
                } else {
                    if (mLoaderListener != null) {
                        mLoaderListener.onLoadFailed(mPath, mConfig, ImageLoaderListener.FAILED_BITMAP_ERROR);
                    }
                }
            }
        }

        private void start() {
            this.publishProgress(PROGRESS_LOAD_STARTED);
        }

        public void updateProgress(long total, long current) {
            this.publishProgress(PROGRESS_LOADING, total, current);
        }


        @Override
        protected void onProgressUpdate(Object... values) {
            if (mLoaderListener == null) {
                return;
            }
            switch ((Integer) values[0]) {
                case PROGRESS_LOAD_STARTED:
                    mLoaderListener.onLoadStarted(mPath, mConfig);
                    break;
                case PROGRESS_LOADING:
                    if (values.length != 3) return;
                    mLoaderListener.onLoading(mPath, mConfig, (Long) values[1], (Long) values[2]);
                    break;
                default:
                    break;
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

    private static void animationDisplay(View container, Animation animation) {
        if (container == null || animation == null) {
            return;
        }
        try {
            Method cloneMethod = Animation.class.getDeclaredMethod("clone");
            cloneMethod.setAccessible(true);
            container.startAnimation((Animation) cloneMethod.invoke(animation));
        } catch (Throwable e) {
            container.startAnimation(animation);
        }
    }
}
