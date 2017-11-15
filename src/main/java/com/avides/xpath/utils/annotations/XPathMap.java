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
 * @see {@link com.avides.xpath.utils.XPathUnmarshaller}
 * @see {@link com.avides.xpath.utils.XPathUtils#queryMap(nu.xom.Node, String, String, String, Class, Class)}
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface XPathMap
{
    /**
     * the xPath-query to be executed to get all map-entries from
     */
    String entryXPath();

    /**
     * the xPath-query to be executed on each entry-node to get the entry-key
     * from
     */
    String keySubXPath();

    /**
     * the xPath-query to be executed on each entry-node to get the entry-value
     * from
     */
    String valueSubXPath();

    /**
     * the {@link Class} of a {@link Function Converter} to convert each
     * entry-key with. Default: {@link NoneConverter} which does not convert
     * anything
     */
    Class<? extends Function<String, ?>> keyConverterClass() default NoneConverter.class;

    /**
     * the {@link Class} of a {@link Function Converter} to convert each
     * entry-value with. Default: {@link NoneConverter} which does not convert
     * anything
     */
    Class<? extends Function<String, ?>> valueConverterClass() default NoneConverter.class;

    /**
     * if set to any other {@link Class} than {@link String String.class}, the
     * {@link #keyConverterClass()} is ignored and the found {@link nu.xom.Nodes
     * Nodes} for the keys will be unmarshalled to the given {@link Class}.
     * Default: {@link String String.class}
     */
    Class<?> keySubTypeClass() default String.class;

    /**
     * if set to any other {@link Class} than {@link String String.class}, the
     * {@link #valueConverterClass()} is ignored and the found
     * {@link nu.xom.Nodes Nodes} for the values will be unmarshalled to the
     * given {@link Class}. Default: {@link String String.class}
     */
    Class<?> valueSubTypeClass() default String.class;
}