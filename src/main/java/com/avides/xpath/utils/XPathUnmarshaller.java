package com.avides.xpath.utils;

import static com.avides.xpath.utils.utils.ReflectionUtils.doWithFields;
import static com.avides.xpath.utils.utils.ReflectionUtils.makeAccessible;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avides.xpath.utils.annotations.XPathFirst;
import com.avides.xpath.utils.annotations.XPathList;
import com.avides.xpath.utils.annotations.XPathMap;
import com.avides.xpath.utils.utils.ReflectionUtils;

import nu.xom.Element;
import nu.xom.ParsingException;

/**
 * Class for unmarshalling xml to new instances of classes which have fields
 * that are annotated with {@link com.avides.xpath.utils.annotations.XPathFirst
 * XPathFirst}, {@link com.avides.xpath.utils.annotations.XPathList XPathList}
 * or {@link com.avides.xpath.utils.annotations.XPathMap XPathMap}. Such classes
 * must have a public no-args-constructor
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 *
 * @see {@link com.avides.xpath.utils.XPathUtils}
 * @see {@link com.avides.xpath.utils.XPathUtils#fromRoot(Element, Class)}
 * @see {@link com.avides.xpath.utils.XPathUtils#fromXml(String, Class)}
 */
public class XPathUnmarshaller
{
    private static final Logger log = LoggerFactory.getLogger(XPathUnmarshaller.class);

    private static XPathUnmarshaller instance;

    private XPathUnmarshaller()
    {
        // private constructor to hide the public one (singleton-pattern)
    }

    /**
     * unmarshalls from xml
     *
     * @param xml
     *            xml to unmarshal from
     * @param type
     *            the type of the class with the annotated fields
     * @return new instance of the given type unmarshalled by the given xml
     * @throws ParsingException
     *             if the xml can not be parsed (invalid xml)
     */
    public <T> T unmarshal(String xml, Class<T> type) throws ParsingException
    {
        return unmarshal(XPathUtils.getRootElementFromXml(xml), type);
    }

    /**
     * unmarshalls from {@link nu.xom.Element Element}
     *
     * @param root
     *            {@link nu.xom.Element Element} to unmarshal from
     * @param type
     *            the type of the class with the annotated fields
     * @return new instance of the given type unmarshalled by the given
     *         {@link nu.xom.Element Element}
     */
    public <T> T unmarshal(Element root, Class<T> type)
    {
        final T target;
        try
        {
            target = type.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            log.error("could not create new instance for " + type.getName(), e);
            throw new RuntimeException(e);
        }
        doWithFields(type, field ->
        {
            XPathFirst xPathFirst = field.getAnnotation(XPathFirst.class);
            if (xPathFirst != null)
            {
                Object value = XPathUtils.queryFirst(root, xPathFirst.value(), xPathFirst.converterClass());
                setFieldValueExtended(field, target, value);
            }
            XPathMap xPathMap = field.getAnnotation(XPathMap.class);
            if (xPathMap != null)
            {
                Map<?, ?> value = XPathUtils.queryMap(root, xPathMap.entryXPath(), xPathMap.keySubXPath(), xPathMap.valueSubXPath(),
                    xPathMap.keyConverterClass(), xPathMap.valueConverterClass());
                if (!setFieldValueSimple(field, target, value))
                {
                    throw new RuntimeException("could not set value " + value + " for field " + field + " on target " + target + " (field-types not matching)");
                }
            }
            XPathList xPathList = field.getAnnotation(XPathList.class);
            if (xPathList != null)
            {
                List<?> value = XPathUtils.queryList(root, xPathList.value(), xPathList.converterClass());
                if (!setFieldValueSimple(field, target, value))
                {
                    throw new RuntimeException("could not set value " + value + " for field " + field + " on target " + target + " (field-types not matching)");
                }
            }
        });
        return target;
    }

    private static boolean setFieldValueSimple(Field field, Object target, Object value)
    {
        try
        {
            if (value == null)
            {
                makeAccessible(field);
                if (!field.getType().isPrimitive())
                {
                    field.set(target, null);
                }
                else
                {
                    Object nullValue = ReflectionUtils.getNullValue(field.getType());
                    if (nullValue != null)
                    {
                        field.set(target, nullValue);
                    }
                    else
                    {
                        log.warn("no null-value for primitive byte is implemented yet, so don't set anything for field " + field + " on target " + target);
                    }
                }
                return true;
            }
            else if (ReflectionUtils.isAssignable(field.getType(), value.getClass()))
            {
                makeAccessible(field);
                field.set(target, value);
                return true;
            }
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException("could not set value " + value + " for field " + field + " on target " + target, e);
        }
        return false;
    }

    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    private static void setFieldValueExtended(Field field, Object target, Object value)
    {
        try
        {
            if (!setFieldValueSimple(field, target, value))
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
                    makeAccessible(field);
                    field.set(target, enumValue);
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

    /**
     * Returns the singleton-instance of the
     * {@link com.avides.xpath.utils.XPathUnmarshaller XPathUnmarshaller}
     *
     * @return the singleton-instance of the
     *         {@link com.avides.xpath.utils.XPathUnmarshaller
     *         XPathUnmarshaller}
     */
    public static XPathUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new XPathUnmarshaller();
        }
        return instance;
    }
}