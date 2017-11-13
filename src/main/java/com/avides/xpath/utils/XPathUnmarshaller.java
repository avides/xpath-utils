package com.avides.xpath.utils;

import static com.avides.xpath.utils.utils.ReflectionUtils.doWithFields;
import static com.avides.xpath.utils.utils.ReflectionUtils.makeAccessible;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avides.xpath.utils.annotations.XPathFirst;
import com.avides.xpath.utils.annotations.XPathList;
import com.avides.xpath.utils.annotations.XPathMap;
import com.avides.xpath.utils.converter.ToBooleanConverter;
import com.avides.xpath.utils.converter.ToCharacterConverter;
import com.avides.xpath.utils.converter.ToDoubleConverter;
import com.avides.xpath.utils.converter.ToFloatConverter;
import com.avides.xpath.utils.converter.ToIntegerConverter;
import com.avides.xpath.utils.converter.ToLocalDateConverter;
import com.avides.xpath.utils.converter.ToLocalDateTimeConverter;
import com.avides.xpath.utils.converter.ToLocalTimeConverter;
import com.avides.xpath.utils.converter.ToLongConverter;
import com.avides.xpath.utils.converter.ToShortConverter;
import com.avides.xpath.utils.converter.ToZonedDateTimeConverter;
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

    private static final Map<Class<?>, Function<String, ?>> defaultToTypeConverter = new HashMap<>();

    static
    {
        defaultToTypeConverter.put(Integer.class, XPathUtils.getConverter(ToIntegerConverter.class));
        defaultToTypeConverter.put(Long.class, XPathUtils.getConverter(ToLongConverter.class));
        defaultToTypeConverter.put(Short.class, XPathUtils.getConverter(ToShortConverter.class));
        defaultToTypeConverter.put(Double.class, XPathUtils.getConverter(ToDoubleConverter.class));
        defaultToTypeConverter.put(Float.class, XPathUtils.getConverter(ToFloatConverter.class));
        defaultToTypeConverter.put(Boolean.class, XPathUtils.getConverter(ToBooleanConverter.class));
        defaultToTypeConverter.put(Character.class, XPathUtils.getConverter(ToCharacterConverter.class));
        defaultToTypeConverter.put(int.class, XPathUtils.getConverter(ToIntegerConverter.class));
        defaultToTypeConverter.put(long.class, XPathUtils.getConverter(ToLongConverter.class));
        defaultToTypeConverter.put(short.class, XPathUtils.getConverter(ToShortConverter.class));
        defaultToTypeConverter.put(double.class, XPathUtils.getConverter(ToDoubleConverter.class));
        defaultToTypeConverter.put(float.class, XPathUtils.getConverter(ToFloatConverter.class));
        defaultToTypeConverter.put(boolean.class, XPathUtils.getConverter(ToBooleanConverter.class));
        defaultToTypeConverter.put(char.class, XPathUtils.getConverter(ToCharacterConverter.class));
        defaultToTypeConverter.put(LocalDate.class, XPathUtils.getConverter(ToLocalDateConverter.class));
        defaultToTypeConverter.put(LocalDateTime.class, XPathUtils.getConverter(ToLocalDateTimeConverter.class));
        defaultToTypeConverter.put(LocalTime.class, XPathUtils.getConverter(ToLocalTimeConverter.class));
        defaultToTypeConverter.put(ZonedDateTime.class, XPathUtils.getConverter(ToZonedDateTimeConverter.class));
    }

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
        return unmarshal(XPathUtils.getRootElement(xml), type);
    }

    /**
     * unmarshalls from {@link java.io.InputStream InputStream}
     *
     * @param inputStream
     *            {@link java.io.InputStream InputStream} to unmarshal from
     * @param type
     *            the type of the class with the annotated fields
     * @return new instance of the given type unmarshalled by the given
     *         {@link java.io.InputStream InputStream}
     * @throws ParsingException
     *             if the {@link java.io.InputStream InputStream} can not be
     *             parsed (invalid xml)
     */
    public <T> T unmarshal(InputStream inputStream, Class<T> type) throws ParsingException
    {
        return unmarshal(XPathUtils.getRootElement(inputStream), type);
    }

    /**
     * unmarshalls from {@link java.io.Reader Reader}
     *
     * @param inputStream
     *            {@link java.io.Reader Reader} to unmarshal from
     * @param type
     *            the type of the class with the annotated fields
     * @return new instance of the given type unmarshalled by the given
     *         {@link java.io.Reader Reader}
     * @throws ParsingException
     *             if the {@link java.io.Reader Reader} can not be parsed
     *             (invalid xml)
     */
    public <T> T unmarshal(Reader reader, Class<T> type) throws ParsingException
    {
        return unmarshal(XPathUtils.getRootElement(reader), type);
    }

    /**
     * unmarshalls from {@link java.io.File File}
     *
     * @param file
     *            {@link java.io.File File} to unmarshal from
     * @param type
     *            the type of the class with the annotated fields
     * @return new instance of the given type unmarshalled by the given
     *         {@link java.io.File File}
     * @throws ParsingException
     *             if the {@link java.io.File File} can not be parsed (invalid
     *             xml)
     */
    public <T> T unmarshal(File file, Class<T> type) throws ParsingException
    {
        return unmarshal(XPathUtils.getRootElement(file), type);
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
            XPathList xPathList = field.getAnnotation(XPathList.class);
            if (xPathList != null)
            {
                List<?> value = XPathUtils.queryList(root, xPathList.value(), xPathList.converterClass());
                if (!setFieldValueSimple(field, target, value))
                {
                    throw new RuntimeException("could not set value " + value + " for field " + field + " on target " + target + " (field-types not matching)");
                }
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
            else
            {
                if (ReflectionUtils.isAssignable(field.getType(), value.getClass()))
                {
                    makeAccessible(field);
                    field.set(target, value);
                    return true;
                }
                else if ((value.getClass() == String.class) && !ReflectionUtils.isAssignable(field.getType(), value.getClass()))
                {
                    Function<String, ?> converter = getDefaultConverterToType(field.getType());
                    if (converter != null)
                    {
                        makeAccessible(field);
                        field.set(target, converter.apply((String) value));
                        return true;
                    }
                }
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
     * Registers a default {@link java.util.function.Function Converter} to use
     * for unmarshalling when the value (after an annotated
     * {@link java.util.function.Function Converter}-conversion) is a
     * {@link java.lang.String String } but the field-type is not. So when the
     * registered default-{@link java.util.function.Function Converter}s contain
     * a matching one for the wanted field-type, it is used to convert the value
     * to the wanted type
     *
     * @param type
     *            the type that matches the field-type and the given
     *            {@link java.util.function.Function Converter}-result-type
     * @param converter
     *            the {@link java.util.function.Function Converter} to use for
     *            converting to the given type
     */
    public static void registerDefaultConverterInstanceToType(Class<?> type, Function<String, ?> converter)
    {
        defaultToTypeConverter.put(type, converter);
    }

    private static Function<String, ?> getDefaultConverterToType(Class<?> type)
    {
        return defaultToTypeConverter.get(type);
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