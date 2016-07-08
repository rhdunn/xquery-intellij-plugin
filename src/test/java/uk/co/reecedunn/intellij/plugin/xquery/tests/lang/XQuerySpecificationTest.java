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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuerySpecification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQuerySpecificationTest extends TestCase {
    public void testName() {
        assertThat(XQuerySpecification.XQUERY_1_0_20030502.getName(), is("XQuery 1.0"));
        assertThat(XQuerySpecification.XQUERY_1_0.getName(), is("XQuery 1.0"));
        assertThat(XQuerySpecification.XQUERY_1_0_20101214.getName(), is("XQuery 1.0 (Second Edition)"));
        assertThat(XQuerySpecification.XQUERY_3_0.getName(), is("XQuery 3.0"));
        assertThat(XQuerySpecification.XQUERY_3_1.getName(), is("XQuery 3.1"));

        assertThat(XQuerySpecification.SEMANTICS_1_0.getName(), is("XQuery and Formal Semantics 1.0"));
        assertThat(XQuerySpecification.SEMANTICS_1_0_20101214.getName(), is("XQuery and Formal Semantics 1.0 (Second Edition)"));

        assertThat(XQuerySpecification.FULL_TEXT_1_0.getName(), is("XQuery and Full Text 1.0"));
        assertThat(XQuerySpecification.FULL_TEXT_3_0.getName(), is("XQuery and Full Text 3.0"));

        assertThat(XQuerySpecification.UPDATE_1_0.getName(), is("XQuery and Update Facility 1.0"));
        assertThat(XQuerySpecification.UPDATE_3_0.getName(), is("XQuery and Update Facility 3.0"));
    }

    public void testDescription() {
        assertThat(XQuerySpecification.XQUERY_1_0_20030502.getDescription(), is("W3C Working Draft 02 May 2003"));
        assertThat(XQuerySpecification.XQUERY_1_0.getDescription(), is("W3C Recommendation 23 January 2007"));
        assertThat(XQuerySpecification.XQUERY_1_0_20101214.getDescription(), is("W3C Recommendation 14 December 2010"));
        assertThat(XQuerySpecification.XQUERY_3_0.getDescription(), is("W3C Recommendation 08 April 2014"));
        assertThat(XQuerySpecification.XQUERY_3_1.getDescription(), is("W3C Candidate Recommendation 17 December 2015"));

        assertThat(XQuerySpecification.SEMANTICS_1_0.getDescription(), is("W3C Recommendation 23 January 2007"));
        assertThat(XQuerySpecification.SEMANTICS_1_0_20101214.getDescription(), is("W3C Recommendation 14 December 2010"));

        assertThat(XQuerySpecification.FULL_TEXT_1_0.getDescription(), is("W3C Recommendation 17 March 2011"));
        assertThat(XQuerySpecification.FULL_TEXT_3_0.getDescription(), is("W3C Recommendation 24 November 2015"));

        assertThat(XQuerySpecification.UPDATE_1_0.getDescription(), is("W3C Recommendation 17 March 2011"));
        assertThat(XQuerySpecification.UPDATE_3_0.getDescription(), is("W3C Last Call Working Draft 19 February 2015"));
    }

    public void testReference() {
        assertThat(XQuerySpecification.XQUERY_1_0_20030502.getReference(), is("https://www.w3.org/TR/2003/WD-xquery-20030502/"));
        assertThat(XQuerySpecification.XQUERY_1_0.getReference(), is("https://www.w3.org/TR/2007/REC-xquery-20070123/"));
        assertThat(XQuerySpecification.XQUERY_1_0_20101214.getReference(), is("https://www.w3.org/TR/2010/REC-xquery-20101214/"));
        assertThat(XQuerySpecification.XQUERY_3_0.getReference(), is("https://www.w3.org/TR/2014/REC-xquery-30-20140408/"));
        assertThat(XQuerySpecification.XQUERY_3_1.getReference(), is("https://www.w3.org/TR/2015/CR-xquery-31-20151217/"));

        assertThat(XQuerySpecification.SEMANTICS_1_0.getReference(), is("https://www.w3.org/TR/2007/REC-xquery-semantics-20070123/"));
        assertThat(XQuerySpecification.SEMANTICS_1_0_20101214.getReference(), is("https://www.w3.org/TR/2010/REC-xquery-semantics-20101214/"));

        assertThat(XQuerySpecification.FULL_TEXT_1_0.getReference(), is("https://www.w3.org/TR/2011/REC-xpath-full-text-10-20110317/"));
        assertThat(XQuerySpecification.FULL_TEXT_3_0.getReference(), is("https://www.w3.org/TR/2015/REC-xpath-full-text-30-20151124/"));

        assertThat(XQuerySpecification.UPDATE_1_0.getReference(), is("https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/"));
        assertThat(XQuerySpecification.UPDATE_3_0.getReference(), is("https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/"));
    }

    public void testToString() {
        assertThat(XQuerySpecification.XQUERY_1_0_20030502.toString(), is("xquery-20030502"));
        assertThat(XQuerySpecification.XQUERY_1_0.toString(), is("xquery"));
        assertThat(XQuerySpecification.XQUERY_1_0_20101214.toString(), is("xquery-20101214"));
        assertThat(XQuerySpecification.XQUERY_3_0.toString(), is("xquery-30"));
        assertThat(XQuerySpecification.XQUERY_3_1.toString(), is("xquery-31"));

        assertThat(XQuerySpecification.SEMANTICS_1_0.toString(), is("xquery-semantics"));
        assertThat(XQuerySpecification.SEMANTICS_1_0_20101214.toString(), is("xquery-semantics-20101214"));

        assertThat(XQuerySpecification.FULL_TEXT_1_0.toString(), is("xpath-full-text-10"));
        assertThat(XQuerySpecification.FULL_TEXT_3_0.toString(), is("xpath-full-text-30"));

        assertThat(XQuerySpecification.UPDATE_1_0.toString(), is("xquery-update-10"));
        assertThat(XQuerySpecification.UPDATE_3_0.toString(), is("xquery-update-30"));
    }

    public void testParse() {
        assertThat(XQuerySpecification.parse(null), is(nullValue()));

        assertThat(XQuerySpecification.parse("xquery-20030502"), is(XQuerySpecification.XQUERY_1_0_20030502));
        assertThat(XQuerySpecification.parse("xquery"), is(XQuerySpecification.XQUERY_1_0));
        assertThat(XQuerySpecification.parse("xquery-20101214"), is(XQuerySpecification.XQUERY_1_0_20101214));
        assertThat(XQuerySpecification.parse("xquery-30"), is(XQuerySpecification.XQUERY_3_0));
        assertThat(XQuerySpecification.parse("xquery-31"), is(XQuerySpecification.XQUERY_3_1));

        assertThat(XQuerySpecification.parse("xquery-semantics"), is(XQuerySpecification.SEMANTICS_1_0));
        assertThat(XQuerySpecification.parse("xquery-semantics-20101214"), is(XQuerySpecification.SEMANTICS_1_0_20101214));

        assertThat(XQuerySpecification.parse("xpath-full-text-10"), is(XQuerySpecification.FULL_TEXT_1_0));
        assertThat(XQuerySpecification.parse("xpath-full-text-30"), is(XQuerySpecification.FULL_TEXT_3_0));

        assertThat(XQuerySpecification.parse("xquery-update-10"), is(XQuerySpecification.UPDATE_1_0));
        assertThat(XQuerySpecification.parse("xquery-update-30"), is(XQuerySpecification.UPDATE_3_0));

        assertThat(XQuerySpecification.parse("xquery-10"), is(nullValue()));
        assertThat(XQuerySpecification.parse("xquery-20"), is(nullValue()));

        assertThat(XQuerySpecification.parse("3.1"), is(nullValue()));
        assertThat(XQuerySpecification.parse("3.1/W3C"), is(nullValue()));
        assertThat(XQuerySpecification.parse("1.0-ml/6.2"), is(nullValue()));
        assertThat(XQuerySpecification.parse("1.1-ml/7"), is(nullValue()));
        assertThat(XQuerySpecification.parse("1.0-und"), is(nullValue()));
        assertThat(XQuerySpecification.parse("2.0/W3C"), is(nullValue()));
    }
}
