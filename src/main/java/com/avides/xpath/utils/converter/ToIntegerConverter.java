package com.avides.xpath.utils.converter;

import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.lang.Integer Integer}. If the value is <code>null</code>,
 * <code>null</code> is returned. If the value can not be converted to an
 * {@link java.lang.Integer Integer}, a {@link java.lang.NumberFormatException}
 * is thrown. Values are trimmed before conversion.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToIntegerConverter implements Function<String, Integer>
{
    @Override
    public Integer apply(String t)
    {
        return t != null ? Integer.valueOf(t.trim()) : null;
    }
}