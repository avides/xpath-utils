package com.avides.xpath.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.Test;

import com.avides.xpath.utils.converters.NoneConverter;
import com.avides.xpath.utils.converters.ToIntegerConverter;
import com.avides.xpath.utils.testsupport.XPathTestSupport;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

public class XPathUtilsTest extends XPathTestSupport
{
    @Test
    public void testQueryFirst()
    {
        assertThat(XPathUtils.queryFirst(root, "singleString")).isEqualTo("anyStringValue");
        assertThat(XPathUtils.queryFirst(root, "notExisting")).isNull();
        assertThat(XPathUtils.queryFirst(root, "notExisting", ToIntegerConverter.class)).isNull();
        assertThat(XPathUtils.queryFirst(root, "singleInt", ToIntegerConverter.class)).isEqualTo(123);
        assertThat(XPathUtils.queryFirst(root, "notExisting", ToIntegerConverter.class)).isNull();
    }

    @Test
    public void testQueryMap()
    {
        assertThat(XPathUtils.queryMap(root, "map/entry", "string[1]", "string[2]")).hasSize(4)
            .containsEntry("anyKey1", "234")
            .containsEntry("anyKey2", "345")
            .containsEntry("anyKey3", null)
            .containsEntry("anyKey4", "456");
        assertThat(XPathUtils.queryMap(root, "map/entry", "string[1]", "string[2]", NoneConverter.class, ToIntegerConverter.class)).hasSize(4)
            .containsEntry("anyKey1", Integer.valueOf(234))
            .containsEntry("anyKey2", Integer.valueOf(345))
            .containsEntry("anyKey3", null)
            .containsEntry("anyKey4", Integer.valueOf(456));
    }

    @Test
    public void testQueryValueTypedMap()
    {
        assertThat(XPathUtils.queryValueTypedMap(root, "valueTypedMap/entry", "string", "subObject", AnySubObject.class)).hasSize(4)
            .containsEntry("123", new AnySubObject("234"))
            .containsEntry("234", new AnySubObject("345"))
            .containsEntry("345", null)
            .containsEntry("456", new AnySubObject("456"));
    }

    @Test
    public void testQueryValueTypedMapToKnownType()
    {
        assertThat(XPathUtils.queryValueTypedMap(root, "valueTypedMap/entry", "string", "subObject/singleString", Integer.class)).hasSize(4)
            .containsEntry("123", Integer.valueOf(234))
            .containsEntry("234", Integer.valueOf(345))
            .containsEntry("345", null)
            .containsEntry("456", Integer.valueOf(456));
    }

    @Test
    public void testQueryValueTypedMapWithKeyConverter()
    {
        assertThat(XPathUtils.queryValueTypedMap(root, "valueTypedMap/entry", "string", "subObject", ToIntegerConverter.class, AnySubObject.class)).hasSize(4)
            .containsEntry(Integer.valueOf(123), new AnySubObject("234"))
            .containsEntry(Integer.valueOf(234), new AnySubObject("345"))
            .containsEntry(Integer.valueOf(345), null)
            .containsEntry(Integer.valueOf(456), new AnySubObject("456"));
    }

    @Test
    public void testQueryKeyTypedMap()
    {
        assertThat(XPathUtils.queryKeyTypedMap(root, "keyTypedMap/entry", "subObject", "string", AnySubObject.class)).hasSize(4)
            .containsEntry(new AnySubObject("123"), "123")
            .containsEntry(new AnySubObject("234"), "234")
            .containsEntry(null, "345")
            .containsEntry(new AnySubObject("345"), "456");
    }

    @Test
    public void testQueryKeyTypedMapWithKnownType()
    {
        assertThat(XPathUtils.queryKeyTypedMap(root, "keyTypedMap/entry", "subObject/singleString", "string", Integer.class)).hasSize(4)
            .containsEntry(Integer.valueOf(123), "123")
            .containsEntry(Integer.valueOf(234), "234")
            .containsEntry(null, "345")
            .containsEntry(Integer.valueOf(345), "456");
    }

    @Test
    public void testQueryKeyTypedMapWithValueConverter()
    {
        assertThat(XPathUtils.queryKeyTypedMap(root, "keyTypedMap/entry", "subObject", "string", AnySubObject.class, ToIntegerConverter.class)).hasSize(4)
            .containsEntry(new AnySubObject("123"), Integer.valueOf(123))
            .containsEntry(new AnySubObject("234"), Integer.valueOf(234))
            .containsEntry(null, Integer.valueOf(345))
            .containsEntry(new AnySubObject("345"), Integer.valueOf(456));
    }

    @Test
    public void testQueryTypedMap()
    {
        assertThat(XPathUtils.queryTypedMap(root, "typedMap/entry", "subObject[1]", "subObject[2]", AnySubObject.class, AnySubObject.class)).hasSize(4)
            .containsEntry(new AnySubObject("anyKey1"), new AnySubObject("anyValue1"))
            .containsEntry(new AnySubObject("anyKey2"), new AnySubObject("anyValue2"))
            .containsEntry(new AnySubObject("anyKey3"), null)
            .containsEntry(new AnySubObject("anyKey4"), new AnySubObject("anyValue4"));
    }

    @Test
    public void testQueryList()
    {
        assertThat(XPathUtils.queryList(root, "list/value")).containsExactly("567", "678", "789");
        assertThat(XPathUtils.queryList(root, "list/value", ToIntegerConverter.class)).containsExactly(Integer.valueOf(567), Integer.valueOf(678),
            Integer.valueOf(789));
    }

    @Test
    public void testQueryTypesList()
    {
        assertThat(XPathUtils.queryTypedList(root, "typedList/subObject", AnySubObject.class)).containsExactly(new AnySubObject("123"), new AnySubObject("234"),
            new AnySubObject("345"));
    }

    @Test
    public void testQueryTypedListWithKnownType()
    {
        assertThat(XPathUtils.queryTypedList(root, "list/value", Integer.class)).containsExactly(Integer.valueOf(567), Integer.valueOf(678),
            Integer.valueOf(789));
    }

    @Test
    public void testQueryListWithoutConverter()
    {
        assertThat(XPathUtils.queryList(root, "list/value")).containsExactly("567", "678", "789");
        assertThat(XPathUtils.queryList(root, "list/value", null)).containsExactly(null, null, null);
    }

    @Test
    public void testFromXml() throws ParsingException
    {
        AnyObject anyObject = XPathUtils.fromXml(xml, AnyObject.class);
        assertAnyObjectIsCorrect(anyObject);
    }

    @Test
    public void testFromRoot()
    {
        AnyObject anyObject = XPathUtils.fromRoot(root, AnyObject.class);
        assertAnyObjectIsCorrect(anyObject);
    }

    @Test
    public void testFromInputStream() throws ParsingException, IOException
    {
        AnyObject anyObject = XPathUtils.fromInputStream(getClass().getClassLoader().getResource("test.xml").openStream(), AnyObject.class);
        assertAnyObjectIsCorrect(anyObject);
    }

    @Test
    public void testFromReader() throws ParsingException
    {
        AnyObject anyObject = XPathUtils.fromReader(new StringReader(xml), AnyObject.class);
        assertAnyObjectIsCorrect(anyObject);
    }

    @Test
    public void testFromFile() throws ParsingException
    {
        AnyObject anyObject = XPathUtils.fromFile(new File(getClass().getClassLoader().getResource("test.xml").getFile()), AnyObject.class);
        assertAnyObjectIsCorrect(anyObject);
    }

    @Test(expected = ParsingException.class)
    public void testGetRootElementFromXml() throws ParsingException
    {
        XPathUtils.getRootElement("anyInvalidXml");
    }

    @Test
    public void testGetRootElementFromReader() throws ParsingException
    {
        assertThat(XPathUtils.getRootElement(new StringReader("<root></root>")).getLocalName()).isEqualTo("root");
    }

    @Test
    public void testGetRootElementFromFile() throws ParsingException
    {
        assertThat(XPathUtils.getRootElement(new File(getClass().getClassLoader().getResource("test.xml").getFile())).getLocalName()).isEqualTo("root");
    }

    @Test
    public void testQueryNodes()
    {
        Nodes nodes = XPathUtils.queryNodes(root, "list/value");
        assertThat(nodes.size()).isEqualTo(3);
        assertThat(nodes.get(0).getValue()).isEqualTo("567");
        assertThat(nodes.get(1).getValue()).isEqualTo("678");
        assertThat(nodes.get(2).getValue()).isEqualTo("789");
    }

    @Test
    public void testQueryNodeList()
    {
        List<Node> nodes = XPathUtils.queryNodeList(root, "list/value");
        assertThat(nodes).hasSize(3);
        assertThat(nodes.get(0).getValue()).isEqualTo("567");
        assertThat(nodes.get(1).getValue()).isEqualTo("678");
        assertThat(nodes.get(2).getValue()).isEqualTo("789");
    }

    @Test
    public void testQueryElementList()
    {
        List<Element> elements = XPathUtils.queryElementList(root, "list/value");
        assertThat(elements).hasSize(3);
        assertThat(elements.get(0).getValue()).isEqualTo("567");
        assertThat(elements.get(1).getValue()).isEqualTo("678");
        assertThat(elements.get(2).getValue()).isEqualTo("789");
    }

    @Test
    public void testHasNodes()
    {
        assertThat(XPathUtils.hasNodes(root.query("list/value"))).isTrue();
        assertThat(XPathUtils.hasNodes(root.query("notExisting"))).isFalse();
        assertThat(XPathUtils.hasNodes(null)).isFalse();
    }

    @Test
    public void testHasNode()
    {
        assertThat(XPathUtils.hasNode(root, "singleString")).isTrue();
        assertThat(XPathUtils.hasNode(root, "notExisting")).isFalse();
    }

    @Test
    public void testQueryFirstNode()
    {
        Node node = XPathUtils.queryFirstNode(root, "singleString");
        assertThat(node.getValue()).isEqualTo("anyStringValue");
    }

    @Test
    public void testQueryFirstElement()
    {
        Element element = XPathUtils.queryFirstElement(root, "singleString");
        assertThat(element.getValue()).isEqualTo("anyStringValue");
    }

    @Test
    public void testQueryInteger()
    {
        assertThat(XPathUtils.queryInteger(root, "singleInt")).isEqualTo(123);
        assertThat(XPathUtils.queryInteger(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryLong()
    {
        assertThat(XPathUtils.queryLong(root, "singleInt")).isEqualTo(123);
        assertThat(XPathUtils.queryLong(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryShort()
    {
        assertThat(XPathUtils.queryShort(root, "singleInt")).isEqualTo((short) 123);
        assertThat(XPathUtils.queryShort(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryDouble()
    {
        assertThat(XPathUtils.queryDouble(root, "singleDouble")).isEqualTo(19.99);
        assertThat(XPathUtils.queryDouble(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryFloat()
    {
        assertThat(XPathUtils.queryFloat(root, "singleDouble")).isEqualTo(19.99f);
        assertThat(XPathUtils.queryFloat(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryBoolean()
    {
        assertThat(XPathUtils.queryBoolean(root, "singleBoolean")).isTrue();
        assertThat(XPathUtils.queryBoolean(root, "singleBoolean[2]")).isFalse();
        assertThat(XPathUtils.queryBoolean(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryCharacter()
    {
        assertThat(XPathUtils.queryCharacter(root, "singleString")).isEqualTo('a');
        assertThat(XPathUtils.queryCharacter(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryPrimitiveInteger()
    {
        assertThat(XPathUtils.queryPrimitiveInteger(root, "singleInt")).isEqualTo(123);
        assertThat(XPathUtils.queryPrimitiveInteger(root, "notExisting")).isZero();
    }

    @Test
    public void testQueryPrimitiveLong()
    {
        assertThat(XPathUtils.queryPrimitiveLong(root, "singleInt")).isEqualTo(123);
        assertThat(XPathUtils.queryPrimitiveLong(root, "notExisting")).isZero();
    }

    @Test
    public void testQueryPrimitiveShort()
    {
        assertThat(XPathUtils.queryPrimitiveShort(root, "singleInt")).isEqualTo((short) 123);
        assertThat(XPathUtils.queryPrimitiveShort(root, "notExisting")).isZero();
    }

    @Test
    public void testQueryPrimitiveDouble()
    {
        assertThat(XPathUtils.queryPrimitiveDouble(root, "singleDouble")).isEqualTo(19.99);
        assertThat(XPathUtils.queryPrimitiveDouble(root, "notExisting")).isZero();
    }

    @Test
    public void testQueryPrimitiveFloat()
    {
        assertThat(XPathUtils.queryPrimitiveFloat(root, "singleDouble")).isEqualTo(19.99f);
        assertThat(XPathUtils.queryPrimitiveFloat(root, "notExisting")).isZero();
    }

    @Test
    public void testQueryPrimitiveBoolean()
    {
        assertThat(XPathUtils.queryPrimitiveBoolean(root, "singleBoolean")).isTrue();
        assertThat(XPathUtils.queryPrimitiveBoolean(root, "singleBoolean[2]")).isFalse();
        assertThat(XPathUtils.queryPrimitiveBoolean(root, "notExisting")).isFalse();
    }

    @Test
    public void testQueryPrimitiveCharacter()
    {
        assertThat(XPathUtils.queryPrimitiveCharacter(root, "singleString")).isEqualTo('a');
        assertThat(XPathUtils.queryPrimitiveCharacter(root, "notExisting")).isEqualTo((char) 0);
    }

    @Test
    public void testQueryInt()
    {
        assertThat(XPathUtils.queryInt(root, "singleInt")).isEqualTo(123);
        assertThat(XPathUtils.queryInt(root, "notExisting")).isZero();
    }

    @Test
    public void testQueryBool()
    {
        assertThat(XPathUtils.queryBool(root, "singleBoolean")).isTrue();
        assertThat(XPathUtils.queryBool(root, "singleBoolean[2]")).isFalse();
        assertThat(XPathUtils.queryBool(root, "notExisting")).isFalse();
    }

    @Test
    public void testQueryChar()
    {
        assertThat(XPathUtils.queryChar(root, "singleString")).isEqualTo('a');
        assertThat(XPathUtils.queryChar(root, "notExisting")).isEqualTo((char) 0);
    }

    @Test
    public void testQueryZonedDateTime()
    {
        assertThat(XPathUtils.queryZonedDateTime(root, "zonedDateTime"))
            .isEqualTo(ZonedDateTime.ofLocal(LocalDateTime.of(2017, 11, 8, 15, 55, 32), ZoneOffset.UTC, ZoneOffset.UTC));
        assertThat(XPathUtils.queryZonedDateTime(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryLocalDateTime()
    {
        assertThat(XPathUtils.queryLocalDateTime(root, "localDateTime")).isEqualTo(LocalDateTime.of(2017, 11, 8, 15, 55, 32));
        assertThat(XPathUtils.queryLocalDateTime(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryLocalDate()
    {
        assertThat(XPathUtils.queryLocalDate(root, "localDate")).isEqualTo(LocalDate.of(2017, 11, 8));
        assertThat(XPathUtils.queryLocalDate(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryLocalTime()
    {
        assertThat(XPathUtils.queryLocalTime(root, "localTime")).isEqualTo(LocalTime.of(14, 36, 37));
        assertThat(XPathUtils.queryLocalTime(root, "notExisting")).isNull();
    }

    @Test
    public void testQueryEnum()
    {
        assertThat(XPathUtils.queryEnum(root, "singleEnum", AnyEnum.class)).isSameAs(AnyEnum.ENUM_VALUE2);
        assertThat(XPathUtils.queryEnum(root, "anyNotExisting", AnyEnum.class)).isNull();
        assertThat(XPathUtils.queryEnum(root, "emptyValue", AnyEnum.class)).isNull();
    }
}