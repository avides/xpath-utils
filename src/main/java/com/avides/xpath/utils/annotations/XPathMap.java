package com.avides.xpath.utils.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Function;

import com.avides.xpath.utils.converters.NoneConverter;

/**
 * Annotation to set on fields which should be enriched by xPath-query-results
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 *
 * @see com.avides.xpath.utils.XPathUnmarshaller
 * @see com.avides.xpath.utils.XPathUtils#queryMap(nu.xom.Node, String, String,
 *      String, Class, Class)
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface XPathMap
{
    /**
     * the xPath-query to be executed to get all map-entries from
     *
     * @return the xPath-query for an entry
     */
    String entryXPath();

    /**
     * the xPath-query to be executed on each entry-node to get the entry-key
     * from
     *
     * @return the xPath-query for a key
     */
    String keySubXPath();

    /**
     * the xPath-query to be executed on each entry-node to get the entry-value
     * from
     *
     * @return the xPath-query for a value
     */
    String valueSubXPath();

    /**
     * the {@link Class} of a {@link Function Converter} to convert each
     * entry-key with. Default: {@link NoneConverter} which does not convert
     * anything
     *
     * @return the type of a {@link Function Converter} for the key
     */
    Class<? extends Function<String, ?>> keyConverterClass() default NoneConverter.class;

    /**
     * the {@link Class} of a {@link Function Converter} to convert each
     * entry-value with. Default: {@link NoneConverter} which does not convert
     * anything
     *
     * @return the type of a {@link Function Converter} for the value
     */
    Class<? extends Function<String, ?>> valueConverterClass() default NoneConverter.class;

    /**
     * if set to any other {@link Class} than {@link String String.class}, the
     * {@link #keyConverterClass()} is ignored and the found {@link nu.xom.Nodes
     * Nodes} for the keys will be unmarshalled to the given {@link Class}.
     * Default: {@link String String.class}
     *
     * @return the generic-type of the map-key
     */
    Class<?> keySubTypeClass() default String.class;

    /**
     * if set to any other {@link Class} than {@link String String.class}, the
     * {@link #valueConverterClass()} is ignored and the found
     * {@link nu.xom.Nodes Nodes} for the values will be unmarshalled to the
     * given {@link Class}. Default: {@link String String.class}
     *
     * @return the generic-type of the map-value
     */
    Class<?> valueSubTypeClass() default String.class;
}
