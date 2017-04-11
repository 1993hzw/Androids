package cn.forward.androids.Image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.LruCache;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import cn.forward.androids.base.BaseApplication;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.utils.cache.DiskLruCache;

/**
 * Created by huangziwei on 2017/3/28.
 */

public class ImageCache {

    private int mMemoryCacheMaxSize;
    private long mDiskCacheMaxSize;
    private File mDiskCacheDir;

    private final Object mDiskCacheLock = new Object();
    private DiskLruCache diskLruCache;
    private LruCache<String, Bitmap> memoryLruCache;

    public ImageCache(Context context, int memoryCacheMaxSize, long diskCacheMaxSize) {
        this(context, memoryCacheMaxSize, diskCacheMaxSize, new File(getDiskCacheDir(context, "androidsCache")));
    }

    public ImageCache(Context context, int memoryCacheMaxSize, long diskCacheMaxSize, File diskCacheDir) {
        mMemoryCacheMaxSize = memoryCacheMaxSize;
        mDiskCacheMaxSize = diskCacheMaxSize;
        mDiskCacheDir = diskCacheDir;

        initMemoryCache();
        initDiskCache();
    }

    private void initMemoryCache() {
        memoryLruCache = new LruCache<String, Bitmap>(mMemoryCacheMaxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (value == null) {
                    return 0;
                }
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    private void initDiskCache() {
        synchronized (mDiskCacheLock) {
            if (diskLruCache != null && !diskLruCache.isClosed()) {
                return;
            }
            try {
                if (mDiskCacheDir.exists() || mDiskCacheDir.mkdirs()) {
                    diskLruCache = DiskLruCache.open(mDiskCacheDir, BaseApplication.VERSION_CODE, 1, mDiskCacheMaxSize);
                } else {
                    LogUtil.e("disk cache dir init failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //删除所有缩略图
    public void clearAllCache() {
        clearAllMemoryCache();
        clearAllDiskCache();
    }

    public void clearAllMemoryCache() {
        if (memoryLruCache == null) {
            return;
        }
        memoryLruCache.evictAll();
    }

    public void clearAllDiskCache() {
        synchronized (mDiskCacheLock) {
            if (diskLruCache == null) {
                return;
            }
            try {
                diskLruCache.delete();
                diskLruCache.close();
                diskLruCache = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        initDiskCache();
    }

    public void closeAllCache() {
        closeMemoryCache();
        closeDiskCache();
    }

    public void closeMemoryCache() {
        clearAllMemoryCache();
        memoryLruCache = null;
    }

    public void closeDiskCache() {
        synchronized (mDiskCacheLock) {
            if (diskLruCache == null) {
                return;
            }
            try {
                diskLruCache.close();
                diskLruCache = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从内存缓存中获取图片
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapMemoryCache(String key) {
        //从内存中获取
        if (memoryLruCache != null) {
            return memoryLruCache.get(key);
        }
        return null;
    }

    /**
     * 保存图片到内存缓存
     *
     * @param bitmap
     * @param key
     */
    public void saveBitmapMemoryCache(Bitmap bitmap, String key) {
        if (memoryLruCache != null && key != null) {
            memoryLruCache.put(key, bitmap);
        }
    }

    /**
     * 从本地缓存中获取图片
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            if (diskLruCache == null || key == null) {
                return null;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            DiskLruCache.Snapshot snapshot = null;
            try {
                snapshot = diskLruCache.get(key.hashCode() + "");
                if (snapshot != null) {
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inInputShareable = true;
                    options.inPurgeable = true;
                    return BitmapFactory.decodeStream(snapshot.getInputStream(0), null, options);
                }
            } catch (OutOfMemoryError error) {
                LogUtil.e("getBitmapDiskCache:OutOfMemory");
                try {
                    //如果内存溢出，则压缩图片
                    options.inSampleSize = 2;
                    return BitmapFactory.decodeStream(snapshot.getInputStream(0), null, options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void saveBitmapDiskCache(Bitmap bitmap, String key) {
        saveBitmapDiskCache(bitmap, key, Bitmap.CompressFormat.JPEG);
    }

    /**
     * 保存图片到本地缓存
     *
     * @param bitmap
     * @param key
     * @param format 保存格式
     */
    public void saveBitmapDiskCache(Bitmap bitmap, String key, Bitmap.CompressFormat format) {
        synchronized (mDiskCacheLock) {
            if (diskLruCache == null || key == null) {
                return;
            }
            try {
                DiskLruCache.Editor editor = diskLruCache.edit(key.hashCode() + "");
                if (editor != null) {
                    OutputStream out = editor.newOutputStream(0);
                    bitmap.compress(format, 90, out);
                    editor.commit();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从内存及本地缓存中获取图片
     *
     * @param key
     * @return
     */
    public Bitmap getBitmap(String key) {
        Bitmap bitmap = getBitmapMemoryCache(key);
        if (bitmap == null) {
            bitmap = getBitmapDiskCache(key);
            if (bitmap != null) {
                saveBitmapMemoryCache(bitmap, key);
            }
        }
        return bitmap;
    }

    /**
     * 把图片保存在内存及本地缓存中
     *
     * @param bitmap
     * @param format 本地缓存的图片格式
     */
    public void save(Bitmap bitmap, String key, Bitmap.CompressFormat format) {
        saveBitmapMemoryCache(bitmap, key);
        saveBitmapDiskCache(bitmap, key, format);
    }

    public void save(Bitmap bitmap, String key) {
        saveBitmapMemoryCache(bitmap, key);
        saveBitmapDiskCache(bitmap, key);
    }

    public void flushDiskCache() {
        synchronized (mDiskCacheLock) {
            if (diskLruCache != null) {
                try {
                    diskLruCache.flush();
                } catch (Throwable e) {
                    LogUtil.e(e.getMessage());
                }
            }
        }

    }

    public int getMemoryCacheMaxSize() {
        return mMemoryCacheMaxSize;
    }

    public void setMemoryCacheMaxSize(int size) {
        if (memoryLruCache != null) {
            memoryLruCache.resize(size);
        }
    }

    public long getDiskCacheMaxSize() {
        return mDiskCacheMaxSize;
    }

    public void setDiskCacheMaxSize(long size) {
        synchronized (mDiskCacheLock) {
            if (diskLruCache == null) {
                return;
            }
            diskLruCache.setMaxSize(size);
        }
    }

    public File getDiskCacheDir() {
        return mDiskCacheDir;
    }

    public Map<String, Bitmap> getSnapshotMemoryCache() {
        if (memoryLruCache == null) {
            return null;
        }
        return memoryLruCache.snapshot();
    }

    public static String getDiskCacheDir(Context context, String dirName) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            }
        }
        if (cachePath == null) {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                cachePath = cacheDir.getPath();
            }
        }

        return cachePath + File.separator + dirName;
    }



}
