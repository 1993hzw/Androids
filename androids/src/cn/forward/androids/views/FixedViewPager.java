package cn.forward.androids.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 解决ViewPager设置切换动画PageTransformer后子页无法触摸的问题
 */
public class FixedViewPager extends ViewPager {

    private PageTransformer mPageTransformer;

    public FixedViewPager(Context context) {
        this(context, null);
    }

    public FixedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * android4.1+设置PageTransformer会使ViewPager的子页里面的触摸事件异常
     * （当前看到的子页并非在最上面，所以触摸事件被隐藏在其上面的View给消费了）
     * 所以结合setPageTransformer()，在onPageScrolled()里“手动”调用切换页面的动画
     *
     */
    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        // 下面的源码来自super.onPageScrolled()
        int scrollX;
        int childCount;
        int i;
        if (this.mPageTransformer != null) {
            scrollX = this.getScrollX();
            childCount = this.getChildCount();

            for (i = 0; i < childCount; ++i) {
                View var15 = this.getChildAt(i);
                ViewPager.LayoutParams var16 = (ViewPager.LayoutParams) var15.getLayoutParams();
                if (!var16.isDecor) {
                    float var17 = (float) (var15.getLeft() - scrollX) / (float) this.getClientWidth();
                    this.mPageTransformer.transformPage(var15, var17);
                }
            }
        }
    }

    private int getClientWidth() {
        return this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight();
    }

    /**
     * 覆盖该方法，不设置PageTransformer，以成员变量的形式保存PageTransformer
     *
     */
    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        super.setPageTransformer(reverseDrawingOrder, null);
        mPageTransformer = transformer;
    }

}