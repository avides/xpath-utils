package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class ToLocalTimeConverterTest
{
    private ToLocalTimeConverter toLocalTimeConverter = new ToLocalTimeConverter();

    @Test
    public void testApply()
    {
        assertThat(toLocalTimeConverter.apply("15:55:32")).isEqualTo(LocalTime.of(15, 55, 32));
        assertThat(toLocalTimeConverter.apply(null)).isNull();
    }

    @Test
    public void testApplyWithGivenFormatter()
    {
        ToLocalTimeConverter converter = new ToLocalTimeConverter(DateTimeFormatter.ofPattern("HH-mm-ss"));

        assertThat(converter.apply("15-55-32")).isEqualTo(LocalTime.of(15, 55, 32));
        assertThat(converter.apply(null)).isNull();
    }
}