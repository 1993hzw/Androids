package cn.forward.androids.Image;

/**
 * Created by huangziwei on 2017/4/10.
 */

public class ImageCacheKeyGenerator {
    /**
     * 缓存的key
     *
     * @param size   size:[宽，高】
     * @param path
     * @param config
     * @return
     */
    public String generateCacheKey(int[] size, final String path, ImageLoaderConfig config) {
        final int width = size[0];
        final int height = size[1];
        final String key = config.isLoadOriginal() ? path + "_" + config.isAutoRotate() + "_" + config.isExtractThumbnail()
                : "" + path + "=" + width + "_" + height + "_" + config.isAutoRotate() + "_" + config.isExtractThumbnail();
        return key;
    }
}
