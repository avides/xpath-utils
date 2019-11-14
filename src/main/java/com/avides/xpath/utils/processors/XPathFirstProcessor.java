package com.avides.xpath.utils.processors;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;

import com.avides.xpath.utils.XPathUtils;
import com.avides.xpath.utils.annotations.XPathFirst;

import nu.xom.Element;

/**
 * @author Martin Schumacher
 * @since 1.0.3.RELEASE
 */
public class XPathFirstProcessor extends AbstractAnnotationProcessor
{
    public XPathFirstProcessor(Element root, Field field, Object target, Map<Class<?>, Function<String, ?>> defaultToTypeConverters)
    {
        super(root, field, target, defaultToTypeConverters);
    }

    public void process()
    {
        XPathFirst xPathFirst = field.getAnnotation(XPathFirst.class);
        if (xPathFirst != null)
        {
            if (xPathFirst.isSubType())
            {
                Element subRoot = XPathUtils.queryFirstElement(root, xPathFirst.value());
                Object value = subRoot != null ? XPathUtils.fromRoot(subRoot, field.getType()) : null;
                setFieldValueSimple(field, target, value, true);
            }
            else
            {

                @SuppressWarnings("unchecked") Object value = XPathUtils.queryFirst(root, xPathFirst.value(),
                    (Class<? extends Function<String, Object>>) xPathFirst.converterClass());
                setFieldValueExtended(field, target, value);
            }
        }
    }
}
