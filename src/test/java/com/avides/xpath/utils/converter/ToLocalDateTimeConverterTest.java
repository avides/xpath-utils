package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.Test;

public class ToLocalDateTimeConverterTest
{
    private ToLocalDateTimeConverter toLocalDateTimeConverter = new ToLocalDateTimeConverter();

    @Test
    public void testApply()
    {
        assertThat(toLocalDateTimeConverter.apply("2017-11-08T15:55:32")).isEqualTo(LocalDateTime.of(2017, 11, 8, 15, 55, 32));
        assertThat(toLocalDateTimeConverter.apply(null)).isNull();
    }
}