package com.avides.xpath.utils.testsupport;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.junit.Before;

import com.avides.xpath.utils.XPathUtils;
import com.avides.xpath.utils.annotations.XPathFirst;
import com.avides.xpath.utils.annotations.XPathList;
import com.avides.xpath.utils.annotations.XPathMap;
import com.avides.xpath.utils.converters.ToIntegerConverter;

import nu.xom.Element;
import nu.xom.ParsingException;

public abstract class XPathTestSupport
{
    protected Element root;
    protected String xml;

    @Before
    public void setUp() throws ParsingException, IOException
    {
        File xmlFile = new File(getClass().getClassLoader().getResource("test.xml").getFile());
        byte[] encoded = Files.readAllBytes(xmlFile.toPath());
        xml = new String(encoded, Charset.forName("UTF-8"));
        root = XPathUtils.getRootElement(xmlFile);
    }

    protected void assertAnyObjectIsCorrect(AnyObject anyObject)
    {
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
        assertThat(anyObject.getAnySubObject().getStringValue()).isEqualTo("anySubObjectStringValue");
        assertThat(anyObject.getAnyNotExistingSubObject()).isNull();
        assertThat(anyObject.getTypedMap()).hasSize(2)
            .containsEntry(new AnySubObject("anySubMapKey1"), new AnySubObject("anySubMapValue1"))
            .containsEntry(new AnySubObject("anySubMapKey2"), new AnySubObject("anySubMapValue2"));
        assertThat(anyObject.getTypedList()).containsExactly(new AnySubObject("anySubListValue1"), new AnySubObject("anySubListValue2"));
    }

    public static class AnyObject
    {
        @XPathFirst(value = "notExistingInt", converterClass = ToIntegerConverter.class)
        private int notExistingInt;

        @XPathFirst("singleString")
        private String anyString;

        @XPathFirst(value = "singleInt", converterClass = ToIntegerConverter.class)
        private Integer anyInteger;

        @XPathFirst(value = "singleInt")
        private Integer anyIntegerWithoutGivenConverterClass;

        @XPathFirst(value = "singleInt", converterClass = ToIntegerConverter.class)
        private int anyInt;

        @XPathFirst(value = "singleInt")
        private int anyIntWithoutGivenConverterClass;

        @XPathFirst(value = "notExistingString")
        private String notExistingString;

        @XPathFirst(value = "notExistingInteger", converterClass = ToIntegerConverter.class)
        private Integer notExistingInteger;

        @XPathMap(entryXPath = "map/entry", keySubXPath = "string[1]", valueSubXPath = "string[2]")
        private Map<String, String> anyStringMap;

        @XPathMap(entryXPath = "map/entry", keySubXPath = "string[1]", valueSubXPath = "string[2]", valueConverterClass = ToIntegerConverter.class)
        private Map<String, Integer> anyIntegerMap;

        @XPathList("list/value")
        private List<String> anyStringList;

        @XPathList(value = "list/value", converterClass = ToIntegerConverter.class)
        private List<Integer> anyIntegerList;

        @XPathFirst("singleEnum")
        private AnyEnum anyEnum;

        @XPathFirst("emptyValue")
        private AnyEnum emptyEnum;

        @XPathFirst(value = "subObject", isSubType = true)
        private AnySubObject anySubObject;

        @XPathFirst(value = "notExistingSubObject", isSubType = true)
        private AnySubObject anyNotExistingSubObject;

        @XPathMap(entryXPath = "subMap/entry", keySubXPath = "key", valueSubXPath = "value", keySubTypeClass = AnySubObject.class,
            valueSubTypeClass = AnySubObject.class)
        private Map<AnySubObject, AnySubObject> typedMap;

        @XPathList(value = "subList/value", subTypeClass = AnySubObject.class)
        private List<AnySubObject> typedList;

        public String getAnyString()
        {
            return anyString;
        }

        public Integer getAnyInteger()
        {
            return anyInteger;
        }

        public Integer getAnyIntegerWithoutGivenConverterClass()
        {
            return anyIntegerWithoutGivenConverterClass;
        }

        public int getAnyInt()
        {
            return anyInt;
        }

        public int getAnyIntWithoutGivenConverterClass()
        {
            return anyIntWithoutGivenConverterClass;
        }

        public String getNotExistingString()
        {
            return notExistingString;
        }

        public Integer getNotExistingInteger()
        {
            return notExistingInteger;
        }

        public int getNotExistingInt()
        {
            return notExistingInt;
        }

        public Map<String, String> getAnyStringMap()
        {
            return anyStringMap;
        }

        public Map<String, Integer> getAnyIntegerMap()
        {
            return anyIntegerMap;
        }

        public List<String> getAnyStringList()
        {
            return anyStringList;
        }

        public List<Integer> getAnyIntegerList()
        {
            return anyIntegerList;
        }

        public AnyEnum getAnyEnum()
        {
            return anyEnum;
        }

        public AnyEnum getEmptyEnum()
        {
            return emptyEnum;
        }

        public AnySubObject getAnySubObject()
        {
            return anySubObject;
        }

        public AnySubObject getAnyNotExistingSubObject()
        {
            return anyNotExistingSubObject;
        }

        public Map<AnySubObject, AnySubObject> getTypedMap()
        {
            return typedMap;
        }

        public List<AnySubObject> getTypedList()
        {
            return typedList;
        }
    }

    public static class AnySubObject
    {
        @XPathFirst("singleString")
        private String stringValue;

        public AnySubObject(String stringValue)
        {
            this.stringValue = stringValue;
        }

        public AnySubObject()
        {
            // no-args-constructor needed for unmarshalling
        }

        public String getStringValue()
        {
            return stringValue;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((stringValue == null) ? 0 : stringValue.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            AnySubObject other = (AnySubObject) obj;
            if (stringValue == null)
            {
                if (other.stringValue != null)
                {
                    return false;
                }
            }
            else if (!stringValue.equals(other.stringValue))
            {
                return false;
            }
            return true;
        }
    }

    public static enum AnyEnum
    {
        ENUM_VALUE1, ENUM_VALUE2;
    }
}
