xpath-utils
===========

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.avides.xpath/xpath-utils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.avides.xpath/xpath-utils)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/49fe00fd4ec843b6ac21b2d3996f2de9)](https://www.codacy.com/app/developer_6/xpath-utils)
[![Coverage Status](https://coveralls.io/repos/github/avides/xpath-utils/badge.svg?branch=master)](https://coveralls.io/github/avides/xpath-utils?branch=master)
[![Build Status](https://travis-ci.org/avides/xpath-utils.svg?branch=master)](https://travis-ci.org/avides/xpath-utils)

#### Maven
```xml
<dependency>
    <groupId>com.avides.xpath</groupId>
    <artifactId>xpath-utils</artifactId>
    <version>1.0.3.RELEASE</version>
</dependency>
```
#### Available methods
```java
XPathUtils.getRootElement(InputStream inputStream);
XPathUtils.getRootElement(Reader reader);
XPathUtils.getRootElement(File file);
XPathUtils.getRootElement(String xml);

XPathUtils.queryBool(Node root, String xPath);
XPathUtils.queryBoolean(Node root, String xPath);
XPathUtils.queryChar(Node root, String xPath);
XPathUtils.queryCharacter(Node root, String xPath);
XPathUtils.queryDouble(Node root, String xPath);
XPathUtils.queryElementList(Node root, String xPath);
XPathUtils.queryEnum(Node root, String xPath, Class<E> enumClass);
XPathUtils.queryFirst(Node root, String xPath);
XPathUtils.queryFirst(Node root, String xPath, Class<? extends Function<String, T>> converterClass);
XPathUtils.queryFirstElement(Node root, String xPath);
XPathUtils.queryFirstNode(Node root, String xPath);
XPathUtils.queryFloat(Node root, String xPath);
XPathUtils.queryInt(Node root, String xPath);
XPathUtils.queryInteger(Node root, String xPath);
XPathUtils.queryList(Node root, String xPath);
XPathUtils.queryList(Node root, String xPath, Class<? extends Function<String, T>> converterClass);
XPathUtils.queryList(Node root, String xPath, Class<? extends Function<String, T>> converterClass, Class<T> subType);
XPathUtils.queryTypedList(Node root, String xPath, Class<T> subType);
XPathUtils.queryLocalDate(Node root, String xPath);
XPathUtils.queryLocalDateTime(Node root, String xPath);
XPathUtils.queryLocalTime(Node root, String xPath);
XPathUtils.queryLong(Node root, String xPath);
XPathUtils.queryMap(Node root, String, String, String);
XPathUtils.queryMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<? extends Function<String, K>> keyConverterClass, Class<? extends Function<String, V>> valueConverterClass);
XPathUtils.queryMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<? extends Function<String, K>> keyConverterClass, Class<? extends Function<String, V>> valueConverterClass, Class<K> keySubType, Class<V> valueSubType)
XPathUtils.queryTypedMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<K> keySubType, Class<V> valueSubType)
XPathUtils.queryKeyTypedMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<K> keySubType, Class<? extends Function<String, V>> valueConverterClass)
XPathUtils.queryKeyTypedMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<K> keySubType)
XPathUtils.queryValueTypedMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<? extends Function<String, K>> keyConverterClass, Class<V> valueSubType)
XPathUtils.queryValueTypedMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<V> valueSubType)
XPathUtils.queryNodeList(Node root, String xPath);
XPathUtils.queryNodeMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath)
XPathUtils.queryNodes(Node root, String xPath);
XPathUtils.queryPrimitiveBoolean(Node root, String xPath);
XPathUtils.queryPrimitiveCharacter(Node root, String xPath);
XPathUtils.queryPrimitiveDouble(Node root, String xPath);
XPathUtils.queryPrimitiveFloat(Node root, String xPath);
XPathUtils.queryPrimitiveInteger(Node root, String xPath);
XPathUtils.queryPrimitiveLong(Node root, String xPath);
XPathUtils.queryPrimitiveShort(Node root, String xPath);
XPathUtils.queryShort(Node root, String xPath);
XPathUtils.queryZonedDateTime(Node root, String xPath);

XPathUtils.hasNode(Node root, String xPath);
XPathUtils.hasNodes(Nodes nodes);

XPathUtils.each(Node root, String xPath);

// marshalling-methods:
XPathUtils.fromRoot(Element root, Class<T> type);
XPathUtils.fromInputStream(InputStream inputStream, Class<T> type);
XPathUtils.fromReader(Reader reader, Class<T> type);
XPathUtils.fromFile(File file, Class<T> type);
XPathUtils.fromXml(String xml, Class<T> type);

XPathUtils.registerConverterInstance(Function<String, T> converter);
XPathUtils.unregisterConverterInstance(Class<? extends Function<String, ?>> converterClass)
XPathUtils.clearConverterInstances()

XPathUtils.registerDefaultConverterInstanceToType(Class<?> type, Function<String, ?> converter)
XPathUtils.unregisterDefaultConverterInstanceToType(Class<?> type)
XPathUtils.resetDefaultConverterInstancesToType()
```
#### Examples
```xml
<xml>
    <anyStringValue>anyValue</anyStringValue>
    <anyIntegerValue>123</anyIntegerValue>
    <stringList>
        <value>listValue1</value>
        <value>listValue2</value>
        <value>listValue3</value>
    </stringList>
    <integerList>
        <value>10</value>
        <value>11</value>
        <value>12</value>
    </integerList>
    <stringMap>
        <entry>
            <string>key1</string>
            <string>value1</string>
        </entry>
        <entry>
            <string>key2</string>
            <string>value2</string>
        </entry>
    </stringMap>
    <integerMap>
        <entry>
            <string>key1</string>
            <string>100</string>
        </entry>
        <entry>
            <string>key2</string>
            <string>200</string>
        </entry>
    </integerMap>
</xml>
```
##### Simple example
```java
Element root = XPathUtils.getRootElementFromXml(xml);

String stringValue = XPathUtils.queryFirst(root, "anyStringValue");
Integer integerValue = XPathUtils.queryFirst(root, "anyIntegerValue", ToIntegerConverter.class);
List<String> stringList = XPathUtils.queryList(root, "stringList/value");
List<Integer> integerList = XPathUtils.queryList(root, "integerList/value");
Map<String, String> stringMap = XPathUtils.queryMap(root, "stringMap/entry", "string[1]", "string[2]");
Map<String, Integer> integerMap = XPathUtils.queryMap(root, "integerMap/entry", "string[1]", "string[2]", NoneConverter.class, ToIntegerConverter.class);

// should output 'anyValue - 123':
System.out.println(stringValue + " - " + integerValue);

// should output '[value1, value2, value3]':
System.out.println(stringList);

// should output '[10, 11, 12]':
System.out.println(integerList);

// should output '{key1=value1, key2=value2}':
System.out.println(stringMap);

// should output '{key1=100, key2=200}':
System.out.println(integerMap);
```
##### Simple marshalling-example
```java
class AnyObject
{
    @XPathFirst("anyStringValue")
    String stringValue;

    // All known default-converters are already registered for conversion 
    // to default-field-types, so the converterClass could be left here, 
    // but is set for illustration:    
    @XPathFirst(value = "anyIntegerValue", converterClass = ToIntegerConverter.class)
    Integer integerValue;
    
    @XPathList("stringList/value")
    List<String> stringList;
    
    @XPathList(value = "integerList/value", converterClass = ToIntegerConverter.class)
    List<Integer> integerList;
    
    @XPathMap(entryXPath = "stringMap/entry", keySubXPath = "string[1]", valueSubXPath = "string[2]")
    Map<String, String> stringMap;
    
    @XPathMap(entryXPath = "integerMap/entry", keySubXPath = "string[1]", valueSubXPath = "string[2]", valueConverterClass = ToIntegerConverter.class)
    Map<String, Integer> integerMap;
}

AnyObject anyObject = XPathUtils.fromXml(xml, AnyObject.class);

// should output 'anyValue - 123':
System.out.println(anyObject.stringValue + " - " + anyObject.integerValue);

// should output '[value1, value2, value3]':
System.out.println(anyObject.stringList);

// should output '[10, 11, 12]':
System.out.println(anyObject.integerList);

// should output '{key1=value1, key2=value2}':
System.out.println(anyObject.stringMap);

// should output '{key1=100, key2=200}':
System.out.println(anyObject.integerMap);
```
#### Using Converter with arguments
```java
ToLocalDateConverter converter = new ToLocalDateConverter(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
XPathUtils.registerConverterInstance(converter);
```
#### Register a default-converter for a special field-type
```java
ToLocalDateConverter converter = new ToLocalDateConverter(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
XPathUrils.registerDefaultConverterInstanceToType(LocalDate.class, converter);
```