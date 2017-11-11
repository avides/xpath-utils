package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.avides.xpath.utils.converter.ToCharacterConverter;

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