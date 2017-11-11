package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.avides.xpath.utils.converter.NoneConverter;

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