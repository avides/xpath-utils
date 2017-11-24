package com.avides.xpath.utils;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avides.xpath.utils.converters.NoneConverter;
import com.avides.xpath.utils.converters.ToBooleanConverter;
import com.avides.xpath.utils.converters.ToCharacterConverter;
import com.avides.xpath.utils.converters.ToDoubleConverter;
import com.avides.xpath.utils.converters.ToFloatConverter;
import com.avides.xpath.utils.converters.ToIntegerConverter;
import com.avides.xpath.utils.converters.ToLocalDateConverter;
import com.avides.xpath.utils.converters.ToLocalDateTimeConverter;
import com.avides.xpath.utils.converters.ToLocalTimeConverter;
import com.avides.xpath.utils.converters.ToLongConverter;
import com.avides.xpath.utils.converters.ToShortConverter;
import com.avides.xpath.utils.converters.ToZonedDateTimeConverter;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * Utility-class for simple access to xml-nodes via xPath-queries containing
 * simple converting to java-types and others, including
 * {@link XPathUnmarshaller unmarshalling} to complete objects via annotations
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 *
 * @see XPathUnmarshaller
 */
public abstract class XPathUtils
{
    private static final Logger log = LoggerFactory.getLogger(XPathUtils.class);

    private static final Map<Class<? extends Function<String, ?>>, Function<String, ?>> converterCache = new HashMap<>(256);

    private XPathUtils()
    {
        // private constructor to hide the public one
    }

    /**
     * Builds an {@link Element} of the given {@link InputStream} on which
     * further xPath-operations can be executed
     *
     * @param inputStream
     *            the {@link InputStream} to build the {@link Element} from
     * @return the resulting {@link Element}
     * @throws ParsingException
     *             if the {@link Element} can not be build
     *
     * @since 1.0.2.RELEASE
     */
    public static Element getRootElement(InputStream inputStream) throws ParsingException
    {
        return buildElement(inputStream, input -> new Builder().build(input)).getRootElement();
    }

    /**
     * Builds an {@link Element} of the given {@link Reader} on which further
     * xPath-operations can be executed
     *
     * @param reader
     *            the {@link Reader} to build the {@link Element} from
     * @return the resulting {@link Element}
     * @throws ParsingException
     *             if the {@link Element} can not be build
     *
     * @since 1.0.2.RELEASE
     */
    public static Element getRootElement(Reader reader) throws ParsingException
    {
        return buildElement(reader, input -> new Builder().build(input)).getRootElement();
    }

    /**
     * Builds an {@link Element} of the given {@link File} on which further
     * xPath-operations can be executed
     *
     * @param file
     *            the {@link File} to build the {@link Element} from
     * @return the resulting {@link Element}
     * @throws ParsingException
     *             if the {@link Element} can not be build
     *
     * @since 1.0.2.RELEASE
     */
    public static Element getRootElement(File file) throws ParsingException
    {
        return buildElement(file, input -> new Builder().build(input)).getRootElement();
    }

    /**
     * Parses the given xml to an {@link Element} on which further
     * xPath-operations can be executed
     *
     * @param xml
     *            the xml to parse to an {@link Element}
     * @return the resulting {@link Element} (root-element of the given xml)
     * @throws ParsingException
     *             if the xml can not be parsed (invalid xml)
     *
     * @since 1.0.0.RELEASE
     */
    public static Element getRootElement(String xml) throws ParsingException
    {
        return getRootElement(new ByteArrayInputStream(xml.getBytes()));
    }

    /**
     * Unmarshals the given {@link Element} to a new instance of the given
     * {@link Class}, using annotations
     * {@link com.avides.xpath.utils.annotations.XPathFirst XPathFirst},
     * {@link com.avides.xpath.utils.annotations.XPathList XPathList} and
     * {@link com.avides.xpath.utils.annotations.XPathMap XPathMap}
     *
     * @param <T>
     *            the type of the resulting new instance, determined by the
     *            given {@link Class}
     * @param root
     *            the {@link Element} unmarshal from
     * @param type
     *            the {@link Class} of the wanted new instance
     * @return a new instance of the given {@link Class}, unmarshalled from the
     *         given xml
     *
     * @since 1.0.0.RELEASE
     *
     * @see XPathUnmarshaller#unmarshal(Element, Class)
     * @see #fromXml(String, Class)
     */
    public static <T> T fromRoot(Element root, Class<T> type)
    {
        return XPathUnmarshaller.getInstance().unmarshal(root, type);
    }

    /**
     * Unmarshals the given {@link InputStream} to a new instance of the given
     * {@link Class}, using annotations
     * {@link com.avides.xpath.utils.annotations.XPathFirst XPathFirst},
     * {@link com.avides.xpath.utils.annotations.XPathList XPathList} and
     * {@link com.avides.xpath.utils.annotations.XPathMap XPathMap}
     *
     * @param inputStream
     *            the {@link InputStream} to be unmarshalled
     * @param type
     *            the {@link Class} of the wanted new instance
     * @return a new instance of the given {@link Class}, unmarshalled from the
     *         given {@link InputStream}
     * @throws ParsingException
     *             if the {@link InputStream} can not be parsed (invalid xml)
     *
     * @since 1.0.2.RELEASE
     *
     * @see XPathUnmarshaller#unmarshal(String, Class)
     * @see #fromRoot(Element, Class)
     */
    public static <T> T fromInputStream(InputStream inputStream, Class<T> type) throws ParsingException
    {
        return XPathUnmarshaller.getInstance().unmarshal(inputStream, type);
    }

    /**
     * Unmarshals the given {@link Reader} to a new instance of the given
     * {@link Class}, using annotations
     * {@link com.avides.xpath.utils.annotations.XPathFirst XPathFirst},
     * {@link com.avides.xpath.utils.annotations.XPathList XPathList} and
     * {@link com.avides.xpath.utils.annotations.XPathMap XPathMap}
     *
     * @param reader
     *            the {@link Reader} to be unmarshalled
     * @param type
     *            the {@link Class} of the wanted new instance
     * @return a new instance of the given {@link Class}, unmarshalled from the
     *         given {@link Reader}
     * @throws ParsingException
     *             if the {@link Reader} can not be parsed (invalid xml)
     *
     * @since 1.0.2.RELEASE
     *
     * @see XPathUnmarshaller#unmarshal(String, Class)
     * @see #fromRoot(Element, Class)
     */
    public static <T> T fromReader(Reader reader, Class<T> type) throws ParsingException
    {
        return XPathUnmarshaller.getInstance().unmarshal(reader, type);
    }

    /**
     * Unmarshals the given {@link File} to a new instance of the given
     * {@link Class}, using annotations
     * {@link com.avides.xpath.utils.annotations.XPathFirst XPathFirst},
     * {@link com.avides.xpath.utils.annotations.XPathList XPathList} and
     * {@link com.avides.xpath.utils.annotations.XPathMap XPathMap}
     *
     * @param file
     *            the {@link File} to be unmarshalled
     * @param type
     *            the {@link Class} of the wanted new instance
     * @return a new instance of the given {@link Class}, unmarshalled from the
     *         given {@link Reader}
     * @throws ParsingException
     *             if the {@link File} can not be parsed (invalid xml)
     *
     * @since 1.0.2.RELEASE
     *
     * @see XPathUnmarshaller#unmarshal(String, Class)
     * @see #fromRoot(Element, Class)
     */
    public static <T> T fromFile(File file, Class<T> type) throws ParsingException
    {
        return XPathUnmarshaller.getInstance().unmarshal(file, type);
    }

    /**
     * Unmarshals the given xml to a new instance of the given {@link Class},
     * using annotations {@link com.avides.xpath.utils.annotations.XPathFirst
     * XPathFirst}, {@link com.avides.xpath.utils.annotations.XPathList
     * XPathList} and {@link com.avides.xpath.utils.annotations.XPathMap
     * XPathMap}
     *
     * @param xml
     *            the xml to be unmarshalled
     * @param type
     *            the {@link Class} of the wanted new instance
     * @return a new instance of the given {@link Class}, unmarshalled from the
     *         given xml
     * @throws ParsingException
     *             if the xml can not be parsed (invalid xml)
     *
     * @since 1.0.0.RELEASE
     *
     * @see XPathUnmarshaller#unmarshal(String, Class)
     * @see #fromRoot(Element, Class)
     */
    public static <T> T fromXml(String xml, Class<T> type) throws ParsingException
    {
        return XPathUnmarshaller.getInstance().unmarshal(xml, type);
    }

    /**
     * Alias for {@link Node#query(String)}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query on
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found {@link Nodes}
     *
     * @since 1.0.0.RELEASE
     *
     * @see Node#query(String)
     */
    public static Nodes queryNodes(Node root, String xPath)
    {
        return root.query(xPath);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and returns the
     * found {@link Nodes} converted to an {@link Iterable} of {@link Node}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found {@link Nodes} converted to a {@link Iterable} of
     *         {@link Node}, will never be <code>null</code>
     *
     * @since 1.0.0.RELEASE
     *
     * @see NodeIterator
     */
    public static Iterable<Node> each(Node root, String xPath)
    {
        return new NodeIterator(queryNodes(root, xPath));
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and returns the
     * found {@link Nodes} converted to a {@link List} of {@link Node}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found {@link Nodes} converted to a {@link List} of
     *         {@link Node}, will never be <code>null</code>
     *
     * @since 1.0.0.RELEASE
     */
    public static List<Node> queryNodeList(Node root, String xPath)
    {
        List<Node> nodeList = new ArrayList<>();
        for (Node node : each(root, xPath))
        {
            nodeList.add(node);
        }
        return nodeList;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and returns the
     * found {@link Nodes} converted to a {@link List} of {@link Element}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found {@link Nodes} converted to a {@link List} of
     *         {@link Element}, will never be <code>null</code>
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryNodeList(Node, String)
     */
    public static List<Element> queryElementList(Node root, String xPath)
    {
        List<Element> elementList = new ArrayList<>();
        for (Node node : each(root, xPath))
        {
            elementList.add((Element) node);
        }
        return elementList;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and returns the
     * first found {@link Node} or <code>null</code> if no {@link Nodes} were
     * found
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the first found {@link Node}, <code>null</code> if no
     *         {@link Nodes} were found
     *
     * @since 1.0.0.RELEASE
     */
    public static Node queryFirstNode(Node root, String xPath)
    {
        Nodes nodes = queryNodes(root, xPath);
        return (hasNodes(nodes)) ? nodes.get(0) : null;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and returns the
     * found {@link Node} casted to an {@link Element}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found {@link Node} casted to an {@link Element},
     *         <code>null</code> if no {@link Node} was found
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirstNode(Node, String)
     */
    public static Element queryFirstElement(Node root, String xPath)
    {
        return (Element) queryFirstNode(root, xPath);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and returns the
     * first found value or <code>null</code> if no value was found
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the first found value, <code>null</code> if no value was found
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirstNode(Node, String)
     */
    public static String queryFirst(Node root, String xPath)
    {
        Node node = queryFirstNode(root, xPath);
        return node != null ? node.getValue() : null;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and returns the
     * first found value converted by the given {@link Function Converter} or
     * <code>null</code> if no value was found
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @param converterClass
     *            the {@link Function Converter} (given by the {@link Class}) to
     *            convert the value with
     * @return the first found value converted by the given {@link Function
     *         Converter}, <code>null</code> if no value was found
     *
     * @since 1.0.0.RELEASE
     */
    public static <T> T queryFirst(Node root, String xPath, Class<? extends Function<String, T>> converterClass)
    {
        return getConverter(converterClass).apply(queryFirst(root, xPath));
    }

    /**
     * Tests, if the given xPath-query on the given {@link Node} results to an
     * existing {@link Node}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return true if a {@link Node} exists, false if not
     *
     * @since 1.0.0.RELEASE
     */
    public static boolean hasNode(Node root, String xPath)
    {
        return queryFirst(root, xPath) != null;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to an {@link Integer}. Values are trimmed before conversion
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to an {@link Integer},
     *         <code>null</code> if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a {@link Integer}
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToIntegerConverter
     */
    public static Integer queryInteger(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToIntegerConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a primitive integer (<code>int</code>). Values are trimmed
     * before conversion
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a primitive integer
     *         (<code>int</code>), zero if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>int</code>
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToIntegerConverter
     */
    public static int queryPrimitiveInteger(Node root, String xPath)
    {
        Integer value = queryFirst(root, xPath, ToIntegerConverter.class);
        return value != null ? value.intValue() : 0;
    }

    /**
     * Alias for {@link #queryPrimitiveInteger(Node, String)}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a primitive integer
     *         (<code>int</code>), zero if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>int</code>
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryPrimitiveInteger(Node, String)
     */
    public static int queryInt(Node root, String xPath)
    {
        return queryPrimitiveInteger(root, xPath);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a {@link Long}. Values are trimmed before conversion
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link Long}, <code>null</code> if
     *         no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a {@link Long}
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToLongConverter
     */
    public static Long queryLong(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToLongConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a primitive <code>long</code>. Values are trimmed before
     * conversion
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a primitive <code>long</code>, zero
     *         if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>long</code>
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToLongConverter
     */
    public static long queryPrimitiveLong(Node root, String xPath)
    {
        Long value = queryFirst(root, xPath, ToLongConverter.class);
        return value != null ? value.longValue() : 0;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a {@link Short}. Values are trimmed before conversion
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link Short}, <code>null</code>
     *         if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a {@link Short}
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToShortConverter
     */
    public static Short queryShort(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToShortConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a primitive <code>short</code>. Values are trimmed before
     * conversion
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a primitive <code>short</code>, zero
     *         if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>short</code>
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToShortConverter
     */
    public static short queryPrimitiveShort(Node root, String xPath)
    {
        Short value = queryFirst(root, xPath, ToShortConverter.class);
        return value != null ? value.shortValue() : 0;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a {@link Double}. Values are trimmed before conversion,
     * commas are replaced by dots
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link Double}, <code>null</code>
     *         if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a {@link Double}
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToDoubleConverter
     */
    public static Double queryDouble(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToDoubleConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a primitive <code>double</code>. Values are trimmed before
     * conversion, commas are replaced by dots
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a primitive <code>double</code>,
     *         zero if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>double</code>
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToDoubleConverter
     */
    public static double queryPrimitiveDouble(Node root, String xPath)
    {
        Double value = queryFirst(root, xPath, ToDoubleConverter.class);
        return value != null ? value.doubleValue() : 0;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a {@link Float}. Values are trimmed before conversion,
     * commas are replaced by dots
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link Float}, <code>null</code>
     *         if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a {@link Float}
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToFloatConverter
     */
    public static Float queryFloat(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToFloatConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a primitive <code>float</code>. Values are trimmed before
     * conversion, commas are replaced by dots
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a primitive <code>float</code>, zero
     *         if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>float</code>
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToFloatConverter
     */
    public static float queryPrimitiveFloat(Node root, String xPath)
    {
        Float value = queryFirst(root, xPath, ToFloatConverter.class);
        return value != null ? value.floatValue() : 0;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a {@link Boolean}. Allowed values for {@link Boolean#TRUE
     * TRUE} are (not case-sensitive): 'true', 'yes', 'on', '1', 'positive',
     * 'correct', 'ja', 'oui', 'si', 'sì'. All other values will result to
     * {@link Boolean#FALSE FALSE}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link Boolean}, <code>null</code>
     *         if no value was found
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToBooleanConverter
     */
    public static Boolean queryBoolean(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToBooleanConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a primitive <code>boolean</code>. Allowed values for
     * <code>boolean true</code> are (not case-sensitive): 'true', 'yes', 'on',
     * '1', 'positive', 'correct', 'ja', 'oui', 'si', 'sì'. All other values
     * will result to <code>boolean false</code>
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a primitive <code>boolean</code>,
     *         <code>boolean false</code> if no value was found
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToBooleanConverter
     */
    public static boolean queryPrimitiveBoolean(Node root, String xPath)
    {
        Boolean value = queryFirst(root, xPath, ToBooleanConverter.class);
        return value != null ? value.booleanValue() : false;
    }

    /**
     * Alias for {@link #queryPrimitiveBoolean(Node, String)}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a primitive <code>boolean</code>,
     *         <code>boolean false</codes> if no value was found
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryPrimitiveBoolean(Node, String)
     */
    public static boolean queryBool(Node root, String xPath)
    {
        return queryPrimitiveBoolean(root, xPath);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a {@link Character}. If the found value is longer than 1
     * character, the first character will be returned. If no value was found or
     * the found value is empty, <code>null</code> will be returned.
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link Character},
     *         <code>null</code> if no value was found or the found value is
     *         empty
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToCharacterConverter
     */
    public static Character queryCharacter(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToCharacterConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a primitive character (<code>char</code>). If the found
     * value is longer than 1 character, the first character will be returned.
     * If no value was found or the found value is empty, a zero-valued
     * characater will be returned.
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a primitive character
     *         (<code>char</code>), zero-valued character if no value was found
     *         or the found value is empty
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToCharacterConverter
     */
    public static char queryPrimitiveCharacter(Node root, String xPath)
    {
        Character value = queryFirst(root, xPath, ToCharacterConverter.class);
        return value != null ? value.charValue() : 0;
    }

    /**
     * Alias for {@link #queryPrimitiveCharacter(Node, String)}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a primitive character
     *         (<code>char</code>), zero-valued character if no value was found
     *         or the found value is empty
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryPrimitiveCharacter(Node, String)
     */
    public static char queryChar(Node root, String xPath)
    {
        return queryPrimitiveCharacter(root, xPath);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to the wanted {@link Enum}. If the value is empty,
     * <code>null</code> will be returned
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link Enum}, <code>null</code> if
     *         no value was found, is empty or could not be converted to a
     *         {@link Enum}
     *
     * @throws IllegalArgumentException
     *             if the enum-value can not be parsed
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     */
    public static <E extends Enum<E>> E queryEnum(Node root, String xPath, Class<E> enumType)
    {
        String value = queryFirst(root, xPath);
        return (value != null) && !value.isEmpty() ? Enum.valueOf(enumType, value) : null;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a {@link ZonedDateTime}. The value has to match the format
     * {@link java.time.format.DateTimeFormatter#ISO_ZONED_DATE_TIME}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link ZonedDateTime},
     *         <code>null</code> if no value was found
     * @throws java.time.format.DateTimeParseException
     *             if value can not be converted to {@link ZonedDateTime}
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToZonedDateTimeConverter
     */
    public static ZonedDateTime queryZonedDateTime(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToZonedDateTimeConverter.class);
    }

    /**
     * Executes the given xPath-query on the given node and converts the found
     * value to a {@link LocalDateTime}. The value has to match the format
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link LocalDateTime},
     *         <code>null</code> if no value was found
     * @throws java.time.format.DateTimeParseException
     *             if value can not be converted to {@link LocalDateTime}
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToLocalDateTimeConverter
     */
    public static LocalDateTime queryLocalDateTime(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToLocalDateTimeConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a {@link LocalDate}. The value has to match the format
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link LocalDate},
     *         <code>null</code> if no value was found
     * @throws java.time.format.DateTimeParseException
     *             if value can not be converted to {@link LocalDate}
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToLocalDateConverter
     */
    public static LocalDate queryLocalDate(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToLocalDateConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and converts the
     * found value to a {@link LocalTime}. The value has to match the format
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_TIME}
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return the found value converted to a {@link LocalTime},
     *         <code>null</code> if no value was found or could not be converted
     *         to a {@link LocalTime}
     * @throws java.time.format.DateTimeParseException
     *             if value can not be converted to {@link LocalTime}
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryFirst(Node, String, Class)
     * @see ToLocalTimeConverter
     */
    public static LocalTime queryLocalTime(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToLocalTimeConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and returns a
     * {@link List} of the found values
     *
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @return a {@link List} of the found values, will never be
     *         <code>null</code>
     *
     * @since 1.0.0.RELEASE
     */
    public static List<String> queryList(Node root, String xPath)
    {
        List<String> list = new ArrayList<>();
        each(root, xPath).forEach(node -> list.add(node.getValue()));
        return list;
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and returns a
     * {@link List} of the found values converted by the given {@link Function
     * Converter}
     *
     * @param <T>
     *            the type of the resulting list-items, determined by the given
     *            {@link Function Converter}
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @param converterClass
     *            the {@link Function Converter} (given by the {@link Class}) to
     *            convert the values with
     * @return a {@link List} of the found values converted by the given
     *         {@link Function Converter}, will never be <code>null</code>
     *
     * @since 1.0.0.RELEASE
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> queryList(Node root, String xPath, Class<? extends Function<String, T>> converterClass)
    {
        Stream<String> stringList = queryList(root, xPath).stream();
        if (converterClass != null)
        {
            Function<String, T> converter = getConverter(converterClass);
            return stringList.map(item -> converter.apply(item)).collect(toList());
        }
        return (List<T>) stringList.map(item -> null).collect(toList());
    }

    /**
     * Executes the given xPath-query on the given {@link Node} and returns a
     * {@link List} of the found values converted by the given {@link Function
     * Converter}. If the {@link Class subType} is given, the {@link Function
     * Converter} is ignored and the found {@link Node}s are unmarshalled to the
     * given {@link Class subType}. The given {@link Class subType} may also be
     * an <code>enum</code>-{@link Class} to convert the values to
     *
     * @param <T>
     *            the type of the resulting list-items, determined by the given
     *            {@link Function Converter}
     * @param root
     *            the {@link Node} to execute the given xPath-query from
     * @param xPath
     *            the xPath-query to execute on the given {@link Node}
     * @param converterClass
     *            the {@link Function Converter} (given by the {@link Class}) to
     *            convert the values with
     * @return a {@link List} of the found values converted by the given
     *         {@link Function Converter}, or a {@link List} of the given
     *         {@link Class subType}, if a {@link Class subType is given}. Will
     *         never be <code>null</code>
     *
     * @since 1.0.3.RELEASE
     */
    public static <T> List<T> queryList(Node root, String xPath, Class<? extends Function<String, T>> converterClass, Class<T> subType)
    {
        if ((subType != null) && (subType != String.class))
        {
            List<Node> nodeList = XPathUtils.queryNodeList(root, xPath);
            List<T> values = new ArrayList<>(nodeList.size());
            for (Node node : nodeList)
            {
                values.add(unmarshallSubType(node, subType));
            }
            return values;
        }
        return queryList(root, xPath, converterClass);
    }

    /**
     * Alias / shortcut for {@link #queryList(Node, String, Class, Class)
     *
     * @since 1.0.3.RELEASE
     */
    public static <T> List<T> queryTypedList(Node root, String xPath, Class<T> subType)
    {
        return queryList(root, xPath, null, subType);
    }

    /**
     * Executes the give entryXPath on the given {@link Node} to create an
     * {@link Entry} for each found {@link Node}. From that found {@link Node}
     * each entry-key is queried by the given keySubXPath and each entry-value
     * is queried by the given valueSubXPath
     *
     * @param root
     *            the {@link Node} to execute the given entryXPath-query from
     * @param entryXPath
     *            the xPath-query to execute on the given {@link Node} for all
     *            entries
     * @param keySubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the key of that {@link Entry}
     * @param valueSubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the value of that {@link Entry}
     * @return a {@link Map} of {@link Node keyNode} and {@link Node valueNode}
     *
     * @since 1.0.3.RELEASE
     */
    public static Map<Node, Node> queryNodeMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath)
    {
        Map<Node, Node> map = new HashMap<>();
        for (Node node : each(root, entryXPath))
        {
            Node key = queryFirstNode(node, keySubXPath);
            Node value = queryFirstNode(node, valueSubXPath);
            map.put(key, value);
        }
        return map;
    }

    /**
     * Executes the give entryXPath on the given {@link Node} to create an
     * {@link Entry} for each found {@link Node}. From that found {@link Node}
     * each entry-key is queried by the given keySubXPath and each entry-value
     * is queried by the given valueSubXPath
     *
     * @param root
     *            the {@link Node} to execute the given entryXPath-query from
     * @param entryXPath
     *            the xPath-query to execute on the given {@link Node} for all
     *            entries
     * @param keySubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the key of that {@link Entry}
     * @param valueSubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the value of that {@link Entry}
     * @return a {@link Map} of {@link String keyString} and {@link String
     *         valueString}
     *
     * @since 1.0.0.RELEASE
     */
    public static Map<String, String> queryMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath)
    {
        Map<String, String> map = new HashMap<>();
        for (Node node : each(root, entryXPath))
        {
            String key = queryFirst(node, keySubXPath);
            String value = queryFirst(node, valueSubXPath);
            map.put(key, value);
        }
        return map;
    }

    /**
     * Executes the give entryXPath on the given {@link Node} to create an
     * {@link Entry} for each found {@link Node}. From that found {@link Node}
     * each entry-key is queried by the given keySubXPath and each entry-value
     * is queried by the given valueSubXPath. Each entry-key is converted by the
     * given {@link Function keyConverter} and each entry-value is converted by
     * the given {@link Function valueConverter}
     *
     * @param root
     *            the {@link Node} to execute the given entryXPath-query from
     * @param entryXPath
     *            the xPath-query to execute on the given {@link Node} for all
     *            entries
     * @param keySubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the key of that {@link Entry}
     * @param valueSubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the value of that {@link Entry}
     * @return a {@link Map} of key and value, each converted by the given
     *         {@link Function keyConverter} and {@link Function valueConverter}
     *
     * @since 1.0.0.RELEASE
     *
     * @see #queryMap(Node, String, String, String, Class, Class, Class, Class)
     */
    public static <K, V> Map<K, V> queryMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath,
        Class<? extends Function<String, K>> keyConverterClass, Class<? extends Function<String, V>> valueConverterClass)
    {
        return queryMap(root, entryXPath, keySubXPath, valueSubXPath, keyConverterClass, valueConverterClass, null, null);
    }

    /**
     * Executes the give entryXPath on the given {@link Node} to create an
     * {@link Entry} for each found {@link Node}. From that found {@link Node}
     * each entry-key is queried by the given keySubXPath and each entry-value
     * is queried by the given valueSubXPath. Each entry-key is converted by the
     * given {@link Function keyConverter} and each entry-value is converted by
     * the given {@link Function valueConverter}. If a {@link Class keySubType}
     * is given, the {@link Function keyConverter} is ignored and the found
     * entry-key-{@link Node} will be unmarshalled to the given {@link Class
     * keySubType}. If a {@link Class valueSubType} is given, the
     * {@link Function valueConverter} is ignored and the found
     * entry-value-{@link Node} will be unmarshalled to the given {@link Class
     * valueSubType}. The given {@link Class keySubType} or {@link Class
     * valueSubType} may also be an <code>enum</code>-{@link Class} to convert
     * the keys and values to
     *
     * @param root
     *            the {@link Node} to execute the given entryXPath-query from
     * @param entryXPath
     *            the xPath-query to execute on the given {@link Node} for all
     *            entries
     * @param keySubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the key of that {@link Entry}
     * @param valueSubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the value of that {@link Entry}
     * @param keySubType
     *            the {@link Class} to unmarshall the found
     *            entry-key-{@link Node}
     * @param valueSubType
     *            the {@link Class} to unmarshall the found
     *            entry-value-{@link Node}
     * @return a {@link Map} of key and value, each converted by the given
     *         {@link Function keyConverter} and {@link Function valueConverter}
     *         or unmarshalled to the given {@link Class keySubType} and
     *         {@link Class valueSubType}
     *
     * @since 1.0.3.RELEASE
     *
     * @see #queryMap(Node, String, String, String)
     */
    public static <K, V> Map<K, V> queryMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath,
        Class<? extends Function<String, K>> keyConverterClass, Class<? extends Function<String, V>> valueConverterClass, Class<K> keySubType,
        Class<V> valueSubType)
    {
        Map<Node, Node> nodeMap = queryNodeMap(root, entryXPath, keySubXPath, valueSubXPath);
        Map<K, V> map = new HashMap<>(nodeMap.size());
        Function<String, K> keyConverter = null;
        Function<String, V> valueConverter = null;
        if (((keySubType == null) || (keySubType == String.class)) && (keyConverterClass != null))
        {
            keyConverter = getConverter(keyConverterClass);
        }
        if (((valueSubType == null) || (keySubType == String.class)) && (valueConverterClass != null))
        {
            valueConverter = getConverter(valueConverterClass);
        }
        for (Entry<Node, Node> entry : nodeMap.entrySet())
        {
            K key = unmarshallOrConvert(entry.getKey(), keySubType, keyConverter);
            V value = unmarshallOrConvert(entry.getValue(), valueSubType, valueConverter);
            map.put(key, value);
        }
        return map;
    }

    /**
     * Alias / shortcut for
     * {@link #queryMap(Node, String, String, String, Class, Class, Class, Class)}
     *
     * @since 1.0.3.RELEASE
     */
    public static <K, V> Map<K, V> queryTypedMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<K> keySubType,
        Class<V> valueSubType)
    {
        return queryMap(root, entryXPath, keySubXPath, valueSubXPath, null, null, keySubType, valueSubType);
    }

    /**
     * Alias / shortcut for
     * {@link #queryMap(Node, String, String, String, Class, Class, Class, Class)}
     *
     * @since 1.0.3.RELEASE
     */
    public static <K, V> Map<K, V> queryKeyTypedMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<K> keySubType,
        Class<? extends Function<String, V>> valueConverterClass)
    {
        return queryMap(root, entryXPath, keySubXPath, valueSubXPath, null, valueConverterClass, keySubType, null);
    }

    /**
     * Alias / shortcut for
     * {@link #queryMap(Node, String, String, String, Class, Class, Class, Class)}
     *
     * @since 1.0.3.RELEASE
     */
    public static <K> Map<K, String> queryKeyTypedMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<K> keySubType)
    {
        return queryMap(root, entryXPath, keySubXPath, valueSubXPath, null, NoneConverter.class, keySubType, null);
    }

    /**
     * Alias / shortcut for
     * {@link #queryMap(Node, String, String, String, Class, Class, Class, Class)}
     *
     * @since 1.0.3.RELEASE
     */
    public static <K, V> Map<K, V> queryValueTypedMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath,
        Class<? extends Function<String, K>> keyConverterClass, Class<V> valueSubType)
    {
        return queryMap(root, entryXPath, keySubXPath, valueSubXPath, keyConverterClass, null, null, valueSubType);
    }

    /**
     * Alias / shortcut for
     * {@link #queryMap(Node, String, String, String, Class, Class, Class, Class)}
     *
     * @since 1.0.3.RELEASE
     */
    public static <V> Map<String, V> queryValueTypedMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath, Class<V> valueSubType)
    {
        return queryMap(root, entryXPath, keySubXPath, valueSubXPath, NoneConverter.class, null, null, valueSubType);
    }

    /**
     * Simply tests, if the given {@link Nodes} are not <code>null</code> and
     * not empty
     *
     * @param nodes
     *            the {@link Nodes} to test on
     * @return true if the {@link Nodes} are not <code>null</code> and not
     *         empty, false if the {@link Nodes} are <code>null</code> or empty
     *
     * @since 1.0.0.RELEASE
     */
    public static boolean hasNodes(Nodes nodes)
    {
        return ((nodes != null) && (nodes.size() > 0));
    }

    /**
     * Registeres the given explicit instance of a {@link Function Converter} to
     * use, if a {@link Function Converter} of that class is used
     *
     * @param converter
     *            The instance of the {@link Function Converter} to use if a
     *            {@link Function Converter} of that class is used
     *
     * @since 1.0.2.RELEASE
     */
    @SuppressWarnings("unchecked")
    public static void registerConverterInstance(Function<String, ?> converter)
    {
        converterCache.put((Class<? extends Function<String, ?>>) converter.getClass(), converter);
    }

    /**
     * Unregisters an instance of {@link Function Converter} by its
     * {@link Class}
     *
     * @param converterClass
     *            the {@link Class} of the {@link Function Converter}-instance
     *            to be unregistered
     *
     * @since 1.0.3.RELEASE
     */
    public static void unregisterConverterInstance(Class<? extends Function<String, ?>> converterClass)
    {
        converterCache.remove(converterClass);
    }

    /**
     * Clears all cached instances of {@link Function Converter}s
     *
     * @since 1.0.3.RELEASE
     */
    public static void clearConverterInstances()
    {
        converterCache.clear();
    }

    /**
     * Registers a default {@link Function Converter} to use for unmarshalling
     * when the value (after an annotated {@link Function Converter}-conversion)
     * is a {@link String} but the field-type is not. So when the registered
     * default-{@link Function Converter}s contain a matching one for the wanted
     * field-type, it is used to convert the value to the wanted {@link Class}
     *
     * @param type
     *            the {@link Class} that matches the field-type and the given
     *            {@link Function Converter}-result-type
     * @param converter
     *            the {@link Function Converter} to use for converting to the
     *            given {@link Class}
     *
     * @since 1.0.2.RELEASE
     *
     * @see XPathUnmarshaller#registerDefaultConverterInstanceToType(Class,
     *      Function)
     */
    public static void registerDefaultConverterInstanceToType(Class<?> type, Function<String, ?> converter)
    {
        XPathUnmarshaller.registerDefaultConverterInstanceToType(type, converter);
    }

    /**
     * Unregisters a {@link Function Converter} for default-conversion for the
     * given {@link Class}
     *
     * @param type
     *            the {@link Class} the {@link Function Converter} is registered
     *            to convert to
     *
     * @since 1.0.3.RELEASE
     *
     * @see XPathUnmarshaller#unregisterDefaultConverterInstanceToType(Class)
     */
    public static void unregisterDefaultConverterInstanceToType(Class<?> type)
    {
        XPathUnmarshaller.unregisterDefaultConverterInstanceToType(type);
    }

    /**
     * Resets all {@link Function Converters} for default-conversion to the
     * default (clears all and puts the drefault ones)
     *
     * @since 1.0.3.RELEASE
     *
     * @see XPathUnmarshaller#resetDefaultConverterInstancesToType()
     */
    public static void resetDefaultConverterInstancesToType()
    {
        XPathUnmarshaller.resetDefaultConverterInstancesToType();
    }

    @SuppressWarnings("unchecked")
    static <T> Function<String, T> getConverter(Class<? extends Function<String, T>> converterClass)
    {
        Function<String, T> converter = (Function<String, T>) converterCache.get(converterClass);
        if (converter == null)
        {
            try
            {
                converter = converterClass.newInstance();
                converterCache.put(converterClass, converter);
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                log.error("could not create new instance for converter-class " + converterClass + ": " + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return converter;
    }

    private static <T> T unmarshallOrConvert(Node node, Class<T> subType, Function<String, T> converter)
    {
        if (node != null)
        {
            if ((subType != null) && (subType != String.class))
            {
                return unmarshallSubType(node, subType);
            }
            else if (converter != null)
            {
                return converter.apply(node.getValue());
            }
        }
        return null;
    }

    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private static <T> T unmarshallSubType(Node node, Class<T> subType)
    {
        if (subType.isEnum())
        {
            return (T) Enum.valueOf((Class<Enum>) subType, node.getValue());
        }
        return XPathUtils.fromRoot((Element) node, subType);
    }

    private static <T> Document buildElement(T input, ToDocumentConverter<T> converter) throws ParsingException
    {
        try
        {
            return converter.convert(input);
        }
        catch (IOException e)
        {
            throw new ParsingException("the xml can not be parsed", e);
        }
    }

    private interface ToDocumentConverter<T>
    {
        Document convert(T input) throws IOException, ValidityException, ParsingException;
    }
}