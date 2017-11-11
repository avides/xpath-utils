package com.avides.xpath.utils.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.avides.xpath.utils.converter.ToBooleanConverter;

public class ToBooleanConverterTest
{
    private ToBooleanConverter toBooleanConverter = new ToBooleanConverter();

    @Test
    public void testApply()
    {
        assertThat(toBooleanConverter.apply("true")).isTrue();
        assertThat(toBooleanConverter.apply("yes")).isTrue();
        assertThat(toBooleanConverter.apply("on")).isTrue();
        assertThat(toBooleanConverter.apply("1")).isTrue();
        assertThat(toBooleanConverter.apply("positive")).isTrue();
        assertThat(toBooleanConverter.apply("correct")).isTrue();
        assertThat(toBooleanConverter.apply("ja")).isTrue();
        assertThat(toBooleanConverter.apply("oui")).isTrue();
        assertThat(toBooleanConverter.apply("si")).isTrue();
        assertThat(toBooleanConverter.apply("sì")).isTrue();
        assertThat(toBooleanConverter.apply("TRUE")).isTrue();
        assertThat(toBooleanConverter.apply("YES")).isTrue();
        assertThat(toBooleanConverter.apply("ON")).isTrue();
        assertThat(toBooleanConverter.apply("Positive")).isTrue();
        assertThat(toBooleanConverter.apply("Correct")).isTrue();
        assertThat(toBooleanConverter.apply("Ja")).isTrue();
        assertThat(toBooleanConverter.apply("OUI")).isTrue();
        assertThat(toBooleanConverter.apply("SI")).isTrue();
        assertThat(toBooleanConverter.apply("sì")).isTrue();
        assertThat(toBooleanConverter.apply(" true ")).isTrue();
        assertThat(toBooleanConverter.apply(" 1 ")).isTrue();
        assertThat(toBooleanConverter.apply(null)).isNull();
        assertThat(toBooleanConverter.apply("anyValue")).isFalse();
        assertThat(toBooleanConverter.apply("false")).isFalse();
        assertThat(toBooleanConverter.apply("no")).isFalse();
        assertThat(toBooleanConverter.apply("off")).isFalse();
        assertThat(toBooleanConverter.apply("0")).isFalse();
        assertThat(toBooleanConverter.apply("negative")).isFalse();
        assertThat(toBooleanConverter.apply("incorrect")).isFalse();
        assertThat(toBooleanConverter.apply("nein")).isFalse();
        assertThat(toBooleanConverter.apply("non")).isFalse();
        assertThat(toBooleanConverter.apply("none")).isFalse();
    }
}