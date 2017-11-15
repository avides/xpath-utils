package com.avides.xpath.utils.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Utility-class for all internally used reflection-stuff
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ReflectionUtils
{
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new HashMap<>(256);

    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    private static final Map<Class<?>, Object> primitiveNullValuesMap = new IdentityHashMap<Class<?>, Object>(8);

    private static final Field[] NO_FIELDS =
    {};

    static
    {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        primitiveNullValuesMap.put(boolean.class, Boolean.FALSE);
        primitiveNullValuesMap.put(char.class, Character.valueOf((char) 0));
        primitiveNullValuesMap.put(double.class, Double.valueOf(0));
        primitiveNullValuesMap.put(float.class, Float.valueOf(0));
        primitiveNullValuesMap.put(int.class, Integer.valueOf(0));
        primitiveNullValuesMap.put(long.class, Long.valueOf(0));
        primitiveNullValuesMap.put(short.class, Short.valueOf((short) 0));

        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet())
        {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fc)
    {
        Class<?> targetClass = clazz;
        do
        {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields)
            {
                try
                {
                    fc.doWith(field);
                }
                catch (IllegalAccessException ex)
                {
                    throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        while ((targetClass != null) && (targetClass != Object.class));
    }

    private static Field[] getDeclaredFields(Class<?> clazz)
    {
        Field[] result = declaredFieldsCache.get(clazz);
        if (result == null)
        {
            result = clazz.getDeclaredFields();
            declaredFieldsCache.put(clazz, (result.length == 0 ? NO_FIELDS : result));
        }
        return result;
    }

    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType)
    {
        if (lhsType.isAssignableFrom(rhsType))
        {
            return true;
        }
        if (lhsType.isPrimitive())
        {
            Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            if (lhsType == resolvedPrimitive)
            {
                return true;
            }
        }
        else
        {
            Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
            if ((resolvedWrapper != null) && lhsType.isAssignableFrom(resolvedWrapper))
            {
                return true;
            }
        }
        return false;
    }

    public static Object getNullValue(Class<?> type)
    {
        return primitiveNullValuesMap.get(type);
    }

    public interface FieldCallback
    {
        void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
    }
}