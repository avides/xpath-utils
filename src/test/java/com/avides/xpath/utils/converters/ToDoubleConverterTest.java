package com.avides.xpath.utils.converters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.avides.xpath.utils.converters.ToDoubleConverter;

public class ToDoubleConverterTest
{
    private ToDoubleConverter toDoubleConverter = new ToDoubleConverter();

    @Test
    public void testApply()
    {
        assertThat(toDoubleConverter.apply("19.99")).isEqualTo(19.99);
        assertThat(toDoubleConverter.apply(" -19.99 ")).isEqualTo(-19.99);
        assertThat(toDoubleConverter.apply(" -123,456 ")).isEqualTo(-123.456);
        assertThat(toDoubleConverter.apply(null)).isNull();
    }
}
