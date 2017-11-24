package com.avides.xpath.utils.processors;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avides.xpath.utils.utils.ReflectionUtils;

import nu.xom.Element;

/**
 * @author Martin Schumacher
 * @since 1.0.3.RELEASE
 */
public abstract class AbstractAnnotationProcessor
{
    private static final Logger log = LoggerFactory.getLogger(AbstractAnnotationProcessor.class);

    protected Element root;
    protected Field field;
    protected Object target;
    private Map<Class<?>, Function<String, ?>> defaultToTypeConverters;

    public AbstractAnnotationProcessor(Element root, Field field, Object target, Map<Class<?>, Function<String, ?>> defaultToTypeConverters)
    {
        this.root = root;
        this.field = field;
        this.target = target;
        this.defaultToTypeConverters = defaultToTypeConverters;
    }

    protected <T> boolean setFieldValueSimple(Field field, Object target, T value, boolean throwExceptionIfFieldCouldNotBeSet)
    {
        try
        {
            if (value == null)
            {
                if (!field.getType().isPrimitive())
                {
                    setFieldValue(field, target, null);
                }
                else
                {
                    Object nullValue = ReflectionUtils.getNullValue(field.getType());
                    if (nullValue != null)
                    {
                        setFieldValue(field, target, nullValue);
                    }
                    else
                    {
                        log.warn("no null-value for primitive byte is implemented yet, so don't set anything for field " + field + " on target " + target);
                    }
                }
                return true;
            }
            if (ReflectionUtils.isAssignable(field.getType(), value.getClass()))
            {
                setFieldValue(field, target, value);
                return true;
            }
            else if ((value.getClass() == String.class) && !ReflectionUtils.isAssignable(field.getType(), value.getClass()))
            {
                Function<String, ?> converter = defaultToTypeConverters.get(field.getType());
                if (converter != null)
                {
                    setFieldValue(field, target, converter.apply((String) value));
                    return true;
                }
            }
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException("could not set value " + value + " for field " + field + " on target " + target, e);
        }
        if (throwExceptionIfFieldCouldNotBeSet)
        {
            throw new RuntimeException(
                "could not set value " + value + " for field " + field + " on target " + target + " (field-types not matching)");
        }
        return false;
    }

    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    protected <T> void setFieldValueExtended(Field field, Object target, T value)
    {
        try
        {
            if (!setFieldValueSimple(field, target, value, false))
            {
                if ((field.getType().isEnum() && String.class.isAssignableFrom(value.getClass())))
                {
                    Enum enumValue;
                    if (((String) value).isEmpty())
                    {
                        enumValue = null;
                    }
                    else
                    {
                        enumValue = Enum.valueOf((Class<Enum>) field.getType(), (String) value);
                    }
                    setFieldValue(field, target, enumValue);
                }
                else
                {
                    throw new RuntimeException("could not set value " + value + " for field " + field + " on target " + target + " (field-types not matching)");
                }
            }

        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException("could not set value " + value + " for field " + field + " on target " + target, e);
        }
    }

    private <T> void setFieldValue(Field field, Object target, T value) throws IllegalArgumentException, IllegalAccessException
    {
        if ((!Modifier.isPublic(field.getModifiers()) ||
            !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
            Modifier.isFinal(field.getModifiers())) && !field.isAccessible())
        {
            field.setAccessible(true);
        }
        field.set(target, value);
    }
}