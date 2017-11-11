package com.avides.xpath.utils.converter;

import java.time.LocalTime;
import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.time.LocalTime LocalTime}. If the value is <code>null</code>,
 * <code>null</code> is returned. If the value can not be converted to a
 * {@link java.time.LocalTime LocalTime}, a
 * {@link java.time.format.DateTimeParseException} is thrown.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToLocalTimeConverter implements Function<String, LocalTime>
{
    @Override
    public LocalTime apply(String t)
    {
        return t != null ? LocalTime.parse(t) : null;
    }
}