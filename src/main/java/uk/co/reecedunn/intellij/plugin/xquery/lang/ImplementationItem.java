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
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;

public class ImplementationItem {
    private final String mID;
    private final String mName;

    ImplementationItem() {
        mID = null;
        mName = null;
    }

    ImplementationItem(Element element) {
        final NamedNodeMap attributes = element.getAttributes();
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
        return ((ImplementationItem) other).getID().equals(mID);
    }
}
