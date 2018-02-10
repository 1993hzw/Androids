package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
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

        //背景已经设置为StateListDrawable\RippleDrawable,则不再设置Selector
        Drawable bitmapDrawable = view.getBackground();
        if (bitmapDrawable instanceof StateListDrawable) {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (bitmapDrawable instanceof RippleDrawable) {
                return;
            }
        }

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.View);

        // 背景图片和背景颜色互斥，只能设置其中之一

        if (bitmapDrawable instanceof ColorDrawable) { // 颜色值
            bitmapDrawable = null;
        }

        // 用于设置背景的颜色和形状
        GradientDrawable colorShapeDrawable = null;
        GradientDrawable colorShapeDrawablePressed = null;
        GradientDrawable colorShapeDrawableSelected = null;

        // 用于设置背景的图片
        Drawable bitmapDrawablePressed = null;
        Drawable bitmapDrawableSelected = null;

        if (a.hasValue(R.styleable.View_sel_background_pressed) || a.hasValue(R.styleable.View_sel_background_border_pressed)) {
            bitmapDrawablePressed = a.getDrawable(R.styleable.View_sel_background_pressed);
            if (bitmapDrawablePressed instanceof ColorDrawable) {
                bitmapDrawablePressed = null;
            }
            if (bitmapDrawablePressed == null) { // 接下来设置背景颜色
                colorShapeDrawablePressed = new GradientDrawable();
            }
        }
        if (a.hasValue(R.styleable.View_sel_background_selected) || a.hasValue(R.styleable.View_sel_background_border_selected)) {
            bitmapDrawableSelected = a.getDrawable(R.styleable.View_sel_background_selected);
            if (bitmapDrawableSelected instanceof ColorDrawable) {
                bitmapDrawableSelected = null;
            }
            if (bitmapDrawableSelected == null) {
                colorShapeDrawableSelected = new GradientDrawable();
            }
        }

        // 设置背景色
        int background = Color.TRANSPARENT;
        if (bitmapDrawable == null) {
            Drawable bg = view.getBackground();
            if (bg == null && a.hasValue(R.styleable.View_sel_background)) { // 兼容旧版本属性
                bg = a.getDrawable(R.styleable.View_sel_background);
            }
            if (bg instanceof ColorDrawable) {
                background = ((ColorDrawable) bg).getColor();
                colorShapeDrawable = new GradientDrawable();
                colorShapeDrawable.setColor(background);
            }
        }
        if (colorShapeDrawablePressed != null) {
            colorShapeDrawablePressed.setColor(background);
        }
        if (colorShapeDrawableSelected != null) {
            colorShapeDrawableSelected.setColor(background);
        }

        // 形狀
        int shape = a.getInt(R.styleable.View_sel_background_shape, RECTANGLE);
        if (shape == LINE) {
            if (colorShapeDrawable != null) {
                colorShapeDrawable.setShape(GradientDrawable.LINE);
            }
            if (colorShapeDrawablePressed != null) {
                colorShapeDrawablePressed.setShape(GradientDrawable.LINE);
            }
            if (colorShapeDrawableSelected != null) {
                colorShapeDrawableSelected.setShape(GradientDrawable.LINE);
            }
        } else if (shape == OVAL) {
            if (colorShapeDrawable != null) {
                colorShapeDrawable.setShape(GradientDrawable.OVAL);
            }
            if (colorShapeDrawablePressed != null) {
                colorShapeDrawablePressed.setShape(GradientDrawable.OVAL);
            }
            if (colorShapeDrawableSelected != null) {
                colorShapeDrawableSelected.setShape(GradientDrawable.OVAL);
            }
        } else if (shape == RING) {
            if (colorShapeDrawable != null) {
                colorShapeDrawable.setShape(GradientDrawable.RING);
            }
            if (colorShapeDrawablePressed != null) {
                colorShapeDrawablePressed.setShape(GradientDrawable.RING);
            }
            if (colorShapeDrawableSelected != null) {
                colorShapeDrawableSelected.setShape(GradientDrawable.RING);
            }
        } else {
            if (colorShapeDrawable != null) {
                colorShapeDrawable.setShape(GradientDrawable.RECTANGLE);
            }
            if (colorShapeDrawablePressed != null) {
                colorShapeDrawablePressed.setShape(GradientDrawable.RECTANGLE);
            }
            if (colorShapeDrawableSelected != null) {
                colorShapeDrawableSelected.setShape(GradientDrawable.RECTANGLE);
            }
        }

        // 圆角
        int backgroundCorners = a.getDimensionPixelOffset(R.styleable.View_sel_background_corners, 0);
        final int radius = backgroundCorners;
        if (colorShapeDrawable != null) {
            colorShapeDrawable.setCornerRadius(backgroundCorners);
        }
        if (colorShapeDrawablePressed != null) {
            colorShapeDrawablePressed.setCornerRadius(backgroundCorners);
        }
        if (colorShapeDrawableSelected != null) {
            colorShapeDrawableSelected.setCornerRadius(backgroundCorners);
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
            if (colorShapeDrawable != null) {
                colorShapeDrawable.setCornerRadii(new float[]{
                        topLeftRadius, topLeftRadius,
                        topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius,
                        bottomLeftRadius, bottomLeftRadius
                });
            }
            if (colorShapeDrawablePressed != null) {
                colorShapeDrawablePressed.setCornerRadii(new float[]{
                        topLeftRadius, topLeftRadius,
                        topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius,
                        bottomLeftRadius, bottomLeftRadius
                });
            }
            if (colorShapeDrawableSelected != null) {
                colorShapeDrawableSelected.setCornerRadii(new float[]{
                        topLeftRadius, topLeftRadius,
                        topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius,
                        bottomLeftRadius, bottomLeftRadius
                });
            }
        }


        // border
        int backgroundBorderWidth = a.getDimensionPixelOffset(R.styleable.View_sel_background_border_width, -1);
        if (backgroundBorderWidth != -1) {
            if (colorShapeDrawable != null) {
                colorShapeDrawable.setStroke(backgroundBorderWidth, 0);
            }
            if (colorShapeDrawablePressed != null) {
                colorShapeDrawablePressed.setStroke(backgroundBorderWidth, 0);
            }
            if (colorShapeDrawableSelected != null) {
                colorShapeDrawableSelected.setStroke(backgroundBorderWidth, 0);
            }
        }

        if (a.hasValue(R.styleable.View_sel_background_border_color)) {
            int backgroundBorder = a.getColor(R.styleable.View_sel_background_border_color, -1);
            if (colorShapeDrawable != null) {
                colorShapeDrawable.setStroke(backgroundBorderWidth, backgroundBorder);
            }
            if (colorShapeDrawablePressed != null) {
                colorShapeDrawablePressed.setStroke(backgroundBorderWidth, backgroundBorder);
            }
            if (colorShapeDrawableSelected != null) {
                colorShapeDrawableSelected.setStroke(backgroundBorderWidth, backgroundBorder);
            }
        }

        // 如果没有设置背景图片，则读取背景颜色
        if (colorShapeDrawablePressed != null && a.hasValue(R.styleable.View_sel_background_pressed)) {
            int backgroundPressed = a.getColor(R.styleable.View_sel_background_pressed, 0);
            colorShapeDrawablePressed.setColor(backgroundPressed);
        }
        if (colorShapeDrawablePressed != null && a.hasValue(R.styleable.View_sel_background_border_pressed)) {
            int backgroundBorderPressed = a.getColor(R.styleable.View_sel_background_border_pressed, -1);
            colorShapeDrawablePressed.setStroke(backgroundBorderWidth, backgroundBorderPressed);
        }
        if (colorShapeDrawableSelected != null && a.hasValue(R.styleable.View_sel_background_selected)) {
            int backgroundSelected = a.getColor(R.styleable.View_sel_background_selected, -1);
            colorShapeDrawableSelected.setColor(backgroundSelected);
        }
        if (colorShapeDrawableSelected != null && a.hasValue(R.styleable.View_sel_background_border_selected)) {
            int backgroundBorderSelected = a.getColor(R.styleable.View_sel_background_border_selected, -1);
            colorShapeDrawableSelected.setStroke(backgroundBorderWidth, backgroundBorderSelected);
        }


        // 设置不同状态下的显示
        StateListDrawable stateListDrawable = new StateListDrawable();
        // ripple属性兼容低版本（<21）
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP // 低于21（5.0）版本
                && a.hasValue(R.styleable.View_sel_background_ripple)) { // 但设置了ripple，处理兼容则把ripple的颜色设为按下时的颜色

            Drawable drawable = null;
            if (a.hasValue(R.styleable.View_sel_background_ripple_mask)) { // 根据mask设置形状
                drawable = a.getDrawable(R.styleable.View_sel_background_ripple_mask);
                if (drawable instanceof ColorDrawable) { // mask为颜色类型才可以设置形状
                    drawable = new GradientDrawable();
                    parseRippleMaskShape((GradientDrawable) drawable, a);
                    // 以sel_background_ripple的颜色为准
                    ((GradientDrawable) drawable).setColor(a.getColor(R.styleable.View_sel_background_ripple, 0));
                } else {
                    drawable = a.getDrawable(R.styleable.View_sel_background_ripple);
                }
            } else {
                drawable = a.getDrawable(R.styleable.View_sel_background_ripple);
            }

            // ripple效果只是在原来的pressed基础上附加波纹效果，并不会单独作为pressed状态的效果
            if (colorShapeDrawablePressed != null || bitmapDrawablePressed != null) {
                drawable = new LayerDrawable(new Drawable[]{
                        bitmapDrawablePressed != null ? bitmapDrawablePressed : colorShapeDrawablePressed,
                        drawable
                });
            } else if (bitmapDrawable != null || colorShapeDrawable != null) {
                drawable = new LayerDrawable(new Drawable[]{
                        bitmapDrawable != null ? bitmapDrawable : colorShapeDrawable,
                        drawable
                });
            }
            stateListDrawable.addState(
                    new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, drawable);
        } else if (colorShapeDrawablePressed != null || bitmapDrawablePressed != null) {
            Drawable drawable = bitmapDrawablePressed != null ? bitmapDrawablePressed : colorShapeDrawablePressed;
            stateListDrawable.addState(
                    new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, drawable);
        }

        if (colorShapeDrawableSelected != null || bitmapDrawableSelected != null) {
            Drawable drawable = bitmapDrawableSelected != null ? bitmapDrawableSelected : colorShapeDrawableSelected;
            stateListDrawable.addState(
                    new int[]{android.R.attr.state_enabled, android.R.attr.state_selected}, drawable);
        }
        stateListDrawable.addState(
                new int[]{},
                bitmapDrawable != null ? bitmapDrawable : colorShapeDrawable);


        // 设置ripple水波纹
        boolean hasRipple = parseRipple(view, a,
                // ripple效果只是在原来的pressed基础上附加波纹效果，并不会单独作为pressed状态的效果
                (colorShapeDrawable != null || bitmapDrawable != null
                        || colorShapeDrawablePressed != null || bitmapDrawablePressed != null) ?
                        stateListDrawable : null);

        if (!hasRipple) {
            view.setBackgroundDrawable(stateListDrawable);
        }
        a.recycle();
    }

    private static boolean parseRipple(View view, TypedArray a, Drawable content) {
        // 设置ripple水波纹
        boolean hasRipple = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (a.hasValue(R.styleable.View_sel_background_ripple)) {
                hasRipple = true;
                // 设置了背景则作为ripple的contentDrawable

                // 用于设置mask的形状
                GradientDrawable maskShapeDrawable = null;
                // 用于设置mask的图片
                Drawable maskBitmapDrawable = null;

                if (a.hasValue(R.styleable.View_sel_background_ripple_mask)) {
                    maskBitmapDrawable = a.getDrawable(R.styleable.View_sel_background_ripple_mask);
                    if (maskBitmapDrawable instanceof ColorDrawable) {
                        maskBitmapDrawable = null;
                    }
                    if (maskBitmapDrawable == null) { // 接下来设置mask形状
                        maskShapeDrawable = new GradientDrawable();
                        parseRippleMaskShape(maskShapeDrawable, a);
                    }
                }
                RippleDrawable rippleDrawable = new RippleDrawable(
                        a.getColorStateList(R.styleable.View_sel_background_ripple),
                        content, maskBitmapDrawable != null ? maskBitmapDrawable : maskShapeDrawable);

                view.setBackgroundDrawable(rippleDrawable);
            }
        }
        return hasRipple;
    }

    private static void parseRippleMaskShape(GradientDrawable maskShapeDrawable, TypedArray a) {
        // 必须设置颜色
        maskShapeDrawable.setColor(a.getColor(R.styleable.View_sel_background_ripple_mask, Color.TRANSPARENT));

        // Mask形状
        int shape = a.getInt(R.styleable.View_sel_background_ripple_mask_shape, RECTANGLE);
        if (shape == LINE) {
            maskShapeDrawable.setShape(GradientDrawable.LINE);
        } else if (shape == OVAL) {
            maskShapeDrawable.setShape(GradientDrawable.OVAL);
        } else if (shape == RING) {
            maskShapeDrawable.setShape(GradientDrawable.RING);
        } else {
            maskShapeDrawable.setShape(GradientDrawable.RECTANGLE);
        }

        // mask圆角
        int backgroundCorners = a.getDimensionPixelOffset(R.styleable.View_sel_background_ripple_mask_corners, 0);
        final int radius = backgroundCorners;
        maskShapeDrawable.setCornerRadius(backgroundCorners);

        final int topLeftRadius = a.getDimensionPixelSize(
                R.styleable.View_sel_background_ripple_mask_corner_topLeft, radius);
        final int topRightRadius = a.getDimensionPixelSize(
                R.styleable.View_sel_background_ripple_mask_corner_topRight, radius);
        final int bottomLeftRadius = a.getDimensionPixelSize(
                R.styleable.View_sel_background_ripple_mask_corner_bottomLeft, radius);
        final int bottomRightRadius = a.getDimensionPixelSize(
                R.styleable.View_sel_background_ripple_mask_corner_bottomRight, radius);
        if (topLeftRadius != radius || topRightRadius != radius ||
                bottomLeftRadius != radius || bottomRightRadius != radius) {
            maskShapeDrawable.setCornerRadii(new float[]{
                    topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius
            });
        }
    }
}
