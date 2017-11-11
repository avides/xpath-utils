package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

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