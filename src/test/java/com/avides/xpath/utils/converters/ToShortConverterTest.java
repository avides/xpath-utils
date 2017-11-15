package com.avides.xpath.utils.converters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.avides.xpath.utils.converters.ToShortConverter;

public class ToShortConverterTest
{
    private ToShortConverter toShortConverter = new ToShortConverter();

    @Test
    public void testApply()
    {
        assertThat(toShortConverter.apply("123")).isEqualTo((short) 123);
        assertThat(toShortConverter.apply(" -123 ")).isEqualTo((short) -123);
        assertThat(toShortConverter.apply(null)).isNull();
    }
}