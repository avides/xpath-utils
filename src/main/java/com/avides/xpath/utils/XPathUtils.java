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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avides.xpath.utils.converter.ToBooleanConverter;
import com.avides.xpath.utils.converter.ToCharacterConverter;
import com.avides.xpath.utils.converter.ToDoubleConverter;
import com.avides.xpath.utils.converter.ToFloatConverter;
import com.avides.xpath.utils.converter.ToIntegerConverter;
import com.avides.xpath.utils.converter.ToLocalDateConverter;
import com.avides.xpath.utils.converter.ToLocalDateTimeConverter;
import com.avides.xpath.utils.converter.ToLocalTimeConverter;
import com.avides.xpath.utils.converter.ToLongConverter;
import com.avides.xpath.utils.converter.ToShortConverter;
import com.avides.xpath.utils.converter.ToZonedDateTimeConverter;

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
 * {@link com.avides.xpath.utils.XPathUnmarshaller unmarshalling} to complete
 * objects via annotations
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 *
 * @see {@link com.avides.xpath.utils.XPathUnmarshaller}
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
     * Builds an {nu.xom.Element Element} of the given
     * {@link java.io.InputStream InputStream} on which further xPath-operations
     * can be executed
     *
     * @param inputStream
     *            the {@link java.io.InputStream InputStream} to build the
     *            {@link nu.xom.Element Element} from
     * @return the resulting {@link nu.xom.Element Element}
     * @throws ParsingException
     *             if the {nu.xom.Element Element} can not be build
     */
    public static Element getRootElementFromInputStream(InputStream inputStream) throws ParsingException
    {
        return buildElement(inputStream, input -> new Builder().build(input)).getRootElement();
    }

    /**
     * Builds an {nu.xom.Element Element} of the given {@link java.io.Reader
     * Reader} on which further xPath-operations can be executed
     *
     * @param reader
     *            the {@link java.io.Reader Reader} to build the
     *            {@link nu.xom.Element Element} from
     * @return the resulting {@link nu.xom.Element Element}
     * @throws ParsingException
     *             if the {nu.xom.Element Element} can not be build
     */
    public static Element getRootElementFromReader(Reader reader) throws ParsingException
    {
        return buildElement(reader, input -> new Builder().build(input)).getRootElement();
    }

    /**
     * Builds an {nu.xom.Element Element} of the given {@link java.io.File File}
     * on which further xPath-operations can be executed
     *
     * @param file
     *            the {@link java.io.File File} to build the
     *            {@link nu.xom.Element Element} from
     * @return the resulting {@link nu.xom.Element Element}
     * @throws ParsingException
     *             if the {nu.xom.Element Element} can not be build
     */
    public static Element getRootElementFromFile(File file) throws ParsingException
    {
        return buildElement(file, input -> new Builder().build(input)).getRootElement();
    }

    /**
     * Parses the given xml to an {nu.xom.Element Element} on which further
     * xPath-operations can be executed
     *
     * @param xml
     *            the xml to parse to an {@link nu.xom.Element Element}
     * @return the resulting {@link nu.xom.Element Element} (root-element of the
     *         given xml)
     * @throws ParsingException
     *             if the xml can not be parsed (invalid xml)
     */
    public static Element getRootElementFromXml(String xml) throws ParsingException
    {
        return getRootElementFromInputStream(new ByteArrayInputStream(xml.getBytes()));
    }

    /**
     * Unmarshals the given {@link nu.xom.Element Element} to a new instance of
     * the given {@link java.lang.Class Class}, using annotations
     * {@link com.avides.xpath.utils.annotations.XPathFirst XPathFirst},
     * {@link com.avides.xpath.utils.annotations.XPathList XPathList} and
     * {@link com.avides.xpath.utils.annotations.XPathMap XPathMap}
     *
     * @param <T>
     *            the type of the resulting new instance, determined by the
     *            given {@link java.lang.Class Class}
     * @param root
     *            the {@link nu xom.Element Element} unmarshal from
     * @param type
     *            the {@link java.lang.Class Class} of the wanted new instance
     * @return a new instance of the given {@link java.lang.Class Class},
     *         unmarshalled from the given xml
     *
     * @see {@link com.avides.xpath.utils.XPathUnmarshaller#unmarshal(Element, Class)}
     * @see {@link com.avides.xpath.utils.XPathUtils#fromXml(String, Class)}
     */
    public static <T> T fromRoot(Element root, Class<T> type)
    {
        return XPathUnmarshaller.getInstance().unmarshal(root, type);
    }

    /**
     * Unmarshals the given xml to a new instance of the given
     * {@link java.lang.Class Class}, using annotations
     * {@link com.avides.xpath.utils.annotations.XPathFirst XPathFirst},
     * {@link com.avides.xpath.utils.annotations.XPathList XPathList} and
     * {@link com.avides.xpath.utils.annotations.XPathMap XPathMap}
     *
     * @param xml
     *            the xml to be unmarshalled
     * @param type
     *            the {@link java.lang.Class Class} of the wanted new instance
     * @return a new instance of the given {@link java.lang.Class Class},
     *         unmarshalled from the given xml
     * @throws ParsingException
     *             if the xml can not be parsed (invalid xml)
     *
     * @see {@link com.avides.xpath.utils.XPathUnmarshaller#unmarshal(String, Class)}
     * @see {@link com.avides.xpath.utils.XPathUtils#fromRoot(Element, Class)}
     */
    public static <T> T fromXml(String xml, Class<T> type) throws ParsingException
    {
        return XPathUnmarshaller.getInstance().unmarshal(xml, type);
    }

    /**
     * Alias for {@link nu.xom.Node#query(String)}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            on
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found {@link nu.xom.Nodes Nodes}
     *
     * @see {@link nu.xom.Node#query(String)}
     */
    public static Nodes queryNodes(Node root, String xPath)
    {
        return root.query(xPath);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * returns the found {@link nu.xom.Nodes Nodes} converted to an
     * {@link java.lang.Iterable Iterable} of {@link nu.xom.Node Node}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found {@link nu.xom.Nodes Nodes} converted to a
     *         {@link java.lang.Iterable Iterable} of {@link nu.xom.Node Node},
     *         will never be <code>null</code>
     *
     * @see {@link com.avides.xpath.utils.NodeIterator}
     */
    public static Iterable<Node> each(Node root, String xPath)
    {
        return new NodeIterator(queryNodes(root, xPath));
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * returns the found {@link nu.xom.Nodes Nodes} converted to a
     * {@link java.util.List List} of {@link nu.xom.Node Node}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found {@link nu.xom.Nodes Nodes} converted to a
     *         {@link java.util.List List} of {@link nu.xom.Node Node}, will
     *         never be <code>null</code>
     */
    public static List<Node> queryNodeList(Node root, String xPath)
    {
        List<Node> nodeList = new ArrayList<Node>();
        for (Node node : each(root, xPath))
        {
            nodeList.add(node);
        }
        return nodeList;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * returns the found {@link nu.xom.Nodes Nodes} converted to a
     * {@link java.util.List List} of {@link nu.xom.Element Element}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found {@link nu.xom.Nodes Nodes} converted to a
     *         {@link java.util.List List} of {@link nu.xom.Element Element},
     *         will never be <code>null</code>
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryNodeList(Node, String)}
     */
    public static List<Element> queryElementList(Node root, String xPath)
    {
        List<Element> elementList = new ArrayList<Element>();
        for (Node node : each(root, xPath))
        {
            elementList.add((Element) node);
        }
        return elementList;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * returns the first found {@link nu.xom.Node Node} or <code>null</code> if
     * no {@link nu.xom.Nodes Nodes} were found
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the first found {@link nu.xom.Node Node}, <code>null</code> if no
     *         {@link nu.xom.Nodes Nodes} were found
     */
    public static Node queryFirstNode(Node root, String xPath)
    {
        Nodes nodes = queryNodes(root, xPath);
        return (hasNodes(nodes)) ? nodes.get(0) : null;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * returns the found {@link nu.xom.Node Node} casted to an
     * {@link nu.xom.Element}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found {@link nu.xom.Node Node} casted to an
     *         {@link nu.xom.Element}, <code>null</code> if no
     *         {@link nu.xom.Node Node} was found
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirstNode(Node, String)}
     */
    public static Element queryFirstElement(Node root, String xPath)
    {
        return (Element) queryFirstNode(root, xPath);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * returns the first found value or <code>null</code> if no value was found
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the first found value, <code>null</code> if no value was found
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirstNode(Node, String)}
     */
    public static String queryFirst(Node root, String xPath)
    {
        Node node = queryFirstNode(root, xPath);
        return node != null ? node.getValue() : null;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * returns the first found value converted by the given
     * {@link java.util.function.Function Converter} or <code>null</code> if no
     * value was found
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @param converterClass
     *            the {@link java.util.function.Function Converter} (given by
     *            the {@link java.lang.Class}) to convert the value with
     * @return the first found value converted by the given
     *         {@link java.util.function.Function Converter}, <code>null</code>
     *         if no value was found
     */
    public static <T> T queryFirst(Node root, String xPath, Class<? extends Function<String, T>> converterClass)
    {
        return getConverter(converterClass).apply(queryFirst(root, xPath));
    }

    /**
     * Tests, if the given xPath-query on the given {@link nu.xom.Node Node}
     * results to an existing {@link nu.xom.Node Node}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return true if a {@link nu.xom.Node Node} exists, false if not
     */
    public static boolean hasNode(Node root, String xPath)
    {
        return queryFirst(root, xPath) != null;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to an {@link java.lang.Integer Integer}. Values
     * are trimmed before conversion
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to an {@link java.lang.Integer
     *         Integer}, <code>null</code> if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a
     *             {@link java.lang.Integer Integer}
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToIntegerConverter}
     */
    public static Integer queryInteger(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToIntegerConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a primitive integer (<code>int</code>).
     * Values are trimmed before conversion
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a primitive integer
     *         (<code>int</code>), zero if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>int</code>
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToIntegerConverter}
     */
    public static int queryPrimitiveInteger(Node root, String xPath)
    {
        Integer value = queryFirst(root, xPath, ToIntegerConverter.class);
        return value != null ? value.intValue() : 0;
    }

    /**
     * Alias for
     * {@link com.avides.xpath.utils.XPathUtils#queryPrimitiveInteger(Node, String)}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a primitive integer
     *         (<code>int</code>), zero if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>int</code>
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryPrimitiveInteger(Node, String)}
     */
    public static int queryInt(Node root, String xPath)
    {
        return queryPrimitiveInteger(root, xPath);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a {@link java.lang.Long Long}. Values are
     * trimmed before conversion
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.lang.Long Long},
     *         <code>null</code> if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a {@link java.lang.Long
     *             Long}
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToLongConverter}
     */
    public static Long queryLong(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToLongConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a primitive <code>long</code>. Values are
     * trimmed before conversion
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a primitive <code>long</code>, zero
     *         if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>long</code>
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToLongConverter}
     */
    public static long queryPrimitiveLong(Node root, String xPath)
    {
        Long value = queryFirst(root, xPath, ToLongConverter.class);
        return value != null ? value.longValue() : 0;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a {@link java.lang.Short Short}. Values are
     * trimmed before conversion
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.lang.Short Short},
     *         <code>null</code> if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a {@link java.lang.Short
     *             Short}
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToShortConverter}
     */
    public static Short queryShort(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToShortConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a primitive <code>short</code>. Values are
     * trimmed before conversion
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a primitive <code>short</code>, zero
     *         if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>short</code>
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToShortConverter}
     */
    public static short queryPrimitiveShort(Node root, String xPath)
    {
        Short value = queryFirst(root, xPath, ToShortConverter.class);
        return value != null ? value.shortValue() : 0;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a {@link java.lang.Double Double}. Values are
     * trimmed before conversion, commas are replaced by dots
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.lang.Double Double},
     *         <code>null</code> if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a
     *             {@link java.lang.Double Double}
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToDoubleConverter}
     */
    public static Double queryDouble(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToDoubleConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a primitive <code>double</code>. Values are
     * trimmed before conversion, commas are replaced by dots
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a primitive <code>double</code>,
     *         zero if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>double</code>
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToDoubleConverter}
     */
    public static double queryPrimitiveDouble(Node root, String xPath)
    {
        Double value = queryFirst(root, xPath, ToDoubleConverter.class);
        return value != null ? value.doubleValue() : 0;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a {@link java.lang.Float Float}. Values are
     * trimmed before conversion, commas are replaced by dots
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.lang.Float Float},
     *         <code>null</code> if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a {@link java.lang.Float
     *             Float}
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToFloatConverter}
     */
    public static Float queryFloat(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToFloatConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a primitive <code>float</code>. Values are
     * trimmed before conversion, commas are replaced by dots
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a primitive <code>float</code>, zero
     *         if no value was found
     * @throws NumberFormatException
     *             if the value can not be converted to a <code>float</code>
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToFloatConverter}
     */
    public static float queryPrimitiveFloat(Node root, String xPath)
    {
        Float value = queryFirst(root, xPath, ToFloatConverter.class);
        return value != null ? value.floatValue() : 0;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a {@link java.lang.Boolean Boolean}. Allowed
     * values for {@link java.lang.Boolean#TRUE TRUE} are (not case-sensitive):
     * 'true', 'yes', 'on', '1', 'positive', 'correct', 'ja', 'oui', 'si', 'sì'.
     * All other values will result to {@link java.lang.Boolean#FALSE FALSE}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.lang.Boolean Boolean},
     *         <code>null</code> if no value was found
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToBooleanConverter}
     */
    public static Boolean queryBoolean(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToBooleanConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a primitive <code>boolean</code>. Allowed
     * values for <code>boolean true</code> are (not case-sensitive): 'true',
     * 'yes', 'on', '1', 'positive', 'correct', 'ja', 'oui', 'si', 'sì'. All
     * other values will result to <code>boolean false</code>
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a primitive <code>boolean</code>,
     *         <code>boolean false</code> if no value was found
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToBooleanConverter}
     */
    public static boolean queryPrimitiveBoolean(Node root, String xPath)
    {
        Boolean value = queryFirst(root, xPath, ToBooleanConverter.class);
        return value != null ? value.booleanValue() : false;
    }

    /**
     * Alias for
     * {@link com.avides.xpath.utils.XPathUtils#queryPrimitiveBoolean(Node, String)}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a primitive <code>boolean</code>,
     *         <code>boolean false</codes> if no value was found
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryPrimitiveBoolean(Node, String)}
     */
    public static boolean queryBool(Node root, String xPath)
    {
        return queryPrimitiveBoolean(root, xPath);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a {@link java.lang.Character Character}. If
     * the found value is longer than 1 character, the first character will be
     * returned. If no value was found or the found value is empty,
     * <code>null</code> will be returned.
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.lang.Character
     *         Character}, <code>null</code> if no value was found or the found
     *         value is empty
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToCharacterConverter}
     */
    public static Character queryCharacter(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToCharacterConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a primitive character (<code>char</code>). If
     * the found value is longer than 1 character, the first character will be
     * returned. If no value was found or the found value is empty, a
     * zero-valued characater will be returned.
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a primitive character
     *         (<code>char</code>), zero-valued character if no value was found
     *         or the found value is empty
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToCharacterConverter}
     */
    public static char queryPrimitiveCharacter(Node root, String xPath)
    {
        Character value = queryFirst(root, xPath, ToCharacterConverter.class);
        return value != null ? value.charValue() : 0;
    }

    /**
     * Alias for
     * {@link com.avides.xpath.utils.XPathUtils#queryPrimitiveCharacter(Node, String)}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a primitive character
     *         (<code>char</code>), zero-valued character if no value was found
     *         or the found value is empty
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryPrimitiveCharacter(Node, String)}
     */
    public static char queryChar(Node root, String xPath)
    {
        return queryPrimitiveCharacter(root, xPath);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to the wanted {@link java.lang.Enum Enum}. If
     * the value is empty, <code>null</code> will be returned
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.lang.Enum Enum},
     *         <code>null</code> if no value was found, is empty or could not be
     *         converted to a {@link java.lang.Enum Enum}
     *
     * @throws IllegalArgumentException
     *             if the enum-value can not be parsed
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     */
    public static <E extends Enum<E>> E queryEnum(Node root, String xPath, Class<E> enumType)
    {
        String value = queryFirst(root, xPath);
        return (value != null) && !value.isEmpty() ? Enum.valueOf(enumType, value) : null;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a {@link java.time.ZonedDateTime
     * ZonedDateTime}. The value has to match the format
     * {@link java.time.format.DateTimeFormatter#ISO_ZONED_DATE_TIME}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.time.ZonedDateTime
     *         ZonedDateTime}, <code>null</code> if no value was found
     * @throws java.time.format.DateTimeParseException
     *             if value can not be converted to
     *             {@link java.time.ZonedDateTime ZonedDateTime}
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToZonedDateTimeConverter}
     */
    public static ZonedDateTime queryZonedDateTime(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToZonedDateTimeConverter.class);
    }

    /**
     * Executes the given xPath-query on the given node and converts the found
     * value to a {@link java.time.LocalDateTime LocalDateTime}. The value has
     * to match the format
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.time.LocalDateTime
     *         LocalDateTime}, <code>null</code> if no value was found
     * @throws java.time.format.DateTimeParseException
     *             if value can not be converted to
     *             {@link java.time.LocalDateTime LocalDateTime}
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToLocalDateTimeConverter}
     */
    public static LocalDateTime queryLocalDateTime(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToLocalDateTimeConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * converts the found value to a {@link java.time.LocalDate LocalDate}. The
     * value has to match the format
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.time.LocalDate
     *         LocalDate}, <code>null</code> if no value was found
     * @throws java.time.format.DateTimeParseException
     *             if value can not be converted to {@link java.time.LocalDate
     *             LocalDate}
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToLocalDateConverter}
     */
    public static LocalDate queryLocalDate(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToLocalDateConverter.class);
    }

    /**
     * Executes the given xPath-query on the given node and converts the found
     * value to a {@link java.time.LocalTime LocalTime}. The value has to match
     * the format {@link java.time.format.DateTimeFormatter#ISO_LOCAL_TIME}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return the found value converted to a {@link java.time.LocalTime
     *         LocalTime}, <code>null</code> if no value was found or could not
     *         be converted to a {@link java.time.LocalTime LocalTime}
     * @throws java.time.format.DateTimeParseException
     *             if value can not be converted to {@link java.time.LocalTime
     *             LocalTime}
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryFirst(Node, String, Class)}
     * @see {@link com.avides.xpath.utils.converter.ToLocalTimeConverter}
     */
    public static LocalTime queryLocalTime(Node root, String xPath)
    {
        return queryFirst(root, xPath, ToLocalTimeConverter.class);
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * returns a {@link java.util.List List} of the found values
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @return a {@link java.util.List List} of the found values, will never be
     *         <code>null</code>
     */
    public static List<String> queryList(Node root, String xPath)
    {
        List<String> list = new ArrayList<>();
        each(root, xPath).forEach(node -> list.add(node.getValue()));
        return list;
    }

    /**
     * Executes the given xPath-query on the given {@link nu.xom.Node Node} and
     * returns a {@link java.util.List List} of the found values converted by
     * the given {@link java.util.function.Function Converter}
     *
     * @param <T>
     *            the type of the resulting list-items, determined by the given
     *            {@link java.util.function.Function Converter}
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given xPath-query
     *            from
     * @param xPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node}
     * @param converterClass
     *            the {@link java.util.function.Function Converter} (given by
     *            the {@link java.lang.Class}) to convert the values with
     * @return a {@link java.util.List List} of the found values converted by
     *         the given {@link java.util.function.Function Converter}, will
     *         never be <code>null</code>
     */
    public static <T> List<T> queryList(Node root, String xPath, Class<? extends Function<String, T>> converterClass)
    {
        Function<String, T> converter = getConverter(converterClass);
        return queryList(root, xPath).stream().map(item -> converter.apply(item)).collect(toList());
    }

    /**
     * Executes the give entryXPath on the given {@link nu.xom.Node Node} to
     * create an {@link java.util.Map.Entry Entry} for each found
     * {@link nu.xom.Node Node}. From that found {@link nu.xom.Node Node} each
     * entry-key is queried by the given keySubXPath and each entry-value is
     * queried by the given valueSubXPath
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given
     *            entryXPath-query from
     * @param entryXPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node} for all entries
     * @param keySubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the key of that {@link java.util.Map.Entry Entry}
     * @param valueSubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the value of that {@link java.util.Map.Entry Entry}
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
     * Executes the give entryXPath on the given {@link nu.xom.Node Node} to
     * create an {@link java.util.Map.Entry Entry} for each found
     * {@link nu.xom.Node Node}. From that found {@link nu.xom.Node Node} each
     * entry-key is queried by the given keySubXPath and each entry-value is
     * queried by the given valueSubXPath. Each entry-key is converted by the
     * given {@link java.util.function.Function KeyConverter} and each
     * entry-value is converted by the given {@link java.util.function.Function
     * ValueConverter}
     *
     * @param root
     *            the {@link nu.xom.Node Node} to execute the given
     *            entryXPath-query from
     * @param entryXPath
     *            the xPath-query to execute on the given {@link nu.xom.Node
     *            Node} for all entries
     * @param keySubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the key of that {@link java.util.Map.Entry Entry}
     * @param valueSubXPath
     *            the xPath-query to execute on every found entry-node to get
     *            the value of that {@link java.util.Map.Entry Entry}
     *
     * @see {@link com.avides.xpath.utils.XPathUtils#queryMap(Node, String, String, String)}
     */
    public static <K, V> Map<K, V> queryMap(Node root, String entryXPath, String keySubXPath, String valueSubXPath,
        Class<? extends Function<String, K>> keyConverterClass, Class<? extends Function<String, V>> valueConverterClass)
    {
        Map<String, String> stringMap = queryMap(root, entryXPath, keySubXPath, valueSubXPath);
        Map<K, V> map = new HashMap<>(stringMap.size());
        Function<String, K> keyConverter = getConverter(keyConverterClass);
        Function<String, V> valueConverter = getConverter(valueConverterClass);
        for (Entry<String, String> entry : stringMap.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            map.put(keyConverter.apply(key), valueConverter.apply(value));
        }
        return map;
    }

    /**
     * Simply tests, if the given {@link nu.xom.Nodes Nodes} are not
     * <code>null</code> and not empty
     *
     * @param nodes
     *            the {@link nu.xom.Nodes Nodes} to test on
     * @return true if the {@link nu.xom.Nodes Nodes} are not <code>null</code>
     *         and not empty, false if the {@link nu.xom.Nodes Nodes} are
     *         <code>null</code> or empty
     */
    public static boolean hasNodes(Nodes nodes)
    {
        return ((nodes != null) && (nodes.size() > 0));
    }

    /**
     * Registeres the given explicit instance of a
     * {@link java.util.function.Function Converter} to use, if a
     * {@link java.util.function.Function Converter} of that class is used
     *
     * @param converter
     *            The instance of the {@link java.util.function.Function
     *            Converter} to use if a {@link java.util.function.Function
     *            Converter} of that class is used
     */
    @SuppressWarnings("unchecked")
    public static void registerConverterInstance(Function<String, ?> converter)
    {
        converterCache.put((Class<? extends Function<String, ?>>) converter.getClass(), converter);
    }

    @SuppressWarnings("unchecked")
    private static <T> Function<String, T> getConverter(Class<? extends Function<String, T>> converterClass)
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