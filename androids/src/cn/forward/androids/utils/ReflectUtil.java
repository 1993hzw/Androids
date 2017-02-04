package cn.forward.androids.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public class ReflectUtil {
    /**
     * 获取对象里指定变量的值
     *
     * @param instance
     * @param fieldName
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static Object getValue(Object instance, String fieldName)
            throws IllegalAccessException, NoSuchFieldException {
        Field field = getField(instance.getClass(), fieldName);
        // 参数值为true，禁用访问控制检查
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * 获取类里指定变量的field
     *
     * @param thisClass
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     */
    public static Field getField(Class<?> thisClass, String fieldName)
            throws NoSuchFieldException {
        if (thisClass == null) {
            throw new NoSuchFieldException("Error field !");
        }

        try {
            return thisClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return getField(thisClass.getSuperclass(), fieldName);
        }
    }

    /**
     * 获取对象里的方法
     *
     * @param instance
     * @param methodName
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getMethod(Object instance, String methodName, Class<?>[] parameterTypes)
            throws NoSuchMethodException {
        Method accessMethod = getMethod(instance.getClass(), methodName, parameterTypes);
        //参数值为true，禁用访问控制检查
        accessMethod.setAccessible(true);

        return accessMethod;
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
    private static Method getMethod(Class<?> thisClass, String methodName, Class<?>[] parameterTypes)
            throws NoSuchMethodException {
        if (thisClass == null) {
            throw new NoSuchMethodException("Error method !");
        }

        try {
            return thisClass.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return getMethod(thisClass.getSuperclass(), methodName, parameterTypes);
        }
    }

    /**
     * 调用含多个参数的方法
     *
     * @param instance
     * @param methodName
     * @param args
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokeMethod(Object instance, String methodName, Object... args)
            throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        Class<?>[] parameterTypes = null;
        if (args != null) {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    parameterTypes[i] = args[i].getClass();
                }
            }
        }
        return getMethod(instance, methodName, parameterTypes).invoke(instance, args);
    }
}
