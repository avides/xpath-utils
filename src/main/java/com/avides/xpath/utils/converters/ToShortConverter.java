package com.avides.xpath.utils.converters;

import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.lang.Short Short}. If the value is <code>null</code>,
 * <code>null</code> is returned. If the value can not be converted to a
 * {@link java.lang.Short Short}, a {@link java.lang.NumberFormatException} is
 * thrown. Values are trimmed before conversion.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToShortConverter implements Function<String, Short>
{
    @Override
    public Short apply(String t)
    {
        return t != null ? Short.valueOf(t.trim()) : null;
    }
}
