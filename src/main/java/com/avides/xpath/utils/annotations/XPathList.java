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
 * @see com.avides.xpath.utils.XPathUtils#queryList(nu.xom.Node, String, Class)
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface XPathList
{
    /**
     * the xPath-query to be executed to get a list of node-values
     *
     * @return the xPath-query
     */
    String value();

    /**
     * the type of a {@link Function Converter} to convert each list-item with.
     * Default: {@link NoneConverter} which does not convert anything
     *
     * @return the type of a {@link Function Converter}
     */
    Class<? extends Function<String, ?>> converterClass() default NoneConverter.class;

    /**
     * if set to any other {@link Class} than {@link String String.class}, the
     * {@link #converterClass()} is ignored and the found {@link nu.xom.Nodes
     * Nodes} will be unmarshalled to the given {@link Class}. Default:
     * {@link String String.class}
     *
     * @return the generic-type of the list
     */
    Class<?> subTypeClass() default String.class;
}