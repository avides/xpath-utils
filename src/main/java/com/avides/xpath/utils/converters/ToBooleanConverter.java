package com.avides.xpath.utils.converters;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.lang.Boolean Boolean}. The default allowed values for
 * {@link java.lang.Boolean#TRUE TRUE} are (not case-sensitive): 'true', 'yes',
 * 'on', '1', 'positive', 'correct', 'ja', 'oui', 'si', 'sì' (if the
 * default-constructor is used). If the value is <code>null</code>,
 * <code>null</code> will be returned. All other values will result to
 * {@link java.lang.Boolean#FALSE FALSE}
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToBooleanConverter implements Function<String, Boolean>
{
    private final Collection<String> trueValues;

    /**
     * Default constructor: Values for {@link java.lang.Boolean#TRUE TRUE} are
     * (not case-sensitive): 'true', 'yes', 'on', '1', 'positive', 'correct',
     * 'ja', 'oui', 'si', 'sì'
     */
    public ToBooleanConverter()
    {
        trueValues = Arrays.asList("true", "yes", "on", "1", "positive", "correct", "ja", "oui", "si", "sì");
    }

    /**
     * Constructor with the ability to give a {@link java.util.Collection
     * Collection} of {@link java.lang.String String} for the values (not
     * case-sensitive) for {@link java.lang.Boolean#TRUE TRUE}
     *
     * @param trueValues
     *            the values that lead to {@link java.lang.Boolean#TRUE TRUE}
     */
    public ToBooleanConverter(Collection<String> trueValues)
    {
        this.trueValues = trueValues;
    }

    /**
     * Constructor with the ability to give a var-args-parameter of
     * {@link java.lang.String String} for the values (not case-sensitive) for
     * {@link java.lang.Boolean#TRUE TRUE}
     *
     * @param trueValues
     *            the values that lead to {@link java.lang.Boolean#TRUE TRUE}
     */
    public ToBooleanConverter(String... trueValues)
    {
        this.trueValues = Arrays.asList(trueValues);
    }

    @Override
    public Boolean apply(String t)
    {
        return t != null ? Boolean.valueOf(trueValues.contains(t.trim().toLowerCase())) : null;
    }
}