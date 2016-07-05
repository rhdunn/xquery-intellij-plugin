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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryVersionTest extends TestCase {
    public void testName() {
        assertThat(XQueryVersion.XQUERY_0_9_MARKLOGIC.getName(), is("0.9-ml"));
        assertThat(XQueryVersion.XQUERY_1_0.getName(), is("1.0"));
        assertThat(XQueryVersion.XQUERY_1_0_MARKLOGIC.getName(), is("1.0-ml"));
        assertThat(XQueryVersion.XQUERY_3_0.getName(), is("3.0"));
        assertThat(XQueryVersion.XQUERY_3_1.getName(), is("3.1"));
    }

    public void testDescription() {
        assertThat(XQueryVersion.XQUERY_0_9_MARKLOGIC.getDescription(), is("MarkLogic 3.2 Compatibility XQuery Dialect"));
        assertThat(XQueryVersion.XQUERY_1_0.getDescription(), is("W3C XQuery 1.0"));
        assertThat(XQueryVersion.XQUERY_1_0_MARKLOGIC.getDescription(), is("MarkLogic XQuery Dialect"));
        assertThat(XQueryVersion.XQUERY_3_0.getDescription(), is("W3C XQuery 3.0"));
        assertThat(XQueryVersion.XQUERY_3_1.getDescription(), is("W3C XQuery 3.1"));
    }

    public void testToString() {
        assertThat(XQueryVersion.XQUERY_0_9_MARKLOGIC.toString(), is("0.9-ml"));
        assertThat(XQueryVersion.XQUERY_1_0.toString(), is("1.0"));
        assertThat(XQueryVersion.XQUERY_1_0_MARKLOGIC.toString(), is("1.0-ml"));
        assertThat(XQueryVersion.XQUERY_3_0.toString(), is("3.0"));
        assertThat(XQueryVersion.XQUERY_3_1.toString(), is("3.1"));
    }

    public void testParse() {
        assertThat(XQueryVersion.parse(null), is(nullValue()));

        assertThat(XQueryVersion.parse("0.9-ml"), is(XQueryVersion.XQUERY_0_9_MARKLOGIC));
        assertThat(XQueryVersion.parse("1.0"), is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryVersion.parse("1.0-ml"), is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
        assertThat(XQueryVersion.parse("3.0"), is(XQueryVersion.XQUERY_3_0));
        assertThat(XQueryVersion.parse("3.1"), is(XQueryVersion.XQUERY_3_1));

        assertThat(XQueryVersion.parse("0.9"), is(nullValue()));
        assertThat(XQueryVersion.parse("1.0-und"), is(nullValue()));
        assertThat(XQueryVersion.parse("1.1"), is(nullValue()));
        assertThat(XQueryVersion.parse("2.0"), is(nullValue()));
    }
}
