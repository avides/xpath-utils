package com.avides.xpath.utils.converters;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    private final DateTimeFormatter formatter;

    /**
     * Default-constructor to use the default formatter
     * {@link DateTimeFormatter#ISO_ZONED_DATE_TIME} for parsing
     */
    public ToZonedDateTimeConverter()
    {
        formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    }

    /**
     * Constructor to use a given {@link DateTimeFormatter DateTimeFormatter}
     *
     * @param formatter
     *            the {@link DateTimeFormatter DateTimeFormatter} to use for
     *            parsing
     */
    public ToZonedDateTimeConverter(DateTimeFormatter formatter)
    {
        this.formatter = formatter;
    }

    @Override
    public ZonedDateTime apply(String t)
    {
        return t != null ? ZonedDateTime.parse(t, formatter) : null;
    }
}
