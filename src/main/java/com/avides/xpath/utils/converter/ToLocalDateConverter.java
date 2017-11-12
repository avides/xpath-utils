package com.avides.xpath.utils.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.time.LocalDate LocalDate}. If the value is <code>null</code>,
 * <code>null</code> is returned. If the value can not be converted to a
 * {@link java.time.LocalDate LocalDate}, a
 * {@link java.time.format.DateTimeParseException} is thrown.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToLocalDateConverter implements Function<String, LocalDate>
{
    private final DateTimeFormatter formatter;

    /**
     * Default-constructor to use the default formatter
     * {@link DateTimeFormatter#ISO_LOCAL_DATE} for parsing
     */
    public ToLocalDateConverter()
    {
        formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    }

    /**
     * Constructor to use a given {@link DateTimeFormatter DateTimeFormatter}
     *
     * @param formatter
     *            the {@link DateTimeFormatter DateTimeFormatter} to use for
     *            parsing
     */
    public ToLocalDateConverter(DateTimeFormatter formatter)
    {
        this.formatter = formatter;
    }

    @Override
    public LocalDate apply(String t)
    {
        return t != null ? LocalDate.parse(t, formatter) : null;
    }
}