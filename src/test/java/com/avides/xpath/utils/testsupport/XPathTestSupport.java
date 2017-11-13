package com.avides.xpath.utils.testsupport;

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
import com.avides.xpath.utils.converter.ToIntegerConverter;

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
    }

    public static enum AnyEnum
    {
        ENUM_VALUE1, ENUM_VALUE2;
    }
}