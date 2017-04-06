package cn.forward.androids.Image;

import android.view.View;

/**
 * Created by huangziwei on 2017/3/29.
 */

public interface ImageLoader {

    /**
     * @param view
     * @param path 图片的路径
     * @param config 图片加载的路径
     * @return 返回true表示接受加载任务
     */
    boolean load(View view, String path, ImageLoaderConfig config);
}
