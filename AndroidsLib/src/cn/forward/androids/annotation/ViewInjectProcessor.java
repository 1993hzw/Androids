package cn.forward.androids.annotation;

import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by huangziwei on 16-11-9.
 */
public class ViewInjectProcessor {

    /**
     * 根据id,注入view
     *
     * @param object
     * @param view
     */
    public static void process(Object object, View view) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if (viewInject == null || view.getId() != viewInject.id()) {
                continue;
            }
            try {
                field.setAccessible(true);
                field.set(object, view);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
