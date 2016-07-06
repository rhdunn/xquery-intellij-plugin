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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryImplementation;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryImplementationTest extends TestCase {
    public void testName() {
        assertThat(XQueryImplementation.W3C.getName(), is("W3C Specification"));
        assertThat(XQueryImplementation.MARKLOGIC.getName(), is("MarkLogic"));
    }

    public void testReference() {
        assertThat(XQueryImplementation.W3C.getReference(), is("https://www.w3.org/XML/Query/"));
        assertThat(XQueryImplementation.MARKLOGIC.getReference(), is("https://docs.marklogic.com"));
    }

    public void testToString() {
        assertThat(XQueryImplementation.W3C.toString(), is("W3C"));
        assertThat(XQueryImplementation.MARKLOGIC.toString(), is("MarkLogic"));
    }

    public void testParse() {
        assertThat(XQueryImplementation.parse(null), is(nullValue()));

        assertThat(XQueryImplementation.parse("W3C"), is(XQueryImplementation.W3C));
        assertThat(XQueryImplementation.parse("MarkLogic"), is(XQueryImplementation.MARKLOGIC));

        assertThat(XQueryImplementation.parse("ml6"), is(nullValue()));
        assertThat(XQueryImplementation.parse("0.9"), is(nullValue()));
        assertThat(XQueryImplementation.parse("testdb"), is(nullValue()));
        assertThat(XQueryImplementation.parse("2.0/w3c"), is(nullValue()));
    }

    public void testDefaultVersion() {
        assertThat(XQueryImplementation.W3C.getDefaultVersion(), is(XQueryVersion.XQUERY_3_0));
        assertThat(XQueryImplementation.MARKLOGIC.getDefaultVersion(), is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
    }

    public void testVersions() {
        assertThat(XQueryImplementation.W3C.getVersions().length, is(3));
        assertThat(XQueryImplementation.W3C.getVersions()[0], is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryImplementation.W3C.getVersions()[1], is(XQueryVersion.XQUERY_3_0));
        assertThat(XQueryImplementation.W3C.getVersions()[2], is(XQueryVersion.XQUERY_3_1));

        assertThat(XQueryImplementation.MARKLOGIC.getVersions().length, is(3));
        assertThat(XQueryImplementation.MARKLOGIC.getVersions()[0], is(XQueryVersion.XQUERY_0_9_MARKLOGIC));
        assertThat(XQueryImplementation.MARKLOGIC.getVersions()[1], is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryImplementation.MARKLOGIC.getVersions()[2], is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
    }
}
