package com.avides.xpath.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import nu.xom.Node;
import nu.xom.Nodes;

/**
 * Implementation of Iterator and Iterable to enable iteration over
 * {@link nu.xom.Nodes}
 *
 * @author Martin Schumacher
 * @since 1.0.0.RELEASE
 *
 * @see com.avides.xpath.utils.XPathUtils#each(Node, String)
 * @see com.avides.xpath.utils.XPathUtils#queryNodeList(Node, String)
 */
public class NodeIterator implements Iterator<Node>, Iterable<Node>
{
    private Nodes nodes;
    private int position = 0;

    public NodeIterator(Nodes nodes)
    {
        this.nodes = nodes;
    }

    @Override
    public boolean hasNext()
    {
        return position < nodes.size();
    }

    @Override
    public Node next()
    {
        if (!hasNext())
        {
            throw new NoSuchElementException();
        }
        return nodes.get(position++);
    }

    @Override
    public void remove()
    {
        // this method is not be used
    }

    @Override
    public Iterator<Node> iterator()
    {
        return this;
    }
}