package com.avides.xpath.utils.converters;

import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.lang.Long Long}. If the value is <code>null</code>,
 * <code>null</code> is returned. If the value can not be converted to a
 * {@link java.lang.Long Long}, a {@link java.lang.NumberFormatException} is
 * thrown. Values are trimmed before conversion.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToLongConverter implements Function<String, Long>
{
    @Override
    public Long apply(String t)
    {
        return t != null ? Long.valueOf(t.trim()) : null;
    }
}