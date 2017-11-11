package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

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