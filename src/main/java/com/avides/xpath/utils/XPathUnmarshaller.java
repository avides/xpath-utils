package com.avides.xpath.utils;

import static com.avides.xpath.utils.utils.ReflectionUtils.doWithFields;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avides.xpath.utils.annotations.XPathFirst;
import com.avides.xpath.utils.annotations.XPathList;
import com.avides.xpath.utils.annotations.XPathMap;
import com.avides.xpath.utils.converters.ToBooleanConverter;
import com.avides.xpath.utils.converters.ToCharacterConverter;
import com.avides.xpath.utils.converters.ToDoubleConverter;
import com.avides.xpath.utils.converters.ToFloatConverter;
import com.avides.xpath.utils.converters.ToIntegerConverter;
import com.avides.xpath.utils.converters.ToLocalDateConverter;
import com.avides.xpath.utils.converters.ToLocalDateTimeConverter;
import com.avides.xpath.utils.converters.ToLocalTimeConverter;
import com.avides.xpath.utils.converters.ToLongConverter;
import com.avides.xpath.utils.converters.ToShortConverter;
import com.avides.xpath.utils.converters.ToZonedDateTimeConverter;
import com.avides.xpath.utils.processors.XPathFirstProcessor;
import com.avides.xpath.utils.processors.XPathListProcessor;
import com.avides.xpath.utils.processors.XPathMapProcessor;

import nu.xom.Element;
import nu.xom.ParsingException;

/**
 * Class for unmarshalling xml to new instances of classes which have fields
 * that are annotated with {@link XPathFirst}, {@link XPathList} or
 * {@link XPathMap}. Such classes must have a public no-args-constructor
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 *
 * @see XPathUtils
 * @see XPathUtils#fromXml(String, Class)
 * @see XPathUtils#fromInputStream(InputStream, Class)
 * @see XPathUtils#fromReader(Reader, Class)
 * @see XPathUtils#fromFile(File, Class)
 * @see XPathUtils#fromRoot(Element, Class)
 */
public class XPathUnmarshaller
{
    private static final Logger log = LoggerFactory.getLogger(XPathUnmarshaller.class);

    private static XPathUnmarshaller instance;

    private static final Map<Class<?>, Function<String, ?>> defaultToTypeConverters = new HashMap<>();

    static
    {
        resetDefaultConverterInstancesToType();
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
     *
     * @since 1.0.0.RELEASE
     */
    public <T> T unmarshal(String xml, Class<T> type) throws ParsingException
    {
        return unmarshal(XPathUtils.getRootElement(xml), type);
    }

    /**
     * unmarshalls from {@link InputStream}
     *
     * @param inputStream
     *            {@link InputStream} to unmarshal from
     * @param type
     *            the type of the class with the annotated fields
     * @return new instance of the given type unmarshalled by the given
     *         {@link InputStream}
     * @throws ParsingException
     *             if the {@link InputStream} can not be parsed (invalid xml)
     *
     * @since 1.0.2.RELEASE
     */
    public <T> T unmarshal(InputStream inputStream, Class<T> type) throws ParsingException
    {
        return unmarshal(XPathUtils.getRootElement(inputStream), type);
    }

    /**
     * unmarshalls from {@link Reader}
     *
     * @param reader
     *            {@link Reader} to unmarshal from
     * @param type
     *            the type of the class with the annotated fields
     * @return new instance of the given type unmarshalled by the given
     *         {@link Reader}
     * @throws ParsingException
     *             if the {@link Reader} can not be parsed (invalid xml)
     *
     * @since 1.0.2.RELEASE
     */
    public <T> T unmarshal(Reader reader, Class<T> type) throws ParsingException
    {
        return unmarshal(XPathUtils.getRootElement(reader), type);
    }

    /**
     * unmarshalls from {@link File}
     *
     * @param file
     *            {@link File} to unmarshal from
     * @param type
     *            the type of the class with the annotated fields
     * @return new instance of the given type unmarshalled by the given
     *         {@link File}
     * @throws ParsingException
     *             if the {@link File} can not be parsed (invalid xml)
     *
     * @since 1.0.2.RELEASE
     */
    public <T> T unmarshal(File file, Class<T> type) throws ParsingException
    {
        return unmarshal(XPathUtils.getRootElement(file), type);
    }

    /**
     * unmarshalls from {@link Element}
     *
     * @param root
     *            {@link Element} to unmarshal from
     * @param type
     *            the type of the class with the annotated fields
     * @return new instance of the given type unmarshalled by the given
     *         {@link Element}
     *
     * @since 1.0.0.RELEASE
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
            new XPathFirstProcessor(root, field, target, defaultToTypeConverters).process();
            new XPathListProcessor(root, field, target, defaultToTypeConverters).process();
            new XPathMapProcessor(root, field, target, defaultToTypeConverters).process();
        });
        return target;
    }

    /**
     * Registers a default {@link Function Converter} to use for unmarshalling
     * when the value (after an annotated {@link Function Converter}-conversion)
     * is a {@link String} but the field-type is not. So when the registered
     * default-{@link Function Converter}s contain a matching one for the wanted
     * field-type, it is used to convert the value to the wanted type
     *
     * @param type
     *            the type that matches the field-type and the given
     *            {@link Function Converter}-result-type
     * @param converter
     *            the {@link Function Converter} to use for converting to the
     *            given type
     *
     * @since 1.0.2.RELEASE
     */
    public static void registerDefaultConverterInstanceToType(Class<?> type, Function<String, ?> converter)
    {
        defaultToTypeConverters.put(type, converter);
    }

    /**
     * Unregisters a {@link Function Converter} for default-conversion for the
     * given type
     *
     * @param type
     *            the type the {@link Function Converter} is registered to
     *            convert to
     *
     * @since 1.0.3.RELEASE
     *
     * @see #registerDefaultConverterInstanceToType(Class, Function)
     */
    public static void unregisterDefaultConverterInstanceToType(Class<?> type)
    {
        defaultToTypeConverters.remove(type);
    }

    /**
     * Resets all {@link Function Converter}s for default-conversion to the
     * default (clears all and puts the default ones)
     *
     * @since 1.0.3.RELEASE
     *
     * @see #registerDefaultConverterInstanceToType(Class, Function)
     */
    public static void resetDefaultConverterInstancesToType()
    {
        defaultToTypeConverters.clear();
        defaultToTypeConverters.put(Integer.class, XPathUtils.getConverter(ToIntegerConverter.class));
        defaultToTypeConverters.put(Long.class, XPathUtils.getConverter(ToLongConverter.class));
        defaultToTypeConverters.put(Short.class, XPathUtils.getConverter(ToShortConverter.class));
        defaultToTypeConverters.put(Double.class, XPathUtils.getConverter(ToDoubleConverter.class));
        defaultToTypeConverters.put(Float.class, XPathUtils.getConverter(ToFloatConverter.class));
        defaultToTypeConverters.put(Boolean.class, XPathUtils.getConverter(ToBooleanConverter.class));
        defaultToTypeConverters.put(Character.class, XPathUtils.getConverter(ToCharacterConverter.class));
        defaultToTypeConverters.put(int.class, XPathUtils.getConverter(ToIntegerConverter.class));
        defaultToTypeConverters.put(long.class, XPathUtils.getConverter(ToLongConverter.class));
        defaultToTypeConverters.put(short.class, XPathUtils.getConverter(ToShortConverter.class));
        defaultToTypeConverters.put(double.class, XPathUtils.getConverter(ToDoubleConverter.class));
        defaultToTypeConverters.put(float.class, XPathUtils.getConverter(ToFloatConverter.class));
        defaultToTypeConverters.put(boolean.class, XPathUtils.getConverter(ToBooleanConverter.class));
        defaultToTypeConverters.put(char.class, XPathUtils.getConverter(ToCharacterConverter.class));
        defaultToTypeConverters.put(LocalDate.class, XPathUtils.getConverter(ToLocalDateConverter.class));
        defaultToTypeConverters.put(LocalDateTime.class, XPathUtils.getConverter(ToLocalDateTimeConverter.class));
        defaultToTypeConverters.put(LocalTime.class, XPathUtils.getConverter(ToLocalTimeConverter.class));
        defaultToTypeConverters.put(ZonedDateTime.class, XPathUtils.getConverter(ToZonedDateTimeConverter.class));
    }

    /**
     * Returns the singleton-instance of the {@link XPathUnmarshaller}
     *
     * @return the singleton-instance of the {@link XPathUnmarshaller}
     *
     * @since 1.0.0.RELEASE
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