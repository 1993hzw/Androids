package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;

import cn.forward.androids.R;

/**
 * Created by huangziwei on 17-1-3.
 */
public class SelectorAttrs {

    public static final int RECTANGLE = 0;

    public static final int OVAL = 1;

    public static final int LINE = 2;

    public static final int RING = 3;

    /**
     * 直接在布局文件中直接设置selector
     *
     * @param context
     * @param view
     * @param attrs
     */
    public static void obtainsAttrs(Context context, View view, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Selector);
        GradientDrawable gradientDrawable = new GradientDrawable();
        GradientDrawable gradientDrawablePressed = new GradientDrawable();
        if (a.hasValue(R.styleable.Selector_sel_background)) { // 只有设置了mtv_background其他属性才生效
            int background = a.getColor(R.styleable.Selector_sel_background, -1);
            gradientDrawable.setColor(background);
            gradientDrawablePressed.setColor(background);

            int shape = a.getInt(R.styleable.Selector_sel_background_shape, RECTANGLE);
            if (shape == LINE) {
                gradientDrawable.setShape(GradientDrawable.LINE);
                gradientDrawablePressed.setShape(GradientDrawable.LINE);
            } else if (shape == OVAL) {
                gradientDrawable.setShape(GradientDrawable.OVAL);
                gradientDrawablePressed.setShape(GradientDrawable.OVAL);
            } else if (shape == RING) {
                gradientDrawable.setShape(GradientDrawable.RING);
                gradientDrawablePressed.setShape(GradientDrawable.RING);
            } else {
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawablePressed.setShape(GradientDrawable.RECTANGLE);
            }

            int backgroundBorderWidth = a.getDimensionPixelOffset(R.styleable.Selector_sel_background_border_width, -1);
            if (backgroundBorderWidth != -1) {
                gradientDrawable.setStroke(backgroundBorderWidth, background);
                gradientDrawablePressed.setStroke(backgroundBorderWidth, background);
            }
            int backgroundBorder = a.getColor(R.styleable.Selector_sel_background_border_color, -1);
            if (backgroundBorder != -1) {
                gradientDrawable.setStroke(backgroundBorderWidth, backgroundBorder);
                gradientDrawablePressed.setStroke(backgroundBorderWidth, backgroundBorder);
            }
            int backgroundCorners = a.getDimensionPixelOffset(R.styleable.Selector_sel_background_corners, -1);
            if (backgroundCorners != -1) {
                final int radius = backgroundCorners;
                gradientDrawable.setCornerRadius(backgroundCorners);
                gradientDrawablePressed.setCornerRadius(backgroundCorners);

                final int topLeftRadius = a.getDimensionPixelSize(
                        R.styleable.Selector_sel_background_corner_topLeft, radius);
                final int topRightRadius = a.getDimensionPixelSize(
                        R.styleable.Selector_sel_background_corner_topRight, radius);
                final int bottomLeftRadius = a.getDimensionPixelSize(
                        R.styleable.Selector_sel_background_corner_bottomLeft, radius);
                final int bottomRightRadius = a.getDimensionPixelSize(
                        R.styleable.Selector_sel_background_corner_bottomRight, radius);
                if (topLeftRadius != radius || topRightRadius != radius ||
                        bottomLeftRadius != radius || bottomRightRadius != radius) {
                    gradientDrawable.setCornerRadii(new float[]{
                            topLeftRadius, topLeftRadius,
                            topRightRadius, topRightRadius,
                            bottomRightRadius, bottomRightRadius,
                            bottomLeftRadius, bottomLeftRadius
                    });
                    gradientDrawablePressed.setCornerRadii(new float[]{
                            topLeftRadius, topLeftRadius,
                            topRightRadius, topRightRadius,
                            bottomRightRadius, bottomRightRadius,
                            bottomLeftRadius, bottomLeftRadius
                    });
                }
            }

            int backgroundPressed = a.getColor(R.styleable.Selector_sel_background_pressed, -1);
            if (backgroundPressed != -1) {
                gradientDrawablePressed.setColor(backgroundPressed);
            }
            int backgroundBorderPressed = a.getColor(R.styleable.Selector_sel_background_border_pressed, -1);
            if (backgroundBorderPressed != -1) {
                gradientDrawablePressed.setStroke(backgroundBorderWidth, backgroundBorderPressed);
            }

            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(
                    new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed},
                    gradientDrawablePressed);
            stateListDrawable.addState(
                    new int[]{},
                    gradientDrawable);
            view.setBackgroundDrawable(stateListDrawable);
        }
        a.recycle();
    }
}
