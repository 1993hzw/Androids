package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
        Drawable backgroundDrawable = view.getBackground();
        if (backgroundDrawable instanceof ColorDrawable) { // 颜色值
            backgroundDrawable = null;
        }

        GradientDrawable gradientDrawable = null;
        if (backgroundDrawable == null) {
            gradientDrawable = new GradientDrawable();
        }

        GradientDrawable gradientDrawablePressed = null;
        GradientDrawable gradientDrawableSelected = null;

        Drawable backgroundDrawablePressed = null;
        Drawable backgroundDrawableSelected = null;

        if (a.hasValue(R.styleable.View_sel_background_pressed) || a.hasValue(R.styleable.View_sel_background_border_pressed)) {
            backgroundDrawablePressed = a.getDrawable(R.styleable.View_sel_background_pressed);
            if (backgroundDrawablePressed instanceof ColorDrawable) {
                backgroundDrawablePressed = null;
            }
            if (backgroundDrawablePressed == null) {
                gradientDrawablePressed = new GradientDrawable();
            }
        }
        if (a.hasValue(R.styleable.View_sel_background_selected) || a.hasValue(R.styleable.View_sel_background_border_selected)) {
            backgroundDrawableSelected = a.getDrawable(R.styleable.View_sel_background_selected);
            if (backgroundDrawableSelected instanceof ColorDrawable) {
                backgroundDrawableSelected = null;
            }
            if (backgroundDrawableSelected == null) {
                gradientDrawableSelected = new GradientDrawable();
            }
        }

        int background = Color.TRANSPARENT;
        if (backgroundDrawable == null) {
            Drawable bg = view.getBackground();
            if (bg instanceof ColorDrawable) {
                background = ((ColorDrawable) bg).getColor();
            }
            gradientDrawable.setColor(background);
        }
        if (gradientDrawablePressed != null) {
            gradientDrawablePressed.setColor(background);
        }
        if (gradientDrawableSelected != null) {
            gradientDrawableSelected.setColor(background);
        }


        int shape = a.getInt(R.styleable.View_sel_background_shape, RECTANGLE);
        if (shape == LINE) {
            if (gradientDrawable != null) {
                gradientDrawable.setShape(GradientDrawable.LINE);
            }
            if (gradientDrawablePressed != null) {
                gradientDrawablePressed.setShape(GradientDrawable.LINE);
            }
            if (gradientDrawableSelected != null) {
                gradientDrawableSelected.setShape(GradientDrawable.LINE);
            }
        } else if (shape == OVAL) {
            if (gradientDrawable != null) {
                gradientDrawable.setShape(GradientDrawable.OVAL);
            }
            if (gradientDrawablePressed != null) {
                gradientDrawablePressed.setShape(GradientDrawable.OVAL);
            }
            if (gradientDrawableSelected != null) {
                gradientDrawableSelected.setShape(GradientDrawable.OVAL);
            }
        } else if (shape == RING) {
            if (gradientDrawable != null) {
                gradientDrawable.setShape(GradientDrawable.RING);
            }
            if (gradientDrawablePressed != null) {
                gradientDrawablePressed.setShape(GradientDrawable.RING);
            }
            if (gradientDrawableSelected != null) {
                gradientDrawableSelected.setShape(GradientDrawable.RING);
            }
        } else {
            if (gradientDrawable != null) {
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            }
            if (gradientDrawablePressed != null) {
                gradientDrawablePressed.setShape(GradientDrawable.RECTANGLE);
            }
            if (gradientDrawableSelected != null) {
                gradientDrawableSelected.setShape(GradientDrawable.RECTANGLE);
            }
        }

        int backgroundBorderWidth = a.getDimensionPixelOffset(R.styleable.View_sel_background_border_width, -1);
        if (backgroundBorderWidth != -1) {
            if (gradientDrawable != null) {
                gradientDrawable.setStroke(backgroundBorderWidth, 0);
            }
            if (gradientDrawablePressed != null) {
                gradientDrawablePressed.setStroke(backgroundBorderWidth, 0);
            }
            if (gradientDrawableSelected != null) {
                gradientDrawableSelected.setStroke(backgroundBorderWidth, 0);
            }
        }

        if (a.hasValue(R.styleable.View_sel_background_border_color)) {
            int backgroundBorder = a.getColor(R.styleable.View_sel_background_border_color, -1);
            if (gradientDrawable != null) {
                gradientDrawable.setStroke(backgroundBorderWidth, backgroundBorder);
            }
            if (gradientDrawablePressed != null) {
                gradientDrawablePressed.setStroke(backgroundBorderWidth, backgroundBorder);
            }
            if (gradientDrawableSelected != null) {
                gradientDrawableSelected.setStroke(backgroundBorderWidth, backgroundBorder);
            }
        }
        int backgroundCorners = a.getDimensionPixelOffset(R.styleable.View_sel_background_corners, 0);
        final int radius = backgroundCorners;
        if (gradientDrawable != null) {
            gradientDrawable.setCornerRadius(backgroundCorners);
        }
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
            if (gradientDrawable != null) {
                gradientDrawable.setCornerRadii(new float[]{
                        topLeftRadius, topLeftRadius,
                        topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius,
                        bottomLeftRadius, bottomLeftRadius
                });
            }
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


        if (gradientDrawablePressed != null && a.hasValue(R.styleable.View_sel_background_pressed)) {
            int backgroundPressed = a.getColor(R.styleable.View_sel_background_pressed, 0);
            gradientDrawablePressed.setColor(backgroundPressed);
        }
        if (gradientDrawablePressed != null && a.hasValue(R.styleable.View_sel_background_border_pressed)) {
            int backgroundBorderPressed = a.getColor(R.styleable.View_sel_background_border_pressed, -1);
            gradientDrawablePressed.setStroke(backgroundBorderWidth, backgroundBorderPressed);
        }
        if (gradientDrawableSelected != null && a.hasValue(R.styleable.View_sel_background_selected)) {
            int backgroundSelected = a.getColor(R.styleable.View_sel_background_selected, -1);
            gradientDrawableSelected.setColor(backgroundSelected);
        }
        if (gradientDrawableSelected != null && a.hasValue(R.styleable.View_sel_background_border_selected)) {
            int backgroundBorderSelected = a.getColor(R.styleable.View_sel_background_border_selected, -1);
            gradientDrawableSelected.setStroke(backgroundBorderWidth, backgroundBorderSelected);
        }

        StateListDrawable stateListDrawable = new StateListDrawable();
        if (gradientDrawablePressed != null || backgroundDrawablePressed != null) {
            stateListDrawable.addState(
                    new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed},
                    backgroundDrawablePressed != null ? backgroundDrawablePressed : gradientDrawablePressed);
        }
        if (gradientDrawableSelected != null || backgroundDrawableSelected != null) {
            stateListDrawable.addState(
                    new int[]{android.R.attr.state_enabled, android.R.attr.state_selected},
                    backgroundDrawableSelected != null ? backgroundDrawableSelected : gradientDrawableSelected);
        }
        stateListDrawable.addState(
                new int[]{},
                backgroundDrawable != null ? backgroundDrawable : gradientDrawable);

        view.setBackgroundDrawable(stateListDrawable);
        a.recycle();
    }
}
