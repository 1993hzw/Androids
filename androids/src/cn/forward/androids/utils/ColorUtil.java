package cn.forward.androids.utils;

import android.graphics.Color;

/**
 * 颜色工具栏
 *
 * @author hzw
 * @date 2016/1/24.
 */
public class ColorUtil {

    /**
     * 计算渐变后的颜色
     *
     * @param startColor 开始颜色
     * @param endColor   结束颜色
     * @param rate       渐变率（0,1）
     * @return 渐变后的颜色，当rate=0时，返回startColor，当rate=1时返回endColor
     */
    public static int computeGradientColor(int startColor, int endColor, float rate) {
        if (rate < 0) {
            rate = 0;
        }
        if (rate > 1) {
            rate = 1;
        }

        int alpha = Color.alpha(endColor) - Color.alpha(startColor);
        int red = Color.red(endColor) - Color.red(startColor);
        int green = Color.green(endColor) - Color.green(startColor);
        int blue = Color.blue(endColor) - Color.blue(startColor);

        return Color.argb(
                Math.round(Color.alpha(startColor) + alpha * rate),
                Math.round(Color.red(startColor) + red * rate),
                Math.round(Color.green(startColor) + green * rate),
                Math.round(Color.blue(startColor) + blue * rate));
    }
}
