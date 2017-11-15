package com.avides.xpath.utils.processors;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.avides.xpath.utils.XPathUtils;
import com.avides.xpath.utils.annotations.XPathList;

import nu.xom.Element;

/**
 * @author Martin Schumacher
 * @since 1.0.3.RELEASE
 */
public class XPathListProcessor extends AbstractAnnotationProcessor
{
    public XPathListProcessor(Element root, Field field, Object target, Map<Class<?>, Function<String, ?>> defaultToTypeConverters)
    {
        super(root, field, target, defaultToTypeConverters);
    }

    public <T> void process()
    {
        XPathList xPathList = field.getAnnotation(XPathList.class);
        if (xPathList != null)
        {
            @SuppressWarnings("unchecked") List<T> value = XPathUtils.queryList(root, xPathList.value(),
                (Class<Function<String, T>>) xPathList.converterClass(), (Class<T>) xPathList.subTypeClass());
            setFieldValueSimple(field, target, value, true);
        }
    }
}