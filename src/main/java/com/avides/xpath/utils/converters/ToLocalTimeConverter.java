package com.avides.xpath.utils.converters;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private final DateTimeFormatter formatter;

    /**
     * Default-constructor to use the default formatter
     * {@link DateTimeFormatter#ISO_LOCAL_TIME} for parsing
     */
    public ToLocalTimeConverter()
    {
        formatter = DateTimeFormatter.ISO_LOCAL_TIME;
    }

    /**
     * Constructor to use a given {@link DateTimeFormatter DateTimeFormatter}
     *
     * @param formatter
     *            the {@link DateTimeFormatter DateTimeFormatter} to use for
     *            parsing
     */
    public ToLocalTimeConverter(DateTimeFormatter formatter)
    {
        this.formatter = formatter;
    }

    @Override
    public LocalTime apply(String t)
    {
        return t != null ? LocalTime.parse(t, formatter) : null;
    }
}
