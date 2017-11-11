package com.avides.xpath.utils.converter;

import java.time.LocalDate;
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
    @Override
    public LocalDate apply(String t)
    {
        return t != null ? LocalDate.parse(t) : null;
    }
}