package cn.forward.androids.views.behavior;

/**
 * @author ziwei huang
 */
public interface IOverscroll {

    boolean canScrollDown();

    boolean canScrollUp();

    int getMaxFlingScrollDown();

    int getMaxFlingScrollUp();

    float getDampingFactor();
}
