package com.avides.xpath.utils.converter;

import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.lang.Float Float}. If the value is <code>null</code>,
 * <code>null</code> is returned. If the value can not be converted to a
 * {@link java.lang.Float Float}, a {@link java.lang.NumberFormatException} is
 * thrown. Values are trimmed before conversion, commas are replaced by dots.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToFloatConverter implements Function<String, Float>
{
    @Override
    public Float apply(String t)
    {
        return t != null ? Float.valueOf(t.trim().replace(',', '.')) : null;
    }
}