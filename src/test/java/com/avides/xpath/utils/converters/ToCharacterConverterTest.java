package com.avides.xpath.utils.converters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.avides.xpath.utils.converters.ToCharacterConverter;

public class ToCharacterConverterTest
{
    private ToCharacterConverter toCharacterConverter = new ToCharacterConverter();

    @Test
    public void testApply()
    {
        assertThat(toCharacterConverter.apply("X")).isEqualTo('X');
        assertThat(toCharacterConverter.apply("anyString")).isEqualTo('a');
        assertThat(toCharacterConverter.apply(" anyString ")).isEqualTo(' ');
        assertThat(toCharacterConverter.apply("")).isNull();
        assertThat(toCharacterConverter.apply(null)).isNull();
    }
}