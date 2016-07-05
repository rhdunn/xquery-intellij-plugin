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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVendor;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryVendorTest extends TestCase {
    public void testID() {
        assertThat(XQueryVendor.W3C.getID(), is("W3C"));
        assertThat(XQueryVendor.MARKLOGIC.getID(), is("MarkLogic"));
    }

    public void testName() {
        assertThat(XQueryVendor.W3C.getName(), is("W3C Standard"));
        assertThat(XQueryVendor.MARKLOGIC.getName(), is("MarkLogic"));
    }

    public void testReference() {
        assertThat(XQueryVendor.W3C.getReference(), is("https://www.w3.org/XML/Query/"));
        assertThat(XQueryVendor.MARKLOGIC.getReference(), is("https://docs.marklogic.com"));
    }

    public void testToString() {
        assertThat(XQueryVendor.W3C.toString(), is("W3C"));
        assertThat(XQueryVendor.MARKLOGIC.toString(), is("MarkLogic"));
    }

    public void testParse() {
        assertThat(XQueryVendor.parse(null), is(nullValue()));

        assertThat(XQueryVendor.parse("W3C"), is(XQueryVendor.W3C));
        assertThat(XQueryVendor.parse("MarkLogic"), is(XQueryVendor.MARKLOGIC));

        assertThat(XQueryVendor.parse("ml6"), is(nullValue()));
        assertThat(XQueryVendor.parse("0.9"), is(nullValue()));
        assertThat(XQueryVendor.parse("testdb"), is(nullValue()));
        assertThat(XQueryVendor.parse("2.0/w3c"), is(nullValue()));
    }

    public void testDefaultVersion() {
        assertThat(XQueryVendor.W3C.getDefaultVersion(), is(XQueryVersion.XQUERY_3_0));
        assertThat(XQueryVendor.MARKLOGIC.getDefaultVersion(), is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
    }

    public void testVersions() {
        assertThat(XQueryVendor.W3C.getVersions().length, is(3));
        assertThat(XQueryVendor.W3C.getVersions()[0], is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryVendor.W3C.getVersions()[1], is(XQueryVersion.XQUERY_3_0));
        assertThat(XQueryVendor.W3C.getVersions()[2], is(XQueryVersion.XQUERY_3_1));

        assertThat(XQueryVendor.MARKLOGIC.getVersions().length, is(3));
        assertThat(XQueryVendor.MARKLOGIC.getVersions()[0], is(XQueryVersion.XQUERY_0_9_MARKLOGIC));
        assertThat(XQueryVendor.MARKLOGIC.getVersions()[1], is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryVendor.MARKLOGIC.getVersions()[2], is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
    }
}
