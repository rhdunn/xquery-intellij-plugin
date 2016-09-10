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

import org.w3c.dom.*;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

import java.util.ArrayList;
import java.util.List;

public class ImplementationItem {
    public static final String IMPLEMENTATION_VERSION = "version";
    public static final String IMPLEMENTATION_DIALECT = "dialect";

    public static final String XQUERY = "xquery";
    public static final String UPDATE_FACILITY = "update-facility";
    public static final String FULL_TEXT = "full-text";
    public static final String SCRIPTING = "scripting";

    public static final ImplementationItem NULL_ITEM = new ImplementationItem();

    private final Element mElement;
    private final String mID;
    private final String mName;

    private ImplementationItem() {
        mElement = null;
        mID = null;
        mName = XQueryBundle.message("xquery.implementation.not-supported");
    }

    ImplementationItem(Document document) {
        mElement = document.getDocumentElement();
        mID = null;
        mName = null;
    }

    private ImplementationItem(Element element) {
        final NamedNodeMap attributes = element.getAttributes();
        mElement = element;
        mID = attributes.getNamedItem("id").getNodeValue();
        if (attributes.getNamedItem("localized").getNodeValue().equals("true")) {
            mName = XQueryBundle.message(attributes.getNamedItem("name").getNodeValue());
        } else {
            mName = attributes.getNamedItem("name").getNodeValue();
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
        ImplementationItem otherItem = (ImplementationItem)other;
        if (otherItem.getID() == null) {
            return mID == null;
        }
        return otherItem.getID().equals(mID);
    }

    public List<ImplementationItem> getItems(String tagName) {
        final List<ImplementationItem> items = new ArrayList<>();
        if (mElement != null) {
            NodeList nodes = mElement.getElementsByTagName(tagName);
            for (int i = 0; i != nodes.getLength(); ++i) {
                items.add(new ImplementationItem((Element)nodes.item(i)));
            }
        }
        if (items.isEmpty()) {
            items.add(ImplementationItem.NULL_ITEM);
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

    public XQueryVersion getVersion(String featureName) {
        if (mElement != null) {
            NodeList nodes = mElement.getElementsByTagName(featureName);
            if (nodes.getLength() != 0) {
                Node node = nodes.item(0);
                return XQueryVersion.parse(node.getAttributes().getNamedItem("version").getNodeValue());
            }
        }
        return null;
    }

    public List<ImplementationItem> getItemsByVersion(String tagName, String featureName, XQueryVersion featureVersion) {
        final List<ImplementationItem> items = new ArrayList<>();
        if (mElement != null) {
            NodeList nodes = mElement.getElementsByTagName(tagName);
            for (int i = 0; i != nodes.getLength(); ++i) {
                ImplementationItem item = new ImplementationItem((Element)nodes.item(i));
                if (featureVersion.equals(item.getVersion(featureName))) {
                    items.add(item);
                }
            }
        }
        if (items.isEmpty()) {
            items.add(ImplementationItem.NULL_ITEM);
        }
        return items;
    }

    public ImplementationItem getDefaultItemByVersion(String tagName, String featureName, XQueryVersion featureVersion) {
        if (mElement != null) {
            NodeList nodes = mElement.getElementsByTagName(tagName);
            for (int i = 0; i != nodes.getLength(); ++i) {
                Node node = nodes.item(i);
                if (node.getAttributes().getNamedItem("default").getNodeValue().equals("true")) {
                    ImplementationItem item = new ImplementationItem((Element)nodes.item(i));
                    if (featureVersion.equals(item.getVersion(featureName))) {
                        return item;
                    }
                }
            }
        }
        return NULL_ITEM;
    }

    public List<XQueryVersion> getVersions(String tagName, String featureName) {
        final List<XQueryVersion> items = new ArrayList<>();
        if (mElement != null) {
            NodeList nodes = mElement.getElementsByTagName(tagName);
            for (int i = 0; i != nodes.getLength(); ++i) {
                ImplementationItem item = new ImplementationItem((Element)nodes.item(i));
                XQueryVersion version = item.getVersion(featureName);
                if (!items.contains(version)) {
                    items.add(version);
                }
            }
        }
        return items;
    }

    public XQueryVersion getDefaultVersion(String tagName, String featureName) {
        if (mElement != null) {
            NodeList nodes = mElement.getElementsByTagName(tagName);
            for (int i = 0; i != nodes.getLength(); ++i) {
                Element node = (Element)nodes.item(i);

                NodeList versions = node.getElementsByTagName(featureName);
                for (int j = 0; j != versions.getLength(); ++j) {
                    Node version = versions.item(j);
                    if (version.getAttributes().getNamedItem("default").getNodeValue().equals("true")) {
                        return XQueryVersion.parse(version.getAttributes().getNamedItem("version").getNodeValue());
                    }
                }
            }
        }
        return null;
    }

    public String getSpecification(String featureName) {
        if (mElement != null) {
            NodeList nodes = mElement.getElementsByTagName(featureName);
            if (nodes.getLength() != 0) {
                Node node = nodes.item(0);
                return node.getAttributes().getNamedItem("specification").getNodeValue();
            }
        }
        return null;
    }

    public ImplementationItem getItemById(String id) {
        if (mElement != null) {
            return getItemById(mElement, id);
        }
        return NULL_ITEM;
    }

    private ImplementationItem getItemById(Element element, String id) {
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i != nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;

            Node attributeId = node.getAttributes().getNamedItem("id");
            if (attributeId != null && attributeId.getNodeValue().equals(id)) {
                return new ImplementationItem((Element)node);
            }

            ImplementationItem item = getItemById((Element)node, id);
            if (item != NULL_ITEM) {
                return item;
            }
        }
        return NULL_ITEM;
    }
}
