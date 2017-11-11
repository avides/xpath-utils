package com.avides.xpath.utils.converter;

import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.time.ZonedDateTime ZonedDateTime}. If the value is
 * <code>null</code>, <code>null</code> is returned. If the value can not be
 * converted to a {@link java.time.ZonedDateTime ZonedDateTime}, a
 * {@link java.time.format.DateTimeParseException} is thrown.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToZonedDateTimeConverter implements Function<String, ZonedDateTime>
{
    @Override
    public ZonedDateTime apply(String t)
    {
        return t != null ? ZonedDateTime.parse(t) : null;
    }
}