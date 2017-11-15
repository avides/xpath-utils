package com.avides.xpath.utils.converters;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import com.avides.xpath.utils.converters.ToZonedDateTimeConverter;

public class ToZonedDateTimeConverterTest
{
    private ToZonedDateTimeConverter toZonedDateTimeConverter = new ToZonedDateTimeConverter();

    @Test
    public void testApply()
    {
        assertThat(toZonedDateTimeConverter.apply("2017-11-08T15:55:32Z"))
            .isEqualTo(ZonedDateTime.ofLocal(LocalDateTime.of(2017, 11, 8, 15, 55, 32), ZoneOffset.UTC, ZoneOffset.UTC));
        assertThat(toZonedDateTimeConverter.apply(null)).isNull();
    }

    @Test
    public void testApplyWithGivenFormatter()
    {
        ToZonedDateTimeConverter converter = new ToZonedDateTimeConverter(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ssX"));

        assertThat(converter.apply("08.11.2017 15:55:32Z"))
            .isEqualTo(ZonedDateTime.ofLocal(LocalDateTime.of(2017, 11, 8, 15, 55, 32), ZoneOffset.UTC, ZoneOffset.UTC));
        assertThat(converter.apply(null)).isNull();
    }
}