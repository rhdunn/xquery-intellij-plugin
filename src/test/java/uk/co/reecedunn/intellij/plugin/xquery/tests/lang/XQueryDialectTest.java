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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuerySpecification;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryDialectTest extends TestCase {
    public void testName() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.getName(), is("XQuery 1.0"));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.getName(), is("XQuery 1.0 (Second Edition)"));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.getName(), is("XQuery 3.0"));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.getName(), is("XQuery 3.1"));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.getName(), is("XQuery and Update Facility 1.0"));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.getName(), is("XQuery and Update Facility 3.0"));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.getName(), is("XQuery and Full Text 1.0"));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.getName(), is("XQuery and Full Text 3.0"));

        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.getName(), is("XQuery and Formal Semantics 1.0"));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.getName(), is("XQuery and Formal Semantics 1.0 (Second Edition)"));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.getName(), is("MarkLogic 3.2"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.getName(), is("MarkLogic 7"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.getName(), is("MarkLogic 8"));
    }

    public void testLanguageVersion() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_3_0));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_3_1));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_3_0));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_3_0));

        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.getLanguageVersion(), is(XQueryVersion.XQUERY_0_9_MARKLOGIC));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.getLanguageVersion(), is(XQueryVersion.XQUERY_1_0_MARKLOGIC));
    }

    public void testFeatureCheck_MinimalConformance() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.XQUERY_1_0), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.XQUERY_3_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.XQUERY_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.XQUERY_3_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.XQUERY_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.XQUERY_3_0), is(true));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.XQUERY_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.XQUERY_3_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.XQUERY_3_1), is(true));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.XQUERY_1_0), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.XQUERY_3_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.XQUERY_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.XQUERY_3_0), is(true));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.XQUERY_1_0), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.XQUERY_3_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.XQUERY_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.XQUERY_3_0), is(true));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.XQUERY_1_0), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.XQUERY_3_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.XQUERY_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.XQUERY_3_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(true));
        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.XQUERY_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.XQUERY_3_0), is(false));
        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.XQUERY_1_0), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.XQUERY_3_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.XQUERY_1_0_20030502), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.XQUERY_1_0), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.XQUERY_1_0_20101214), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.XQUERY_3_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.XQUERY_3_1), is(false));
    }

    public void testFeatureCheck_StaticTyping() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(true));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.SEMANTICS_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.SEMANTICS_1_0_20101214), is(false));
    }

    public void testFeatureCheck_FullText() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(true));

        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.FULL_TEXT_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.FULL_TEXT_3_0), is(false));
    }

    public void testFeatureCheck_UpdateFacility() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_W3C.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.UPDATE_1_0), is(true));
        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.conformsTo(XQuerySpecification.UPDATE_3_0), is(true));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));

        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.UPDATE_1_0), is(false));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.conformsTo(XQuerySpecification.UPDATE_3_0), is(false));
    }

    public void testToString() {
        assertThat(XQueryDialect.XQUERY_1_0_W3C.toString(), is("1.0/W3C"));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_W3C.toString(), is("1.0/W3C-2ed"));
        assertThat(XQueryDialect.XQUERY_3_0_W3C.toString(), is("3.0/W3C"));
        assertThat(XQueryDialect.XQUERY_3_1_W3C.toString(), is("3.1/W3C"));

        assertThat(XQueryDialect.XQUERY_1_0_UPDATE_W3C.toString(), is("1.0+update/W3C"));
        assertThat(XQueryDialect.XQUERY_3_0_UPDATE_W3C.toString(), is("3.0+update/W3C"));

        assertThat(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C.toString(), is("1.0+full-text/W3C"));
        assertThat(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C.toString(), is("3.0+full-text/W3C"));

        assertThat(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C.toString(), is("1.0+formal-semantics/W3C"));
        assertThat(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C.toString(), is("1.0+formal-semantics/W3C-2ed"));

        assertThat(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2.toString(), is("0.9-ml/3.2"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_7.toString(), is("1.0-ml/7"));
        assertThat(XQueryDialect.XQUERY_1_0_MARKLOGIC_8.toString(), is("1.0-ml/8"));
    }

    public void testParse() {
        assertThat(XQueryDialect.parse(null), is(nullValue()));

        assertThat(XQueryDialect.parse("1.0/W3C"), is(XQueryDialect.XQUERY_1_0_W3C));
        assertThat(XQueryDialect.parse("1.0/W3C-2ed"), is(XQueryDialect.XQUERY_1_0_2ED_W3C));
        assertThat(XQueryDialect.parse("3.0/W3C"), is(XQueryDialect.XQUERY_3_0_W3C));
        assertThat(XQueryDialect.parse("3.1/W3C"), is(XQueryDialect.XQUERY_3_1_W3C));

        assertThat(XQueryDialect.parse("1.0+update/W3C"), is(XQueryDialect.XQUERY_1_0_UPDATE_W3C));
        assertThat(XQueryDialect.parse("3.0+update/W3C"), is(XQueryDialect.XQUERY_3_0_UPDATE_W3C));

        assertThat(XQueryDialect.parse("1.0+full-text/W3C"), is(XQueryDialect.XQUERY_1_0_FULL_TEXT_W3C));
        assertThat(XQueryDialect.parse("3.0+full-text/W3C"), is(XQueryDialect.XQUERY_3_0_FULL_TEXT_W3C));

        assertThat(XQueryDialect.parse("1.0+formal-semantics/W3C"), is(XQueryDialect.XQUERY_1_0_SEMANTICS_W3C));
        assertThat(XQueryDialect.parse("1.0+formal-semantics/W3C-2ed"), is(XQueryDialect.XQUERY_1_0_2ED_SEMANTICS_W3C));

        assertThat(XQueryDialect.parse("0.9-ml/3.2"), is(XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2));
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
