package com.avides.xpath.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.avides.xpath.utils.testsupport.XPathTestSupport;

import nu.xom.ParsingException;

public class XPathUnmarshallerTest extends XPathTestSupport
{
    private XPathUnmarshaller unmarshaller = XPathUnmarshaller.getInstance();

    @Test
    public void testUnmarshal() throws ParsingException, IOException
    {
        AnyObject anyObject = unmarshaller.unmarshal(xml, AnyObject.class);

        assertThat(anyObject.getAnyString()).isEqualTo("anyStringValue");
        assertThat(anyObject.getAnyInteger()).isEqualTo(123);
        assertThat(anyObject.getAnyInt()).isEqualTo(123);
        assertThat(anyObject.getNotExistingString()).isNull();
        assertThat(anyObject.getNotExistingInteger()).isNull();
        assertThat(anyObject.getNotExistingInt()).isZero();
        assertThat(anyObject.getAnyStringMap()).hasSize(4)
            .containsEntry("anyKey1", "234")
            .containsEntry("anyKey2", "345")
            .containsEntry("anyKey3", null)
            .containsEntry("anyKey4", "456");
        assertThat(anyObject.getAnyIntegerMap()).hasSize(4)
            .containsEntry("anyKey1", Integer.valueOf(234))
            .containsEntry("anyKey2", Integer.valueOf(345))
            .containsEntry("anyKey3", null)
            .containsEntry("anyKey4", Integer.valueOf(456));
        assertThat(anyObject.getAnyStringList()).containsExactly("567", "678", "789");
        assertThat(anyObject.getAnyIntegerList()).containsExactly(Integer.valueOf(567), Integer.valueOf(678), Integer.valueOf(789));
        assertThat(anyObject.getAnyEnum()).isSameAs(AnyEnum.ENUM_VALUE2);
    }
}