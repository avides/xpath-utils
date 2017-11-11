package com.avides.xpath.utils.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Function;

import com.avides.xpath.utils.converter.NoneConverter;

/**
 * Annotation to set on fields which should be enriched by xPath-query-results
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 *
 * @see {@link com.avides.xpath.utils.XPathUnmarshaller}
 * @see {@link com.avides.xpath.utils.XPathUtils#queryList(nu.xom.Node, String, Class)}
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface XPathList
{
    /**
     * the xPath-query to be executed to get a list of node-values
     */
    String value();

    /**
     * the type of a {@link java.util.function Function Converter} to convert
     * each list-item with. Default:
     * {@link com.avides.xpath.utils.converter.NoneConverter} which does not convert
     * anything
     */
    Class<? extends Function<String, ?>> converterClass() default NoneConverter.class;
}