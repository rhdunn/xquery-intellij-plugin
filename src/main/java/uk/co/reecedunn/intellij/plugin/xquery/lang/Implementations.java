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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Implementations {
    private static final ImplementationItem NULL_ITEM = new ImplementationItem();
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

    private static List<ImplementationItem> getItemsByTagName(Element element, String tagName) {
        final List<ImplementationItem> items = new ArrayList<>();
        if (element != null) {
            NodeList nodes = element.getElementsByTagName(tagName);
            for (int i = 0; i != nodes.getLength(); ++i) {
                items.add(new ImplementationItem((Element)nodes.item(i)));
            }
        }
        return items;
    }

    private static ImplementationItem getDefaultItemByTagName(Element element, String tagName) {
        if (element != null) {
            NodeList nodes = element.getElementsByTagName(tagName);
            for (int i = 0; i != nodes.getLength(); ++i) {
                Node node = nodes.item(i);
                if (node.getAttributes().getNamedItem("default").getNodeValue().equals("true")) {
                    return new ImplementationItem((Element)node);
                }
            }
        }
        return NULL_ITEM;
    }

    public static List<ImplementationItem> getImplementations() {
        return getItemsByTagName(sImplementations, "implementation");
    }

    public static ImplementationItem getDefaultImplementation() {
        return getDefaultItemByTagName(sImplementations, "implementation");
    }
}
