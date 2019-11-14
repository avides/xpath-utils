package com.avides.xpath.utils.converters;

import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.lang.Double Double}. If the value is <code>null</code>,
 * <code>null</code> is returned. If the value can not be converted to a
 * {@link java.lang.Double Double}, a {@link java.lang.NumberFormatException} is
 * thrown. Values are trimmed before conversion, commas are replaced by dots.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToDoubleConverter implements Function<String, Double>
{
    @Override
    public Double apply(String t)
    {
        return t != null ? Double.valueOf(t.trim().replace(',', '.')) : null;
    }
}
