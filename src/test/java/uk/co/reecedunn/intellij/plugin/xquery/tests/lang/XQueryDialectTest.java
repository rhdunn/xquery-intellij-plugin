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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryDialect;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryDialectTest extends TestCase {
    public void testName() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.getName(), is("1.0/W3C"));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.getName(), is("3.0/W3C"));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.getName(), is("3.1/W3C"));
    }

    public void testLanguageVersion() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_3_0));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_3_1));
    }

    public void testDescription() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.getDescription(), is("XQuery 1.0 (W3C Recommendation 23 January 2007)"));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.getDescription(), is("XQuery 3.0 (W3C Recommendation 08 April 2014)"));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.getDescription(), is("XQuery 3.1 (W3C Candidate Recommendation 17 December 2015)"));
    }

    public void testReference() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.getReference(), is("https://www.w3.org/TR/2007/REC-xquery-20070123/"));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.getReference(), is("https://www.w3.org/TR/2014/REC-xquery-30-20140408/"));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.getReference(), is("https://www.w3.org/TR/2015/CR-xquery-31-20151217/"));
    }

    public void testToString() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.toString(), is("1.0/W3C"));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.toString(), is("3.0/W3C"));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.toString(), is("3.1/W3C"));
    }

    public void testParse() {
        assertThat(XQueryDialect.parse(null), is(nullValue()));

        assertThat(XQueryDialect.parse("1.0/W3C"), is(XQueryDialect.XQUERY_1_0_W3C));
        assertThat(XQueryDialect.parse("3.0/W3C"), is(XQueryDialect.XQUERY_3_0_W3C));
        assertThat(XQueryDialect.parse("3.1/W3C"), is(XQueryDialect.XQUERY_3_1_W3C));

        assertThat(XQueryDialect.parse("0.9"), is(nullValue()));
        assertThat(XQueryDialect.parse("1.0"), is(nullValue()));
        assertThat(XQueryDialect.parse("1.1"), is(nullValue()));
        assertThat(XQueryDialect.parse("2.0"), is(nullValue()));
        assertThat(XQueryDialect.parse("3.0"), is(nullValue()));
        assertThat(XQueryDialect.parse("3.1"), is(nullValue()));

        assertThat(XQueryDialect.parse("1.0-und"), is(nullValue()));
        assertThat(XQueryDialect.parse("2.0/W3C"), is(nullValue()));
    }
}
