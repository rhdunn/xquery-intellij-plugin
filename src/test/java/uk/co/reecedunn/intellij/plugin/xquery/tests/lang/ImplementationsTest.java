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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang;

import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
public class ImplementationsTest extends TestCase {
    @SuppressWarnings("EqualsWithItself")
    public void testNullImplementationItem() {
        final ImplementationItem item = ImplementationItem.NULL_ITEM;

        assertThat(item.getID(), is(nullValue()));
        assertThat(item.toString(), is("Not Supported"));

        assertThat(item.equals(item), is(true));

        final List<?> items = item.getItems(ImplementationItem.IMPLEMENTATION_VERSION);
        assertThat(items.size(), is(1));
        assertThat(items.get(0), is(ImplementationItem.NULL_ITEM));

        assertThat(item.getDefaultItem(ImplementationItem.IMPLEMENTATION_VERSION), is(ImplementationItem.NULL_ITEM));

        final List<?> itemsForXQuery = item.getItemsByVersion(ImplementationItem.IMPLEMENTATION_VERSION, ImplementationItem.XQUERY, XQueryVersion.XQUERY_1_0.toString());
        assertThat(itemsForXQuery.size(), is(1));
        assertThat(itemsForXQuery.get(0), is(ImplementationItem.NULL_ITEM));

        assertThat(item.getDefaultItemForXQueryVersion(ImplementationItem.IMPLEMENTATION_VERSION, XQueryVersion.XQUERY_1_0),
                   is(ImplementationItem.NULL_ITEM));
    }

    public void testImplementations() {
        final List<ImplementationItem> implementations = Implementations.getImplementations();
        assertThat(implementations.size(), is(3));

        assertThat(implementations.get(0).getID(), is("marklogic"));
        assertThat(implementations.get(0).toString(), is("MarkLogic"));

        assertThat(implementations.get(1).getID(), is("saxon"));
        assertThat(implementations.get(1).toString(), is("Saxonica"));

        assertThat(implementations.get(2).getID(), is("w3c"));
        assertThat(implementations.get(2).toString(), is("W3C"));

        assertThat(implementations.get(0).equals(implementations.get(0)), is(true));
        assertThat(implementations.get(1).equals(implementations.get(0)), is(false));
        assertThat(implementations.get(0).equals(implementations.get(0).getID()), is(false));
        assertThat(implementations.get(0).equals(ImplementationItem.NULL_ITEM), is(false));
    }

    public void testDefaultImplementation() {
        ImplementationItem implementation = Implementations.getDefaultImplementation();
        assertThat(implementation.getID(), is("w3c"));
        assertThat(implementation.toString(), is("W3C"));
    }

    public void testItemsForAnUnsupportedTagName() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);

        final List<ImplementationItem> versions = implementation.getItems("does-not-exist");
        assertThat(versions.size(), is(1));
        assertThat(versions.get(0), is(ImplementationItem.NULL_ITEM));
    }

    public void testDefaultItemForAnUnsupportedTagName() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);

        ImplementationItem version = implementation.getDefaultItem("does-not-exist");
        assertThat(version, is(ImplementationItem.NULL_ITEM));
    }

    public void testImplementationVersion() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);

        final List<ImplementationItem> versions = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION);
        assertThat(versions.size(), is(3));

        assertThat(versions.get(0).getID(), is("saxon/HE"));
        assertThat(versions.get(0).toString(), is("Home Edition"));

        assertThat(versions.get(1).getID(), is("saxon/PE"));
        assertThat(versions.get(1).toString(), is("Professional Edition"));

        assertThat(versions.get(2).getID(), is("saxon/EE"));
        assertThat(versions.get(2).toString(), is("Enterprise Edition"));

        assertThat(versions.get(0).equals(versions.get(0)), is(true));
        assertThat(versions.get(1).equals(versions.get(0)), is(false));
        assertThat(versions.get(0).equals(versions.get(0).getID()), is(false));

        assertThat(versions.get(0).equals(ImplementationItem.NULL_ITEM), is(false));
    }

    public void testDefaultImplementationVersion() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);

        ImplementationItem version = implementation.getDefaultItem(ImplementationItem.IMPLEMENTATION_VERSION);
        assertThat(version.getID(), is("saxon/HE"));
        assertThat(version.toString(), is("Home Edition"));
    }

    public void testImplementationDialect() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);
        ImplementationItem version = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION).get(2);

        final List<ImplementationItem> dialects = version.getItemsByVersion(ImplementationItem.IMPLEMENTATION_DIALECT, ImplementationItem.XQUERY, XQueryVersion.XQUERY_3_0.toString());
        assertThat(dialects.size(), is(2));

        assertThat(dialects.get(0).getID(), is("saxon/EE/3.0"));
        assertThat(dialects.get(0).toString(), is("XQuery"));

        assertThat(dialects.get(1).getID(), is("saxon/EE/3.0-update"));
        assertThat(dialects.get(1).toString(), is("XQuery Update Facility 1.0"));

        assertThat(dialects.get(0).equals(dialects.get(0)), is(true));
        assertThat(dialects.get(1).equals(dialects.get(0)), is(false));
        assertThat(dialects.get(0).equals(dialects.get(0).getID()), is(false));

        assertThat(dialects.get(0).equals(ImplementationItem.NULL_ITEM), is(false));
    }

    public void testVersion() {
        List<ImplementationItem> dialects = Implementations.getItemById("marklogic/v7").getItems(ImplementationItem.IMPLEMENTATION_DIALECT);

        assertThat(dialects.get(0).getVersion(ImplementationItem.XQUERY), is("0.9-ml"));
        assertThat(dialects.get(1).getVersion(ImplementationItem.XQUERY), is("1.0"));
        assertThat(dialects.get(2).getVersion(ImplementationItem.XQUERY), is("1.0-ml"));

        assertThat(dialects.get(0).getVersion(ImplementationItem.UPDATE_FACILITY), is(nullValue()));
        assertThat(dialects.get(0).getVersion(ImplementationItem.FULL_TEXT), is(nullValue()));
        assertThat(dialects.get(0).getVersion(ImplementationItem.SCRIPTING), is(nullValue()));
    }

    public void testSpecification() {
        List<ImplementationItem> dialects = Implementations.getItemById("marklogic/v7").getItems(ImplementationItem.IMPLEMENTATION_DIALECT);

        assertThat(dialects.get(0).getSpecification(ImplementationItem.XQUERY), is("https://www.w3.org/TR/2003/WD-xquery-20030502/"));
        assertThat(dialects.get(1).getSpecification(ImplementationItem.XQUERY), is("https://www.w3.org/TR/2010/REC-xquery-20101214/"));
        assertThat(dialects.get(2).getSpecification(ImplementationItem.XQUERY), is("https://www.w3.org/TR/2010/REC-xquery-20101214/"));

        assertThat(dialects.get(0).getSpecification(ImplementationItem.UPDATE_FACILITY), is(nullValue()));
        assertThat(dialects.get(0).getSpecification(ImplementationItem.FULL_TEXT), is(nullValue()));
        assertThat(dialects.get(0).getSpecification(ImplementationItem.SCRIPTING), is(nullValue()));
    }

    public void testItemsByVersion() {
        ImplementationItem implementation = Implementations.getItemById("marklogic/v7");

        List<ImplementationItem> xquery = implementation.getItemsByVersion(ImplementationItem.IMPLEMENTATION_DIALECT, ImplementationItem.XQUERY, "1.0-ml");
        assertThat(xquery.size(), is(1));
        assertThat(xquery.get(0).getID(), is("marklogic/v7/1.0-ml"));

        List<ImplementationItem> scripting = implementation.getItemsByVersion(ImplementationItem.IMPLEMENTATION_DIALECT, ImplementationItem.SCRIPTING, "1.0");
        assertThat(scripting.size(), is(1));
        assertThat(scripting.get(0), is(ImplementationItem.NULL_ITEM));
    }

    public void testDefaultImplementationDialect() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);
        ImplementationItem version = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION).get(2);

        ImplementationItem dialect = version.getDefaultItemForXQueryVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.XQUERY_3_0);
        assertThat(dialect.getID(), is("saxon/EE/3.0-update"));
        assertThat(dialect.toString(), is("XQuery Update Facility 1.0"));
    }

    public void testXQueryVersion() {
        ImplementationItem implementation = Implementations.getImplementations().get(2);
        ImplementationItem version = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION).get(0);

        final List<ImplementationItem> xquery = version.getItems(ImplementationItem.XQUERY_VERSION);
        assertThat(xquery.size(), is(3));

        assertThat(xquery.get(0).getID(), is("w3c/xquery.1.0"));
        assertThat(xquery.get(0).toString(), is("1.0"));

        assertThat(xquery.get(1).getID(), is("w3c/xquery.3.0"));
        assertThat(xquery.get(1).toString(), is("3.0"));

        assertThat(xquery.get(2).getID(), is("w3c/xquery.3.1"));
        assertThat(xquery.get(2).toString(), is("3.1"));

        assertThat(xquery.get(0).equals(xquery.get(0)), is(true));
        assertThat(xquery.get(1).equals(xquery.get(0)), is(false));
        assertThat(xquery.get(0).equals(xquery.get(0).getID()), is(false));

        assertThat(xquery.get(0).equals(ImplementationItem.NULL_ITEM), is(false));
    }

    public void testDefaultXQueryVersion() {
        ImplementationItem implementation = Implementations.getImplementations().get(2);
        ImplementationItem version = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION).get(0);

        ImplementationItem xquery = version.getDefaultItem(ImplementationItem.XQUERY_VERSION);
        assertThat(xquery.getID(), is("w3c/xquery.3.0"));
        assertThat(xquery.toString(), is("3.0"));
    }

    public void testImplementationDialectForAnUnknownVersion() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);
        ImplementationItem version = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION).get(2);

        final List<ImplementationItem> dialects = version.getItemsByVersion(ImplementationItem.IMPLEMENTATION_DIALECT, ImplementationItem.XQUERY, XQueryVersion.XQUERY_1_0_MARKLOGIC.toString());
        assertThat(dialects.size(), is(1));
        assertThat(dialects.get(0), is(ImplementationItem.NULL_ITEM));
    }

    public void testDefaultImplementationDialectForAnUnknownVersion() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);
        ImplementationItem version = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION).get(2);

        ImplementationItem dialect = version.getDefaultItemForXQueryVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.XQUERY_1_0_MARKLOGIC);
        assertThat(dialect, is(ImplementationItem.NULL_ITEM));
    }

    public void testItemById() {
        ImplementationItem item = Implementations.getItemById("marklogic/v7");
        assertThat(item.getID(), is("marklogic/v7"));
        assertThat(item.toString(), is("MarkLogic 7"));

        assertThat(Implementations.getItemById("unknown"), is(ImplementationItem.NULL_ITEM));
        assertThat(ImplementationItem.NULL_ITEM.getItemById("test"), is(ImplementationItem.NULL_ITEM));
    }
}
