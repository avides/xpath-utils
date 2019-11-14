package com.avides.xpath.utils.converters;

import java.util.function.Function;

/**
 * A converter ({@link java.util.function.Function Function}) that simply does
 * nothing, but is used as default for annotations
 * {@link com.avides.xpath.utils.annotations.XPathFirst XPathFirst},
 * {@link com.avides.xpath.utils.annotations.XPathList XPathList} and
 * {@link com.avides.xpath.utils.annotations.XPathMap XPathMap}
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 */
public class NoneConverter implements Function<String, String>
{
    @Override
    public String apply(String t)
    {
        return t;
    }
}
