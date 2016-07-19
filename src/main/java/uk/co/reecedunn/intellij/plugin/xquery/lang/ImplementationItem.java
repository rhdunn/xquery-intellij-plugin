/*
 * Copyright (C) 2016 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.xquery.lang;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

import java.util.ArrayList;
import java.util.List;

public class ImplementationItem {
    public static final String IMPLEMENTATION_VERSION = "version";

    private static final ImplementationItem NULL_ITEM = new ImplementationItem();

    private final Element mElement;
    private final String mID;
    private final String mName;

    private ImplementationItem() {
        mElement = null;
        mID = null;
        mName = null;
    }

    ImplementationItem(Element element) {
        mElement = element;
        final NamedNodeMap attributes = element.getAttributes();
        if (element.getTagName().equals("implementations")) {
            mID = null;
            mName = null;
        } else {
            mID = attributes.getNamedItem("id").getNodeValue();
            if (attributes.getNamedItem("localized").getNodeValue().equals("true")) {
                mName = XQueryBundle.message(attributes.getNamedItem("name").getNodeValue());
            } else {
                mName = attributes.getNamedItem("name").getNodeValue();
            }
        }
    }

    public String getID() {
        return mID;
    }

    public String toString() {
        return mName;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ImplementationItem))
            return false;
        return ((ImplementationItem) other).getID().equals(mID);
    }

    public List<ImplementationItem> getItems(String tagName) {
        final List<ImplementationItem> items = new ArrayList<>();
        if (mElement != null) {
            NodeList nodes = mElement.getElementsByTagName(tagName);
            for (int i = 0; i != nodes.getLength(); ++i) {
                items.add(new ImplementationItem((Element)nodes.item(i)));
            }
        }
        return items;
    }

    public ImplementationItem getDefaultItem(String tagName) {
        if (mElement != null) {
            NodeList nodes = mElement.getElementsByTagName(tagName);
            for (int i = 0; i != nodes.getLength(); ++i) {
                Node node = nodes.item(i);
                if (node.getAttributes().getNamedItem("default").getNodeValue().equals("true")) {
                    return new ImplementationItem((Element)node);
                }
            }
        }
        return NULL_ITEM;
    }
}
