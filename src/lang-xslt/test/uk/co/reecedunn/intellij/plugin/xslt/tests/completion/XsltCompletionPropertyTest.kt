/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.tests.completion

import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.XsltSpec
import uk.co.reecedunn.intellij.plugin.xslt.completion.xpath.property.XPathVersion
import uk.co.reecedunn.intellij.plugin.xslt.completion.xpath.property.XsltVersion
import uk.co.reecedunn.intellij.plugin.xslt.tests.parser.ParserTestCase

@Suppress("RedundantVisibilityModifier")
@DisplayName("XSLT 3.0 - Code Completion - Properties")
class XsltCompletionPropertyTest : ParserTestCase(XMLParserDefinition()) {
    override val pluginId: PluginId = PluginId.getId("XsltCompletionPropertyTest")

    @Nested
    @DisplayName("generic-element-type")
    internal inner class GenericElementType {
        @Test
        @DisplayName("@use-when = expression")
        fun useWhen() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "comment", "use-when")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "comment", "use-when")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:accumulator")
    internal inner class Accumulator {
        @Test
        @DisplayName("@initial-value = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "accumulator", "initial-value")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "accumulator", "initial-value")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:accumulator-rule")
    internal inner class AccumulatorRule {
        @Test
        @DisplayName("@match = pattern")
        fun match() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "accumulator-rule", "match")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "accumulator-rule", "match")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "accumulator-rule", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "accumulator-rule", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:analyze-string")
    internal inner class AnalyzeString {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "analyze-string", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "analyze-string", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:apply-templates")
    internal inner class ApplyTemplates {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "apply-templates", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "apply-templates", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:assert")
    internal inner class Assert {
        @Test
        @DisplayName("@test = expression")
        fun test() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "assert", "test")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "assert", "test")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "assert", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "assert", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:attribute")
    internal inner class Attribute {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "attribute", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "attribute", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:break")
    internal inner class Break {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "break", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "break", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:catch")
    internal inner class Catch {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "catch", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "catch", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:comment")
    internal inner class Comment {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "comment", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "comment", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:copy")
    internal inner class Copy {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "copy", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "copy", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:copy-of")
    internal inner class CopyOf {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "copy-of", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "copy-of", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:evaluate")
    internal inner class Evaluate {
        @Test
        @DisplayName("@xpath = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "evaluate", "xpath")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "evaluate", "xpath")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }

        @Test
        @DisplayName("@with-params = expression")
        fun withParams() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "evaluate", "with-params")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "evaluate", "with-params")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }

        @Test
        @DisplayName("@context-item = expression")
        fun contextItem() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "evaluate", "context-item")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "evaluate", "context-item")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }

        @Test
        @DisplayName("@namespace-context = expression")
        fun namespaceContext() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "evaluate", "namespace-context")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "evaluate", "namespace-context")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:for-each")
    internal inner class ForEach {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "for-each", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "for-each", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:for-each-group")
    internal inner class ForEachGroup {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "for-each-group", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "for-each-group", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }

        @Test
        @DisplayName("@group-by = expression")
        fun groupBy() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "for-each-group", "group-by")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "for-each-group", "group-by")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }

        @Test
        @DisplayName("@group-adjacent = expression")
        fun groupAdjacent() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "for-each-group", "group-adjacent")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "for-each-group", "group-adjacent")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }

        @Test
        @DisplayName("@group-ending-with = pattern")
        fun groupEndingWith() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "for-each-group", "group-ending-with")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "for-each-group", "group-ending-with")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }

        @Test
        @DisplayName("@group-starting-with = pattern")
        fun groupStartingWith() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "for-each-group", "group-starting-with")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "for-each-group", "group-starting-with")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:if")
    internal inner class If {
        @Test
        @DisplayName("@test = expression")
        fun test() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "if", "test")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "if", "test")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:iterate")
    internal inner class Iterate {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "iterate", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "iterate", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:key")
    internal inner class Key {
        @Test
        @DisplayName("@match = pattern")
        fun match() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "key", "match")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "key", "match")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }

        @Test
        @DisplayName("@use = expression")
        fun use() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "key", "use")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "key", "use")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:map-entry")
    internal inner class MapEntry {
        @Test
        @DisplayName("@key = expression")
        fun key() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "map-entry", "key")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "map-entry", "key")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "map-entry", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "map-entry", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:merge-key")
    internal inner class MergeKey {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "merge-key", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "merge-key", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:merge-source")
    internal inner class MergeSource {
        @Test
        @DisplayName("@for-each-item = expression")
        fun forEachItem() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "merge-source", "for-each-item")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "merge-source", "for-each-item")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }

        @Test
        @DisplayName("@for-each-source = expression")
        fun forEachSource() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "merge-source", "for-each-source")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "merge-source", "for-each-source")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "merge-source", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "merge-source", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:message")
    internal inner class Message {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "message", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "message", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:namespace")
    internal inner class Namespace {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "namespace", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "namespace", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:number")
    internal inner class Number {
        @Test
        @DisplayName("@value = expression")
        fun value() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "number", "value")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "number", "value")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }

        @Test
        @DisplayName("@count = pattern")
        fun count() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "number", "count")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "number", "count")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }

        @Test
        @DisplayName("@from = pattern")
        fun from() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "number", "from")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "number", "from")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "number", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "number", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:on-completion")
    internal inner class OnCompletion {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "on-completion", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "on-completion", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:on-empty")
    internal inner class OnEmpty {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "on-empty", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "on-empty", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:on-non-empty")
    internal inner class OnNonEmpty {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "on-non-empty", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "on-non-empty", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:package")
    internal inner class Package {
        @Test
        @DisplayName("@version = number [3.0]")
        fun version30() {
            val ss = attribute("tests/xslt/xslt-3.0-package.xsl", "package", "version")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_3_1_20170321))
        }
    }

    @Nested
    @DisplayName("xsl:param")
    internal inner class Param {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "param", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "param", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:perform-sort")
    internal inner class PerformSort {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "perform-sort", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "perform-sort", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:processing-instruction")
    internal inner class ProcessingInstruction {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "processing-instruction", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "processing-instruction", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:sequence")
    internal inner class Sequence {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "sequence", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "sequence", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("xsl:sort")
    internal inner class Sort {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "sort", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "sort", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:stylesheet")
    internal inner class Stylesheet {
        @Test
        @DisplayName("@version = number [1.0]")
        fun version10() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "stylesheet", "version")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_1_0_19991116))
        }

        @Test
        @DisplayName("@version = number [2.0]")
        fun version20() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "stylesheet", "version")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_2_0_20070123))
        }

        @Test
        @DisplayName("@version = number [3.0]")
        fun version30() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "stylesheet", "version")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_3_1_20170321))
        }
    }

    @Nested
    @DisplayName("xsl:template")
    internal inner class Template {
        @Test
        @DisplayName("@match = pattern")
        fun match() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "template", "match")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "template", "match")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }

        @Test
        @DisplayName("@name = qname")
        fun name() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "template", "name")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "template", "name")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:transform")
    internal inner class Transform {
        @Test
        @DisplayName("@version = number [1.0]")
        fun version10() {
            val ss = attribute("tests/xslt/xslt-1.0-transform.xsl", "transform", "version")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_1_0_19991116))
        }

        @Test
        @DisplayName("@version = number [2.0]")
        fun version20() {
            val ss = attribute("tests/xslt/xslt-2.0-transform.xsl", "transform", "version")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_2_0_20070123))
        }

        @Test
        @DisplayName("@version = number [3.0]")
        fun version30() {
            val ss = attribute("tests/xslt/xslt-3.0-transform.xsl", "transform", "version")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_3_1_20170321))
        }
    }

    @Nested
    @DisplayName("xsl:try")
    internal inner class Try {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "try", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "try", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
        }
    }

    @Nested
    @DisplayName("xsl:value-of")
    internal inner class ValueOf {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "value-of", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "value-of", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:variable")
    internal inner class Variable {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "variable", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "variable", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:when")
    internal inner class When {
        @Test
        @DisplayName("@test = expression")
        fun test() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "when", "test")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "when", "test")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("xsl:with-param")
    internal inner class WithParam {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "with-param", "select")[0]
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "with-param", "select")[0]
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
        }
    }
}
