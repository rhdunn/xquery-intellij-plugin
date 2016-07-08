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
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementation;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ImplementationTest extends TestCase {
    public void testName() {
        assertThat(Implementation.W3C.getName(), is("W3C Specification"));
        assertThat(Implementation.MARKLOGIC.getName(), is("MarkLogic"));
    }

    public void testReference() {
        assertThat(Implementation.W3C.getReference(), is("https://www.w3.org/XML/Query/"));
        assertThat(Implementation.MARKLOGIC.getReference(), is("https://docs.marklogic.com"));
    }

    public void testToString() {
        assertThat(Implementation.W3C.toString(), is("W3C"));
        assertThat(Implementation.MARKLOGIC.toString(), is("MarkLogic"));
    }

    public void testParse() {
        assertThat(Implementation.parse(null), is(nullValue()));

        assertThat(Implementation.parse("W3C"), is(Implementation.W3C));
        assertThat(Implementation.parse("MarkLogic"), is(Implementation.MARKLOGIC));

        assertThat(Implementation.parse("ml6"), is(nullValue()));
        assertThat(Implementation.parse("0.9"), is(nullValue()));
        assertThat(Implementation.parse("testdb"), is(nullValue()));
        assertThat(Implementation.parse("2.0/w3c"), is(nullValue()));
    }

    public void testDefaultVersion() {
        assertThat(Implementation.W3C.getDefaultVersion(), is(XQueryVersion.XQUERY_3_0));
        assertThat(Implementation.MARKLOGIC.getDefaultVersion(), is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
    }

    public void testVersions() {
        assertThat(Implementation.W3C.getVersions().length, is(3));
        assertThat(Implementation.W3C.getVersions()[0], is(XQueryVersion.XQUERY_1_0));
        assertThat(Implementation.W3C.getVersions()[1], is(XQueryVersion.XQUERY_3_0));
        assertThat(Implementation.W3C.getVersions()[2], is(XQueryVersion.XQUERY_3_1));

        assertThat(Implementation.MARKLOGIC.getVersions().length, is(3));
        assertThat(Implementation.MARKLOGIC.getVersions()[0], is(XQueryVersion.XQUERY_0_9_MARKLOGIC));
        assertThat(Implementation.MARKLOGIC.getVersions()[1], is(XQueryVersion.XQUERY_1_0));
        assertThat(Implementation.MARKLOGIC.getVersions()[2], is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
    }
}
