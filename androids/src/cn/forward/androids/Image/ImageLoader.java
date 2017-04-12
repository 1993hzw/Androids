package cn.forward.androids.Image;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by huangziwei on 2017/3/29.
 */

public interface ImageLoader {

    /**
     * 把图片加载到指定的view
     *
     * @param view           绑定的view
     * @param path           图片的路径
     * @param config         图片加载的路径
     * @param loaderListener
     * @return 返回true表示接受加载任务
     */
    boolean load(View view, String path, ImageLoaderConfig config, ImageLoaderListener loaderListener);

    /**
     * @param path
     * @param config
     * @param loaderListener
     * @return
     */
    boolean load(String path, ImageLoaderConfig config, ImageLoaderListener loaderListener);


    public abstract class ImageLoaderListener {

        /**
         * 已存在相同的任务
         */
        public static final int FAILED_TASK_EXISTS = -1;

        /**
         * 任务被取消
         */
        public static final int FAILED_TASK_CANCELLED = -2;

        /**
         * 图片加载失败
         */
        public static final int FAILED_BITMAP_ERROR = -3;

        /**
         * @param path
         * @param config
         */
        public void onLoadStarted(String path, ImageLoaderConfig config) {
        }

        /**
         * @param path
         * @param config
         * @param total
         * @param current
         */
        public void onLoading(String path, ImageLoaderConfig config, long total, long current) {
        }

        /**
         * @param path
         * @param config * @param bitmap
         */
        public abstract void onLoadCompleted(String path, ImageLoaderConfig config, Bitmap bitmap);

        /**
         * @param path
         * @param config
         * @param reason
         */
        public abstract void onLoadFailed(String path, ImageLoaderConfig config, int reason);
    }
}
