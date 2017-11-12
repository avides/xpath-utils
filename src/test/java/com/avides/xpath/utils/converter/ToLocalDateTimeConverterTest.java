package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @Test
    public void testApplyWithGivenFormatter()
    {
        ToLocalDateTimeConverter converter = new ToLocalDateTimeConverter(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        assertThat(converter.apply("08.11.2017 15:55:32")).isEqualTo(LocalDateTime.of(2017, 11, 8, 15, 55, 32));
        assertThat(converter.apply(null)).isNull();
    }
}