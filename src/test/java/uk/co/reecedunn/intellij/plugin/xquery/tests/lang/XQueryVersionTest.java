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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryVersionTest extends TestCase {
    public void testToString() {
        // XQuery Versions:
        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.toString(), is("0.9-ml"));
        assertThat(XQueryVersion.VERSION_1_0.toString(), is("1.0"));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.toString(), is("1.0-ml"));
        assertThat(XQueryVersion.VERSION_3_0.toString(), is("3.0"));
        assertThat(XQueryVersion.VERSION_3_1.toString(), is("3.1"));

        // XQuery Specification Versions:
        assertThat(XQueryVersion.VERSION_1_0_20070123.toString(), is("1.0-20070123"));
        assertThat(XQueryVersion.VERSION_1_0_20101214.toString(), is("1.0-20101214"));
        assertThat(XQueryVersion.VERSION_3_0_20140408.toString(), is("3.0-20140408"));
        assertThat(XQueryVersion.VERSION_3_1_20161213.toString(), is("3.1-20161213"));

        // MarkLogic Versions:
        assertThat(XQueryVersion.VERSION_6_0.toString(), is("6.0"));
        assertThat(XQueryVersion.VERSION_7_0.toString(), is("7.0"));
        assertThat(XQueryVersion.VERSION_8_0.toString(), is("8.0"));

        // Saxon Versions:
        assertThat(XQueryVersion.VERSION_9_4.toString(), is("9.4"));
        assertThat(XQueryVersion.VERSION_9_5.toString(), is("9.5"));
        assertThat(XQueryVersion.VERSION_9_6.toString(), is("9.6"));
        assertThat(XQueryVersion.VERSION_9_7.toString(), is("9.7"));
    }

    public void testParse() {
        assertThat(XQueryVersion.parse(null), is(nullValue()));

        // XQuery Versions:
        assertThat(XQueryVersion.parse("0.9-ml"), is(XQueryVersion.VERSION_0_9_MARKLOGIC));
        assertThat(XQueryVersion.parse("1.0"), is(XQueryVersion.VERSION_1_0));
        assertThat(XQueryVersion.parse("1.0-ml"), is(XQueryVersion.VERSION_1_0_MARKLOGIC));
        assertThat(XQueryVersion.parse("3.0"), is(XQueryVersion.VERSION_3_0));
        assertThat(XQueryVersion.parse("3.1"), is(XQueryVersion.VERSION_3_1));

        // XQuery Specification Versions:
        assertThat(XQueryVersion.parse("1.0-20070123"), is(XQueryVersion.VERSION_1_0_20070123));
        assertThat(XQueryVersion.parse("1.0-20101214"), is(XQueryVersion.VERSION_1_0_20101214));
        assertThat(XQueryVersion.parse("3.0-20140408"), is(XQueryVersion.VERSION_3_0_20140408));
        assertThat(XQueryVersion.parse("3.1-20161213"), is(XQueryVersion.VERSION_3_1_20161213));

        // MarkLogic Versions:
        assertThat(XQueryVersion.parse("6.0"), is(XQueryVersion.VERSION_6_0));
        assertThat(XQueryVersion.parse("7.0"), is(XQueryVersion.VERSION_7_0));
        assertThat(XQueryVersion.parse("8.0"), is(XQueryVersion.VERSION_8_0));

        // Saxon Versions:
        assertThat(XQueryVersion.parse("9.4"), is(XQueryVersion.VERSION_9_4));
        assertThat(XQueryVersion.parse("9.5"), is(XQueryVersion.VERSION_9_5));
        assertThat(XQueryVersion.parse("9.6"), is(XQueryVersion.VERSION_9_6));
        assertThat(XQueryVersion.parse("9.7"), is(XQueryVersion.VERSION_9_7));

        // Unsupported/Unknown Versions:
        assertThat(XQueryVersion.parse("0.9"), is(nullValue()));
        assertThat(XQueryVersion.parse("1.0-und"), is(nullValue()));
        assertThat(XQueryVersion.parse("1.1"), is(nullValue()));
        assertThat(XQueryVersion.parse("2.0"), is(nullValue()));
    }

    public void testToDouble() {
        // XQuery Versions:
        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.toDouble(), is(0.9));
        assertThat(XQueryVersion.VERSION_1_0.toDouble(), is(1.0));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.toDouble(), is(1.0));
        assertThat(XQueryVersion.VERSION_3_0.toDouble(), is(3.0));
        assertThat(XQueryVersion.VERSION_3_1.toDouble(), is(3.1));

        // XQuery Specification Versions:
        assertThat(XQueryVersion.VERSION_1_0_20070123.toDouble(), is(1.0));
        assertThat(XQueryVersion.VERSION_1_0_20101214.toDouble(), is(1.0));
        assertThat(XQueryVersion.VERSION_3_0_20140408.toDouble(), is(3.0));
        assertThat(XQueryVersion.VERSION_3_1_20161213.toDouble(), is(3.1));

        // MarkLogic Versions:
        assertThat(XQueryVersion.VERSION_6_0.toDouble(), is(6.0));
        assertThat(XQueryVersion.VERSION_7_0.toDouble(), is(7.0));
        assertThat(XQueryVersion.VERSION_8_0.toDouble(), is(8.0));

        // Saxon Versions:
        assertThat(XQueryVersion.VERSION_9_4.toDouble(), is(9.4));
        assertThat(XQueryVersion.VERSION_9_5.toDouble(), is(9.5));
        assertThat(XQueryVersion.VERSION_9_6.toDouble(), is(9.6));
        assertThat(XQueryVersion.VERSION_9_7.toDouble(), is(9.7));
    }

    public void testGetDate() {
        // XQuery Versions:
        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.getDate(), is(0));
        assertThat(XQueryVersion.VERSION_1_0.getDate(), is(0));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.getDate(), is(0));
        assertThat(XQueryVersion.VERSION_3_0.getDate(), is(0));
        assertThat(XQueryVersion.VERSION_3_1.getDate(), is(0));

        // XQuery Specification Versions:
        assertThat(XQueryVersion.VERSION_1_0_20070123.getDate(), is(20070123));
        assertThat(XQueryVersion.VERSION_1_0_20101214.getDate(), is(20101214));
        assertThat(XQueryVersion.VERSION_3_0_20140408.getDate(), is(20140408));
        assertThat(XQueryVersion.VERSION_3_1_20161213.getDate(), is(20161213));

        // MarkLogic Versions:
        assertThat(XQueryVersion.VERSION_6_0.getDate(), is(0));
        assertThat(XQueryVersion.VERSION_7_0.getDate(), is(0));
        assertThat(XQueryVersion.VERSION_8_0.getDate(), is(0));

        // Saxon Versions:
        assertThat(XQueryVersion.VERSION_9_4.getDate(), is(0));
        assertThat(XQueryVersion.VERSION_9_5.getDate(), is(0));
        assertThat(XQueryVersion.VERSION_9_6.getDate(), is(0));
        assertThat(XQueryVersion.VERSION_9_7.getDate(), is(0));
    }

    public void testSupportsVersion() {
        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(null), is(false));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_0_9_MARKLOGIC), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_1_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_1_0), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_1_0_MARKLOGIC), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_3_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_3_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_3_0), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_3_0), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_3_1), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_3_1), is(false));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_3_1), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_3_1), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_3_1), is(true));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_3_1), is(true));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_3_1), is(true));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_3_1), is(true));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_3_1), is(true));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_3_1), is(true));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_3_1), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_3_1), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_6_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_6_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_6_0), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_6_0), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_6_0), is(false));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_6_0), is(true));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_6_0), is(true));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_6_0), is(true));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_6_0), is(true));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_6_0), is(true));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_6_0), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_6_0), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_7_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_7_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_7_0), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_7_0), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_7_0), is(false));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_7_0), is(false));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_7_0), is(true));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_7_0), is(true));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_7_0), is(true));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_7_0), is(true));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_7_0), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_7_0), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_8_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_8_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_8_0), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_8_0), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_8_0), is(false));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_8_0), is(false));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_8_0), is(false));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_8_0), is(true));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_8_0), is(true));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_8_0), is(true));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_8_0), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_8_0), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_9_4), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_9_4), is(false));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_9_4), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_9_4), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_9_4), is(false));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_9_4), is(false));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_9_4), is(false));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_9_4), is(false));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_9_4), is(true));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_9_4), is(true));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_9_4), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_9_4), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_9_5), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_9_5), is(false));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_9_5), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_9_5), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_9_5), is(false));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_9_5), is(false));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_9_5), is(false));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_9_5), is(false));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_9_5), is(false));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_9_5), is(true));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_9_5), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_9_5), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_9_6), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_9_6), is(false));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_9_6), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_9_6), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_9_6), is(false));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_9_6), is(false));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_9_6), is(false));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_9_6), is(false));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_9_6), is(false));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_9_6), is(false));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_9_6), is(true));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_9_6), is(true));

        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_6_0.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_7_0.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_8_0.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_9_4.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_9_5.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_9_6.supportsVersion(XQueryVersion.VERSION_9_7), is(false));
        assertThat(XQueryVersion.VERSION_9_7.supportsVersion(XQueryVersion.VERSION_9_7), is(true));
    }

    public void testSupportsVersion_WithDate() {
        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_1_0_20070123.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_1_0_20101214.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_3_0_20140408.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(null), is(false));
        assertThat(XQueryVersion.VERSION_3_1_20161213.supportsVersion(null), is(false));

        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_1_0_20070123.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_1_0_20101214.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_3_0_20140408.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_1_0), is(true));
        assertThat(XQueryVersion.VERSION_3_1_20161213.supportsVersion(XQueryVersion.VERSION_1_0), is(true));

        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_1_0_20070123), is(true));
        assertThat(XQueryVersion.VERSION_1_0_20070123.supportsVersion(XQueryVersion.VERSION_1_0_20070123), is(true));
        assertThat(XQueryVersion.VERSION_1_0_20101214.supportsVersion(XQueryVersion.VERSION_1_0_20070123), is(true));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_1_0_20070123), is(true));
        assertThat(XQueryVersion.VERSION_3_0_20140408.supportsVersion(XQueryVersion.VERSION_1_0_20070123), is(true));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_1_0_20070123), is(true));
        assertThat(XQueryVersion.VERSION_3_1_20161213.supportsVersion(XQueryVersion.VERSION_1_0_20070123), is(true));

        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_1_0_20101214), is(true));
        assertThat(XQueryVersion.VERSION_1_0_20070123.supportsVersion(XQueryVersion.VERSION_1_0_20101214), is(true));
        assertThat(XQueryVersion.VERSION_1_0_20101214.supportsVersion(XQueryVersion.VERSION_1_0_20101214), is(true));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_1_0_20101214), is(true));
        assertThat(XQueryVersion.VERSION_3_0_20140408.supportsVersion(XQueryVersion.VERSION_1_0_20101214), is(true));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_1_0_20101214), is(true));
        assertThat(XQueryVersion.VERSION_3_1_20161213.supportsVersion(XQueryVersion.VERSION_1_0_20101214), is(true));

        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_3_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0_20070123.supportsVersion(XQueryVersion.VERSION_3_0), is(false));
        assertThat(XQueryVersion.VERSION_1_0_20101214.supportsVersion(XQueryVersion.VERSION_3_0), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_3_0_20140408.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_3_0), is(true));
        assertThat(XQueryVersion.VERSION_3_1_20161213.supportsVersion(XQueryVersion.VERSION_3_0), is(true));

        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_3_0_20140408), is(false));
        assertThat(XQueryVersion.VERSION_1_0_20070123.supportsVersion(XQueryVersion.VERSION_3_0_20140408), is(false));
        assertThat(XQueryVersion.VERSION_1_0_20101214.supportsVersion(XQueryVersion.VERSION_3_0_20140408), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_3_0_20140408), is(true));
        assertThat(XQueryVersion.VERSION_3_0_20140408.supportsVersion(XQueryVersion.VERSION_3_0_20140408), is(true));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_3_0_20140408), is(true));
        assertThat(XQueryVersion.VERSION_3_1_20161213.supportsVersion(XQueryVersion.VERSION_3_0_20140408), is(true));

        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_3_1), is(false));
        assertThat(XQueryVersion.VERSION_1_0_20070123.supportsVersion(XQueryVersion.VERSION_3_1), is(false));
        assertThat(XQueryVersion.VERSION_1_0_20101214.supportsVersion(XQueryVersion.VERSION_3_1), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_3_1), is(false));
        assertThat(XQueryVersion.VERSION_3_0_20140408.supportsVersion(XQueryVersion.VERSION_3_1), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_3_1), is(true));
        assertThat(XQueryVersion.VERSION_3_1_20161213.supportsVersion(XQueryVersion.VERSION_3_1), is(true));

        assertThat(XQueryVersion.VERSION_1_0.supportsVersion(XQueryVersion.VERSION_3_1_20161213), is(false));
        assertThat(XQueryVersion.VERSION_1_0_20070123.supportsVersion(XQueryVersion.VERSION_3_1_20161213), is(false));
        assertThat(XQueryVersion.VERSION_1_0_20101214.supportsVersion(XQueryVersion.VERSION_3_1_20161213), is(false));
        assertThat(XQueryVersion.VERSION_3_0.supportsVersion(XQueryVersion.VERSION_3_1_20161213), is(false));
        assertThat(XQueryVersion.VERSION_3_0_20140408.supportsVersion(XQueryVersion.VERSION_3_1_20161213), is(false));
        assertThat(XQueryVersion.VERSION_3_1.supportsVersion(XQueryVersion.VERSION_3_1_20161213), is(true));
        assertThat(XQueryVersion.VERSION_3_1_20161213.supportsVersion(XQueryVersion.VERSION_3_1_20161213), is(true));
    }
}
