package com.avides.xpath.utils.converter;

import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that converts to a
 * {@link java.lang.Character Character}. If the found value is longer than 1
 * character, the first character will be returned. If the value is
 * <code>null</code> or the found value is empty, <code>null</code> will be
 * returned.
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class ToCharacterConverter implements Function<String, Character>
{
    @Override
    public Character apply(String t)
    {
        return (t != null) && (t.length() > 0) ? Character.valueOf(t.charAt(0)) : null;
    }
}