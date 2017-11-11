package com.avides.xpath.utils.converter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.lang.Boolean Boolean}. Allowed values for
 * {@link java.lang.Boolean#TRUE TRUE} are (not case-sensitive): 'true', 'yes',
 * 'on', '1', 'positive', 'correct', 'ja', 'oui', 'si', 'sì'. If the value is
 * <code>null</code>, <code>null</code> will be returned. All other values will
 * result to {@link java.lang.Boolean#FALSE FALSE}
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToBooleanConverter implements Function<String, Boolean>
{
    private static final List<String> trueValues = Arrays.asList("true", "yes", "on", "1", "positive", "correct", "ja", "oui", "si", "sì");

    @Override
    public Boolean apply(String t)
    {
        return t != null ? Boolean.valueOf(trueValues.contains(t.trim().toLowerCase())) : null;
    }
}