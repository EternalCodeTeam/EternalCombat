package com.eternalcode.combat.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectUtil {

    private ReflectUtil() {
    }

    @SuppressWarnings("unchecked")
    public static  <T> T invokeMethod(Object object, String name) {
        try {
            if (object == null) {
                return null;
            }

            Method method = object.getClass().getDeclaredMethod(name);
            method.setAccessible(true);
            return (T) method.invoke(object);
        }
        catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

}
