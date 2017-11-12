package com.avides.xpath.utils.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.time.LocalDateTime LocalDateTime}. If the value is
 * <code>null</code>, <code>null</code> is returned. If the value can not be
 * converted to a {@link java.time.LocalDateTime LocalDateTime}, a
 * {@link java.time.format.DateTimeParseException} is thrown.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToLocalDateTimeConverter implements Function<String, LocalDateTime>
{
    private final DateTimeFormatter formatter;

    /**
     * Default-constructor to use the default formatter
     * {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME} for parsing
     */
    public ToLocalDateTimeConverter()
    {
        formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }

    /**
     * Constructor to use a given {@link DateTimeFormatter DateTimeFormatter}
     *
     * @param formatter
     *            the {@link DateTimeFormatter DateTimeFormatter} to use for
     *            parsing
     */
    public ToLocalDateTimeConverter(DateTimeFormatter formatter)
    {
        this.formatter = formatter;
    }

    @Override
    public LocalDateTime apply(String t)
    {
        return t != null ? LocalDateTime.parse(t, formatter) : null;
    }
}