package cn.forward.androids.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public class ReflectUtil {
    /**
     * 获取类里指定的变量
     *
     * @param thisClass
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> thisClass, String fieldName) {
        if (thisClass == null) {
            return null;
        }

        try {
            return thisClass.getDeclaredField(fieldName);
        } catch (Throwable e) {
            return null;
        }
    }


    /**
     * 获取对象里变量的值
     *
     * @param instance
     * @param fieldName
     * @return 返回空则可能值不存在，或变量不存在
     */
    public static Object getValue(Object instance, String fieldName) {
        Field field = getField(instance.getClass(), fieldName);
        if (field == null) {
            return null;
        }
        // 参数值为true，禁用访问控制检查
        field.setAccessible(true);
        try {
            return field.get(instance);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 获取类里的方法
     *
     * @param thisClass
     * @param methodName
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    private static Method getMethod(Class<?> thisClass, String methodName, Class<?>[] parameterTypes) {
        if (thisClass == null) {
            return null;
        }

        try {
            Method method = thisClass.getDeclaredMethod(methodName, parameterTypes);
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            return method;
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 执行对象里的方法
     *
     * @param instance
     * @param methodName
     * @param args       方法参数
     * @return 返回值
     * @throws Throwable 方法不存在或者执行失败跑出异常
     */
    public static Object invokeMethod(Object instance, String methodName, Object... args) throws Throwable {
        Class<?>[] parameterTypes = null;
        if (args != null) {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    parameterTypes[i] = args[i].getClass();
                }
            }
        }
        Method method = getMethod(instance.getClass(), methodName, parameterTypes);
        return method.invoke(instance, args);
    }
}
