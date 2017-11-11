package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ToFloatConverterTest
{
    private ToFloatConverter toFloatConverter = new ToFloatConverter();

    @Test
    public void testApply()
    {
        assertThat(toFloatConverter.apply("19.99")).isEqualTo(19.99f);
        assertThat(toFloatConverter.apply(" -19.99 ")).isEqualTo(-19.99f);
        assertThat(toFloatConverter.apply(" -123,456 ")).isEqualTo(-123.456f);
        assertThat(toFloatConverter.apply(null)).isNull();
    }
}