package com.avides.xpath.utils.converters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.avides.xpath.utils.converters.ToIntegerConverter;

public class ToIntegerConverterTest
{
    private ToIntegerConverter toIntegerConverter = new ToIntegerConverter();

    @Test
    public void testApply()
    {
        assertThat(toIntegerConverter.apply("123")).isEqualTo(123);
        assertThat(toIntegerConverter.apply(" -123 ")).isEqualTo(-123);
        assertThat(toIntegerConverter.apply(null)).isNull();
    }
}