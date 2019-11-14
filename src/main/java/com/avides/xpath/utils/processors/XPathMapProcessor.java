package com.avides.xpath.utils.processors;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;

import com.avides.xpath.utils.XPathUtils;
import com.avides.xpath.utils.annotations.XPathMap;

import nu.xom.Element;

/**
 * @author Martin Schumacher
 * @since 1.0.3.RELEASE
 */
public class XPathMapProcessor extends AbstractAnnotationProcessor
{
    public XPathMapProcessor(Element root, Field field, Object target, Map<Class<?>, Function<String, ?>> defaultToTypeConverters)
    {
        super(root, field, target, defaultToTypeConverters);
    }

    public <K, V> void process()
    {
        XPathMap xPathMap = field.getAnnotation(XPathMap.class);
        if (xPathMap != null)
        {
            @SuppressWarnings("unchecked") Map<K, V> value = XPathUtils.queryMap(root, xPathMap.entryXPath(), xPathMap.keySubXPath(), xPathMap.valueSubXPath(),
                (Class<Function<String, K>>) xPathMap.keyConverterClass(), (Class<Function<String, V>>) xPathMap.valueConverterClass(),
                (Class<K>) xPathMap.keySubTypeClass(), (Class<V>) xPathMap.valueSubTypeClass());
            setFieldValueSimple(field, target, value, true);
        }
    }
}
