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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryImplementation;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryDialectTest extends TestCase {
    public void testName() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.getName(), is("XQuery 1.0"));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.getName(), is("XQuery 3.0"));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.getName(), is("XQuery 3.1"));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.getName(), is("XQuery and Update Facility 1.0"));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.getName(), is("XQuery and Update Facility 3.0"));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.getName(), is("XQuery and Full Text 1.0"));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.getName(), is("XQuery and Full Text 3.0"));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.getName(), is("MarkLogic 3.2"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_5.getName(), is("MarkLogic 5"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_6.getName(), is("MarkLogic 6"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.getName(), is("MarkLogic 7"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.getName(), is("MarkLogic 8"));
    }

    public void testLanguageVersion() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_3_0));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_3_1));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_3_0));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_3_0));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.getLanguageVersion(), is(XQueryVersion.XQUERY_0_9_MARKLOGIC));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_5.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_6.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
    }

    public void testDescription() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.getDescription(), is("W3C Recommendation 23 January 2007"));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.getDescription(), is("W3C Recommendation 08 April 2014"));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.getDescription(), is("W3C Candidate Recommendation 17 December 2015"));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.getDescription(), is("W3C Recommendation 17 March 2011"));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.getDescription(), is("W3C Last Call Working Draft 19 February 2015"));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.getDescription(), is("W3C Recommendation 17 March 2011"));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.getDescription(), is("W3C Recommendation 24 November 2015"));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.getDescription(), is(nullValue()));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_5.getDescription(), is(nullValue()));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_6.getDescription(), is(nullValue()));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.getDescription(), is(nullValue()));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.getDescription(), is(nullValue()));
    }

    public void testReference() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.getReference(), is("https://www.w3.org/TR/2007/REC-xquery-20070123/"));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.getReference(), is("https://www.w3.org/TR/2014/REC-xquery-30-20140408/"));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.getReference(), is("https://www.w3.org/TR/2015/CR-xquery-31-20151217/"));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.getReference(), is("https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/"));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.getReference(), is("https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/"));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.getReference(), is("https://www.w3.org/TR/2011/REC-xpath-full-text-10-20110317/"));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.getReference(), is("https://www.w3.org/TR/2015/REC-xpath-full-text-30-20151124/"));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.getReference(), is("https://docs.marklogic.com/guide/xquery/dialects#id_65735"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_5.getReference(), is("https://docs.marklogic.com/5.0/guide/xquery/dialects"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_6.getReference(), is("https://docs.marklogic.com/6.0/guide/xquery/dialects"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.getReference(), is("https://docs.marklogic.com/7.0/guide/xquery/dialects"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.getReference(), is("https://docs.marklogic.com/8.0/guide/xquery/dialects"));
    }

    public void testToString() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.toString(), is("1.0/W3C"));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.toString(), is("3.0/W3C"));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.toString(), is("3.1/W3C"));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.toString(), is("1.0+update/W3C"));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.toString(), is("3.0+update/W3C"));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.toString(), is("1.0+full-text/W3C"));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.toString(), is("3.0+full-text/W3C"));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.toString(), is("0.9-ml/3.2"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_5.toString(), is("1.0-ml/5"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_6.toString(), is("1.0-ml/6"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.toString(), is("1.0-ml/7"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.toString(), is("1.0-ml/8"));
    }

    public void testParse() {
        assertThat(XQueryDialect.parse(null), is(nullValue()));

        assertThat(XQueryDialect.parse("1.0/W3C"), is(XQueryDialect.XQUERY_1_0_W3C));
        assertThat(XQueryDialect.parse("3.0/W3C"), is(XQueryDialect.XQUERY_3_0_W3C));
        assertThat(XQueryDialect.parse("3.1/W3C"), is(XQueryDialect.XQUERY_3_1_W3C));

        assertThat(XQueryDialect.parse("1.0+update/W3C"), is(XQueryDialect.XQUERY_1_0_UPDATE_W3C));
        assertThat(XQueryDialect.parse("3.0+update/W3C"), is(XQueryDialect.XQUERY_3_0_UPDATE_W3C));

        assertThat(XQueryDialect.parse("1.0+full-text/W3C"), is(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C));
        assertThat(XQueryDialect.parse("3.0+full-text/W3C"), is(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C));

        assertThat(XQueryDialect.parse("0.9-ml/3.2"), is(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2));
        assertThat(XQueryDialect.parse("1.0-ml/5"), is(XQueryDialect.XQUERY_1_0_MARKLOGIC_5));
        assertThat(XQueryDialect.parse("1.0-ml/6"), is(XQueryDialect.XQUERY_1_0_MARKLOGIC_6));
        assertThat(XQueryDialect.parse("1.0-ml/7"), is(XQueryDialect.XQUERY_1_0_MARKLOGIC_7));
        assertThat(XQueryDialect.parse("1.0-ml/8"), is(XQueryDialect.XQUERY_1_0_MARKLOGIC_8));

        assertThat(XQueryDialect.parse("0.9"), is(nullValue()));
        assertThat(XQueryDialect.parse("1.0"), is(nullValue()));
        assertThat(XQueryDialect.parse("1.1"), is(nullValue()));
        assertThat(XQueryDialect.parse("2.0"), is(nullValue()));
        assertThat(XQueryDialect.parse("3.0"), is(nullValue()));
        assertThat(XQueryDialect.parse("3.1"), is(nullValue()));

        assertThat(XQueryDialect.parse("1.0-ml/6.2"), is(nullValue()));
        assertThat(XQueryDialect.parse("1.1-ml/7"), is(nullValue()));
        assertThat(XQueryDialect.parse("1.0-und"), is(nullValue()));
        assertThat(XQueryDialect.parse("2.0/W3C"), is(nullValue()));
    }
}
