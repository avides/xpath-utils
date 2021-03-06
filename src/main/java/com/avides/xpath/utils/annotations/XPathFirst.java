package com.avides.xpath.utils.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Function;

import com.avides.xpath.utils.converters.NoneConverter;

/**
 * Annotation to set on fields which should be enriched by xPath-query-results.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 *
 * @see com.avides.xpath.utils.XPathUnmarshaller
 * @see com.avides.xpath.utils.XPathUtils#queryFirst(nu.xom.Node, String, Class)
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface XPathFirst
{
    /**
     * the xPath-query to be executed to get the first found node-value (or
     * <code>null</code> if nothing is found)
     *
     * @return the xPath-query
     */
    String value();

    /**
     * the type of a {@link Function Converter} to convert the value with.
     * Default: {@link NoneConverter} which does not convert anything
     *
     * @return the type of a {@link Function Converter}
     */
    Class<? extends Function<String, ?>> converterClass() default NoneConverter.class;

    /**
     * if set to true, {@link #converterClass()} is ignored and the found
     * {@link nu.xom.Node Node} will be unmarshalled to the field-type of the
     * field on which this annotation is set. Default: <code>false</code>
     *
     * @return true to unmarshall to the field-type
     */
    boolean isSubType() default false;
}
