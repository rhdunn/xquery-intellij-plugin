/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.basex.tests.query.session

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.basex.query.session.toBaseXInfo
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsDuration

@Suppress("UNCHECKED_CAST", "RedundantInnerClassModifier")
@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - BaseX info text")
class BaseXQueryInfoTest {
    @Nested
    @DisplayName("BaseX 7.0 info")
    inner class BaseX70 {
        private val responseEn = listOf(
            "Results: 4 Items",
            "Updated: 0 Items",
            "Total Time: 413.16 ms",
            ""
        )

        @Test
        @DisplayName("Windows line endings")
        fun windows() {
            val info = responseEn.joinToString("\r\n").toBaseXInfo()

            assertThat(info.size, `is`(4))
            assertThat(info["Results"], `is`("4 Items"))
            assertThat(info["Updated"], `is`("0 Items"))
            assertThat(info["Total Time"], `is`(XsDuration.ns(413160000)))

            val profile = info["Profile"] as HashMap<String, XsDuration>
            assertThat(profile["Total Time"], `is`(XsDuration.ns(413160000)))
            assertThat(profile.size, `is`(1))
        }

        @Test
        @DisplayName("Linux line endings")
        fun linux() {
            val info = responseEn.joinToString("\n").toBaseXInfo()

            assertThat(info.size, `is`(4))
            assertThat(info["Results"], `is`("4 Items"))
            assertThat(info["Updated"], `is`("0 Items"))
            assertThat(info["Total Time"], `is`(XsDuration.ns(413160000)))

            val profile = info["Profile"] as HashMap<String, XsDuration>
            assertThat(profile["Total Time"], `is`(XsDuration.ns(413160000)))
            assertThat(profile.size, `is`(1))
        }

        @Test
        @DisplayName("Mac line endings")
        fun mac() {
            val info = responseEn.joinToString("\r").toBaseXInfo()

            assertThat(info.size, `is`(4))
            assertThat(info["Results"], `is`("4 Items"))
            assertThat(info["Updated"], `is`("0 Items"))
            assertThat(info["Total Time"], `is`(XsDuration.ns(413160000)))

            val profile = info["Profile"] as HashMap<String, XsDuration>
            assertThat(profile["Total Time"], `is`(XsDuration.ns(413160000)))
            assertThat(profile.size, `is`(1))
        }
    }

    @Nested
    @DisplayName("BaseX 8.0 info")
    inner class BaseX80 {
        private val responseEn = listOf(
            "",
            "Query executed in 448.43 ms."
        )

        private val responseFr = listOf(
            "",
            "Requête executée en 448.43 ms."
        )

        private val responseJa = listOf(
            "",
            "448.43 ms のクエリーが実行されました。"
        )

        @Test
        @DisplayName("English; Windows line endings")
        fun windows() {
            val info = responseEn.joinToString("\r\n").toBaseXInfo()

            assertThat(info.size, `is`(1))
            assertThat(info["Total Time"], `is`(XsDuration.ns(448430000)))
        }

        @Test
        @DisplayName("English; Windows line endings")
        fun linux() {
            val info = responseEn.joinToString("\n").toBaseXInfo()

            assertThat(info.size, `is`(1))
            assertThat(info["Total Time"], `is`(XsDuration.ns(448430000)))
        }

        @Test
        @DisplayName("English; Mac line endings")
        fun mac() {
            val info = responseEn.joinToString("\r").toBaseXInfo()

            assertThat(info.size, `is`(1))
            assertThat(info["Total Time"], `is`(XsDuration.ns(448430000)))
        }

        @Test
        @DisplayName("French")
        fun french() {
            val info = responseFr.joinToString("\r").toBaseXInfo()

            assertThat(info.size, `is`(1))
            assertThat(info["Total Time"], `is`(XsDuration.ns(448430000)))
        }

        @Test
        @DisplayName("Japanese")
        fun japanese() {
            val info = responseJa.joinToString("\r").toBaseXInfo()

            assertThat(info.size, `is`(1))
            assertThat(info["Total Time"], `is`(XsDuration.ns(448430000)))
        }
    }

    @Nested
    @DisplayName("BaseX 8.0 queryinfo")
    inner class BaseX80QueryInfo {
        private val responseEn = listOf(
            "",
            "Query:",
            "for \$n in 1 to 10 let \$v := fn:sum(1 to \$n) return 2 * \$v",
            "",
            "Compiling:",
            "- pre-evaluate range expression to range sequence: (1 to 10)",
            "- inline \$v_1",
            "",
            "Optimized Query:",
            "for \$n_0 in (1 to 10) return (2 * sum((1 to \$n_0)))",
            "",
            "Parsing: 0.44 ms",
            "Compiling: 0.34 ms",
            "Evaluating: 0.01 ms",
            "Printing: 0.93 ms",
            "Total Time: 1.71 ms",
            "",
            "Hit(s): 10 Items",
            "Updated: 0 Items",
            "Printed: 29 b",
            "Read Locking: (none)",
            "Write Locking: (none)",
            "",
            "Query executed in 1.71 ms."
        ).joinToString("\r\n")

        private val responseFr = listOf(
            "",
            "Requête:",
            "for \$n in 1 to 10 let \$v := fn:sum(1 to \$n) return 2 * \$v",
            "",
            "Compilation:",
            "- pre-evaluate range expression to range sequence: (1 to 10)",
            "- inline \$v_1",
            "",
            "Requête Optimisée:",
            "for \$n_0 in (1 to 10) return (2 * sum((1 to \$n_0)))",
            "",
            "Analyse: 0.44 ms",
            "Compilation: 0.34 ms",
            "Évaluation: 0.01 ms",
            "Impression: 0.93 ms",
            "Temps total: 1.71 ms",
            "",
            "Hit(s): 10 Items",
            "Mis à jour: 0 Items",
            "Imprimé: 29 b",
            "Blocage en lecture: (none)",
            "Blocage en écriture: (none)",
            "",
            "Requête executée en 1.71 ms."
        ).joinToString("\r\n")

        @Test
        @DisplayName("toBaseXInfo; English")
        fun english() {
            val info = responseEn.toBaseXInfo()
            val compiling = listOf(
                "pre-evaluate range expression to range sequence: (1 to 10)",
                "inline \$v_1"
            )

            assertThat(info.size, `is`(9))
            assertThat(info["Compilation"], `is`(compiling))
            assertThat(info["Optimized Query"], `is`("for \$n_0 in (1 to 10) return (2 * sum((1 to \$n_0)))"))
            assertThat(info["Total Time"], `is`(XsDuration.ns(1710000)))
            assertThat(info["Hit(s)"], `is`("10 Items"))
            assertThat(info["Updated"], `is`("0 Items"))
            assertThat(info["Printed"], `is`("29 b"))
            assertThat(info["Read Locking"], `is`("(none)"))
            assertThat(info["Write Locking"], `is`("(none)"))

            val profile = info["Profile"] as HashMap<String, XsDuration>
            assertThat(profile["Total Time"], `is`(XsDuration.ns(1710000)))
            assertThat(profile["Parsing"], `is`(XsDuration.ns(440000)))
            assertThat(profile["Compiling"], `is`(XsDuration.ns(340000)))
            assertThat(profile["Evaluating"], `is`(XsDuration.ns(10000)))
            assertThat(profile["Printing"], `is`(XsDuration.ns(930000)))
            assertThat(profile.size, `is`(5))
        }

        @Test
        @DisplayName("toBaseXInfo; French")
        fun french() {
            val info = responseFr.toBaseXInfo()

            assertThat(info.size, `is`(7))
            assertThat(info["Total Time"], `is`(XsDuration.ns(1710000)))
            assertThat(info["Hit(s)"], `is`("10 Items"))
            assertThat(info["Mis à jour"], `is`("0 Items"))
            assertThat(info["Imprimé"], `is`("29 b"))
            assertThat(info["Blocage en lecture"], `is`("(none)"))
            assertThat(info["Blocage en écriture"], `is`("(none)"))

            val profile = info["Profile"] as HashMap<String, XsDuration>
            assertThat(profile["Temps total"], `is`(XsDuration.ns(1710000)))
            assertThat(profile["Analyse"], `is`(XsDuration.ns(440000)))
            assertThat(profile["Compilation"], `is`(XsDuration.ns(340000)))
            assertThat(profile["Évaluation"], `is`(XsDuration.ns(10000)))
            assertThat(profile["Impression"], `is`(XsDuration.ns(930000)))
            assertThat(profile.size, `is`(5))
        }
    }

    @Nested
    @DisplayName("BaseX 8.0 xmlplan")
    inner class BaseX80XmlPlan {
        private val responseEn = listOf(
            "",
            "Query Plan:",
            "<QueryPlan compiled=\"true\" updating=\"false\">",
            "  <GFLWOR type=\"xs:anyAtomicType*\">",
            "    <For type=\"xs:integer\" size=\"1\" name=\"\$n\" id=\"0\">",
            "      <RangeSeq from=\"1\" to=\"10\" type=\"xs:integer+\" size=\"10\"/>",
            "    </For>",
            "    <Arith op=\"*\" type=\"xs:anyAtomicType?\">",
            "      <Int type=\"xs:integer\" size=\"1\">2</Int>",
            "      <FnSum name=\"sum\" type=\"xs:anyAtomicType?\">",
            "        <Range type=\"xs:integer*\">",
            "          <Int type=\"xs:integer\" size=\"1\">1</Int>",
            "          <VarRef type=\"xs:integer\" size=\"1\" name=\"\$n\" id=\"0\"/>",
            "        </Range>",
            "      </FnSum>",
            "    </Arith>",
            "  </GFLWOR>",
            "</QueryPlan>",
            "",
            "Query executed in 110.41 ms."
        ).joinToString("\r\n")

        @Test
        @DisplayName("toBaseXInfo")
        fun info() {
            val info = responseEn.toBaseXInfo()
            val queryPlan = listOf(
                "<QueryPlan compiled=\"true\" updating=\"false\">",
                "  <GFLWOR type=\"xs:anyAtomicType*\">",
                "    <For type=\"xs:integer\" size=\"1\" name=\"\$n\" id=\"0\">",
                "      <RangeSeq from=\"1\" to=\"10\" type=\"xs:integer+\" size=\"10\"/>",
                "    </For>",
                "    <Arith op=\"*\" type=\"xs:anyAtomicType?\">",
                "      <Int type=\"xs:integer\" size=\"1\">2</Int>",
                "      <FnSum name=\"sum\" type=\"xs:anyAtomicType?\">",
                "        <Range type=\"xs:integer*\">",
                "          <Int type=\"xs:integer\" size=\"1\">1</Int>",
                "          <VarRef type=\"xs:integer\" size=\"1\" name=\"\$n\" id=\"0\"/>",
                "        </Range>",
                "      </FnSum>",
                "    </Arith>",
                "  </GFLWOR>",
                "</QueryPlan>"
            ).joinToString("\n")

            assertThat(info.size, `is`(2))
            assertThat(info["Query Plan"], `is`(queryPlan))
            assertThat(info["Total Time"], `is`(XsDuration.ns(110410000)))
        }
    }

    @Nested
    @DisplayName("BaseX 8.0 queryinfo and xmlplan")
    inner class BaseX80QueryInfoAndXmlPlan {
        private val responseEn = listOf(
            "",
            "Query Plan:",
            "<QueryPlan compiled=\"true\" updating=\"false\">",
            "  <GFLWOR type=\"xs:anyAtomicType*\">",
            "    <For type=\"xs:integer\" size=\"1\" name=\"\$n\" id=\"0\">",
            "      <RangeSeq from=\"1\" to=\"10\" type=\"xs:integer+\" size=\"10\"/>",
            "    </For>",
            "    <Arith op=\"*\" type=\"xs:anyAtomicType?\">",
            "      <Int type=\"xs:integer\" size=\"1\">2</Int>",
            "      <FnSum name=\"sum\" type=\"xs:anyAtomicType?\">",
            "        <Range type=\"xs:integer*\">",
            "          <Int type=\"xs:integer\" size=\"1\">1</Int>",
            "          <VarRef type=\"xs:integer\" size=\"1\" name=\"\$n\" id=\"0\"/>",
            "        </Range>",
            "      </FnSum>",
            "    </Arith>",
            "  </GFLWOR>",
            "</QueryPlan>",
            "",
            "Query:",
            "for \$n in 1 to 10 let \$v := fn:sum(1 to \$n) return 2 * \$v",
            "",
            "Compiling:",
            "- pre-evaluate range expression to range sequence: (1 to 10)",
            "- inline \$v_1",
            "",
            "Optimized Query:",
            "for \$n_0 in (1 to 10) return (2 * sum((1 to \$n_0)))",
            "",
            "Parsing: 788.89 ms",
            "Compiling: 63.72 ms",
            "Evaluating: 0.74 ms",
            "Printing: 1.59 ms",
            "Total Time: 854.94 ms",
            "",
            "Hit(s): 10 Items",
            "Updated: 0 Items",
            "Printed: 29 b",
            "Read Locking: (none)",
            "Write Locking: (none)",
            "",
            "Query executed in 854.94 ms."
        ).joinToString("\r\n")

        @Test
        @DisplayName("toBaseXInfo")
        fun info() {
            val info = responseEn.toBaseXInfo()
            val queryPlan = listOf(
                "<QueryPlan compiled=\"true\" updating=\"false\">",
                "  <GFLWOR type=\"xs:anyAtomicType*\">",
                "    <For type=\"xs:integer\" size=\"1\" name=\"\$n\" id=\"0\">",
                "      <RangeSeq from=\"1\" to=\"10\" type=\"xs:integer+\" size=\"10\"/>",
                "    </For>",
                "    <Arith op=\"*\" type=\"xs:anyAtomicType?\">",
                "      <Int type=\"xs:integer\" size=\"1\">2</Int>",
                "      <FnSum name=\"sum\" type=\"xs:anyAtomicType?\">",
                "        <Range type=\"xs:integer*\">",
                "          <Int type=\"xs:integer\" size=\"1\">1</Int>",
                "          <VarRef type=\"xs:integer\" size=\"1\" name=\"\$n\" id=\"0\"/>",
                "        </Range>",
                "      </FnSum>",
                "    </Arith>",
                "  </GFLWOR>",
                "</QueryPlan>"
            ).joinToString("\n")
            val compiling = listOf(
                "pre-evaluate range expression to range sequence: (1 to 10)",
                "inline \$v_1"
            )

            assertThat(info.size, `is`(10))
            assertThat(info["Query Plan"], `is`(queryPlan))
            assertThat(info["Compilation"], `is`(compiling))
            assertThat(info["Optimized Query"], `is`("for \$n_0 in (1 to 10) return (2 * sum((1 to \$n_0)))"))
            assertThat(info["Total Time"], `is`(XsDuration.ns(854940000)))
            assertThat(info["Hit(s)"], `is`("10 Items"))
            assertThat(info["Updated"], `is`("0 Items"))
            assertThat(info["Printed"], `is`("29 b"))
            assertThat(info["Read Locking"], `is`("(none)"))
            assertThat(info["Write Locking"], `is`("(none)"))

            val profile = info["Profile"] as HashMap<String, XsDuration>
            assertThat(profile["Total Time"], `is`(XsDuration.ns(854940000)))
            assertThat(profile["Parsing"], `is`(XsDuration.ns(788890000)))
            assertThat(profile["Compiling"], `is`(XsDuration.ns(63720000)))
            assertThat(profile["Evaluating"], `is`(XsDuration.ns(740000)))
            assertThat(profile["Printing"], `is`(XsDuration.ns(1590000)))
            assertThat(profile.size, `is`(5))
        }
    }
}
