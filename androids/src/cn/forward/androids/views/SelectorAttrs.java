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
                R.styleable.View);
        GradientDrawable gradientDrawable = new GradientDrawable();


        if (a.hasValue(R.styleable.View_sel_background)) { // 只有设置了mtv_background其他属性才生效

            GradientDrawable gradientDrawablePressed = null;
            GradientDrawable gradientDrawableSelected = null;
            if (a.hasValue(R.styleable.View_sel_background_pressed) || a.hasValue(R.styleable.View_sel_background_border_pressed)) {
                gradientDrawablePressed = new GradientDrawable();
            }
            if (a.hasValue(R.styleable.View_sel_background_selected) || a.hasValue(R.styleable.View_sel_background_selected)) {
                gradientDrawableSelected = new GradientDrawable();
            }

            int background = a.getColor(R.styleable.View_sel_background, -1);
            gradientDrawable.setColor(background);
            if (gradientDrawablePressed != null) {
                gradientDrawablePressed.setColor(background);
            }
            if (gradientDrawableSelected != null) {
                gradientDrawableSelected.setColor(background);
            }


            int shape = a.getInt(R.styleable.View_sel_background_shape, RECTANGLE);
            if (shape == LINE) {
                gradientDrawable.setShape(GradientDrawable.LINE);
                if (gradientDrawablePressed != null) {
                    gradientDrawablePressed.setShape(GradientDrawable.LINE);
                }
                if (gradientDrawableSelected != null) {
                    gradientDrawableSelected.setShape(GradientDrawable.LINE);
                }
            } else if (shape == OVAL) {
                gradientDrawable.setShape(GradientDrawable.OVAL);
                if (gradientDrawablePressed != null) {
                    gradientDrawablePressed.setShape(GradientDrawable.OVAL);
                }
                if (gradientDrawableSelected != null) {
                    gradientDrawableSelected.setShape(GradientDrawable.OVAL);
                }
            } else if (shape == RING) {
                gradientDrawable.setShape(GradientDrawable.RING);
                if (gradientDrawablePressed != null) {
                    gradientDrawablePressed.setShape(GradientDrawable.RING);
                }
                if (gradientDrawableSelected != null) {
                    gradientDrawableSelected.setShape(GradientDrawable.RING);
                }
            } else {
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                if (gradientDrawablePressed != null) {
                    gradientDrawablePressed.setShape(GradientDrawable.RECTANGLE);
                }
                if (gradientDrawableSelected != null) {
                    gradientDrawableSelected.setShape(GradientDrawable.RECTANGLE);
                }
            }

            int backgroundBorderWidth = a.getDimensionPixelOffset(R.styleable.View_sel_background_border_width, -1);
            if (backgroundBorderWidth != -1) {
                gradientDrawable.setStroke(backgroundBorderWidth, background);
                if (gradientDrawablePressed != null) {
                    gradientDrawablePressed.setStroke(backgroundBorderWidth, background);
                }
                if (gradientDrawableSelected != null) {
                    gradientDrawableSelected.setStroke(backgroundBorderWidth, background);
                }
            }
            int backgroundBorder = a.getColor(R.styleable.View_sel_background_border_color, -1);
            if (backgroundBorder != -1) {
                gradientDrawable.setStroke(backgroundBorderWidth, backgroundBorder);
                if (gradientDrawablePressed != null) {
                    gradientDrawablePressed.setStroke(backgroundBorderWidth, backgroundBorder);
                }
                if (gradientDrawableSelected != null) {
                    gradientDrawableSelected.setStroke(backgroundBorderWidth, backgroundBorder);
                }
            }
            int backgroundCorners = a.getDimensionPixelOffset(R.styleable.View_sel_background_corners, 0);
            final int radius = backgroundCorners;
            gradientDrawable.setCornerRadius(backgroundCorners);
            if (gradientDrawablePressed != null) {
                gradientDrawablePressed.setCornerRadius(backgroundCorners);
            }
            if (gradientDrawableSelected != null) {
                gradientDrawableSelected.setCornerRadius(backgroundCorners);
            }

            final int topLeftRadius = a.getDimensionPixelSize(
                    R.styleable.View_sel_background_corner_topLeft, radius);
            final int topRightRadius = a.getDimensionPixelSize(
                    R.styleable.View_sel_background_corner_topRight, radius);
            final int bottomLeftRadius = a.getDimensionPixelSize(
                    R.styleable.View_sel_background_corner_bottomLeft, radius);
            final int bottomRightRadius = a.getDimensionPixelSize(
                    R.styleable.View_sel_background_corner_bottomRight, radius);
            if (topLeftRadius != radius || topRightRadius != radius ||
                    bottomLeftRadius != radius || bottomRightRadius != radius) {
                gradientDrawable.setCornerRadii(new float[]{
                        topLeftRadius, topLeftRadius,
                        topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius,
                        bottomLeftRadius, bottomLeftRadius
                });
                if (gradientDrawablePressed != null) {
                    gradientDrawablePressed.setCornerRadii(new float[]{
                            topLeftRadius, topLeftRadius,
                            topRightRadius, topRightRadius,
                            bottomRightRadius, bottomRightRadius,
                            bottomLeftRadius, bottomLeftRadius
                    });
                }
                if (gradientDrawableSelected != null) {
                    gradientDrawableSelected.setCornerRadii(new float[]{
                            topLeftRadius, topLeftRadius,
                            topRightRadius, topRightRadius,
                            bottomRightRadius, bottomRightRadius,
                            bottomLeftRadius, bottomLeftRadius
                    });
                }
            }


            int backgroundPressed = a.getColor(R.styleable.View_sel_background_pressed, -1);
            if (backgroundPressed != -1) {
                gradientDrawablePressed.setColor(backgroundPressed);
            }
            int backgroundBorderPressed = a.getColor(R.styleable.View_sel_background_border_pressed, -1);
            if (backgroundBorderPressed != -1) {
                gradientDrawablePressed.setStroke(backgroundBorderWidth, backgroundBorderPressed);
            }

            int backgroundSelected = a.getColor(R.styleable.View_sel_background_selected, -1);
            if (backgroundSelected != -1) {
                gradientDrawableSelected.setColor(backgroundSelected);
            }
            int backgroundBorderSelected = a.getColor(R.styleable.View_sel_background_border_selected, -1);
            if (backgroundBorderSelected != -1) {
                gradientDrawableSelected.setStroke(backgroundBorderWidth, backgroundBorderSelected);
            }

            StateListDrawable stateListDrawable = new StateListDrawable();
            if (gradientDrawablePressed != null) {
                stateListDrawable.addState(
                        new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed},
                        gradientDrawablePressed);
            }
            if (gradientDrawableSelected != null) {
                stateListDrawable.addState(
                        new int[]{android.R.attr.state_enabled, android.R.attr.state_selected},
                        gradientDrawableSelected);
            }
            stateListDrawable.addState(
                    new int[]{},
                    gradientDrawable);

            view.setBackgroundDrawable(stateListDrawable);
        }
        a.recycle();
    }
}
