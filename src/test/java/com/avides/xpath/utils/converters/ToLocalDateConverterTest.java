package com.avides.xpath.utils.converters;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import com.avides.xpath.utils.converters.ToLocalDateConverter;

public class ToLocalDateConverterTest
{
    private ToLocalDateConverter toLocalDateConverter = new ToLocalDateConverter();

    @Test
    public void testApply()
    {
        assertThat(toLocalDateConverter.apply("2017-11-08")).isEqualTo(LocalDate.of(2017, 11, 8));
        assertThat(toLocalDateConverter.apply(null)).isNull();
    }

    @Test
    public void testApplyWithGivenFormatter()
    {
        ToLocalDateConverter converter = new ToLocalDateConverter(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        assertThat(converter.apply("08.11.2017")).isEqualTo(LocalDate.of(2017, 11, 8));
        assertThat(converter.apply(null)).isNull();
    }
}