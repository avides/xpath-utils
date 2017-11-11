package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.Test;

public class ToLocalDateConverterTest
{
    private ToLocalDateConverter toLocalDateConverter = new ToLocalDateConverter();

    @Test
    public void testApply()
    {
        assertThat(toLocalDateConverter.apply("2017-11-08")).isEqualTo(LocalDate.of(2017, 11, 8));
        assertThat(toLocalDateConverter.apply(null)).isNull();
    }
}