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
import org.xml.sax.InputSource;
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Implementations {
    public static class Item {
        private final String mID;
        private final String mName;

        Item() {
            mID = null;
            mName = null;
        }

        Item(Node node) {
            final NamedNodeMap attributes = node.getAttributes();
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
            if (!(other instanceof Item))
                return false;
            return ((Item)other).getID().equals(mID);
        }
    }

    private static final Item NULL_ITEM = new Item();
    private static final Element sImplementations = loadImplementations();

    private static Element loadImplementations() {
        ClassLoader loader = Implementations.class.getClassLoader();
        InputStream input = loader.getResourceAsStream("data/implementations.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(input)).getDocumentElement();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Item> getImplementations() {
        final List<Item> items = new ArrayList<>();
        if (sImplementations != null) {
            NodeList nodes = sImplementations.getElementsByTagName("implementation");
            for (int i = 0; i != nodes.getLength(); ++i) {
                items.add(new Item(nodes.item(i)));
            }
        }
        return items;
    }

    public static Item getDefaultImplementation() {
        if (sImplementations != null) {
            NodeList nodes = sImplementations.getElementsByTagName("implementation");
            for (int i = 0; i != nodes.getLength(); ++i) {
                Node node = nodes.item(i);
                if (node.getAttributes().getNamedItem("default").getNodeValue().equals("true")) {
                    return new Item(node);
                }
            }
        }
        return NULL_ITEM;
    }
}
