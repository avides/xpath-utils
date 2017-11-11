package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Test;

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
}