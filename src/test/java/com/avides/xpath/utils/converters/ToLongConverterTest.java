package com.avides.xpath.utils.converters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.avides.xpath.utils.converters.ToLongConverter;

public class ToLongConverterTest
{
    private ToLongConverter toLongConverter = new ToLongConverter();

    @Test
    public void testApply()
    {
        assertThat(toLongConverter.apply("123")).isEqualTo(123);
        assertThat(toLongConverter.apply(" -123 ")).isEqualTo(-123);
        assertThat(toLongConverter.apply(null)).isNull();
    }
}
