package com.avides.xpath.utils.converters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.avides.xpath.utils.converters.NoneConverter;

public class NoneConverterTest
{
    private NoneConverter noneConverter = new NoneConverter();

    @Test
    public void testApply()
    {
        assertThat(noneConverter.apply("anyString")).isEqualTo("anyString");
        assertThat(noneConverter.apply(null)).isNull();
    }
}