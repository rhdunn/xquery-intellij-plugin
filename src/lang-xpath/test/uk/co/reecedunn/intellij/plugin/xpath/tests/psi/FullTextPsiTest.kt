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
package uk.co.reecedunn.intellij.plugin.xpath.tests.psi

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lang.findUsages.XPathFindUsagesProvider
import uk.co.reecedunn.intellij.plugin.xdm.context.XstUsageType
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTStopWords
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTThesaurusID
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("ClassName")
@DisplayName("XPath 3.1 with Full Text 3.0 - IntelliJ Program Structure Interface (PSI)")
private class FullTextPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery and XPath Full Text 3.0 (3.4.3) Thesaurus Option")
    internal inner class ThesaurusOption {
        @Nested
        @DisplayName("Full Text 3.0 EBNF (227) FTThesaurusID")
        internal inner class FTThesaurusID_XPath31 {
            @Test
            @DisplayName("thesaurus uri")
            fun uri() {
                val thesaurus = parse<FTThesaurusID>(
                    "x contains text 'test' using thesaurus at 'http://www.example.com'"
                )[0]
                assertThat(thesaurus.source!!.data, `is`("http://www.example.com"))
                assertThat(thesaurus.source!!.context, `is`(XdmUriContext.Thesaurus))
                assertThat(thesaurus.source!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
            }

            @Test
            @DisplayName("missing thesaurus uri")
            fun wordList_single() {
                val thesaurus = parse<FTThesaurusID>("x contains text 'test' using thesaurus at")[0]
                assertThat(thesaurus.source, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery and XPath Full Text 3.0 (3.4.7) Stop Word Option")
    internal inner class StopWordOption {
        @Nested
        @DisplayName("Full Text 3.0 EBNF (230) FTStopWords")
        internal inner class FTStopWords_XPath31 {
            @Test
            @DisplayName("stop word uri")
            fun uri() {
                val words = parse<FTStopWords>("x contains text 'test' using stop words at 'http://www.example.com'")[0]
                assertThat(words.source!!.data, `is`("http://www.example.com"))
                assertThat(words.source!!.context, `is`(XdmUriContext.StopWords))
                assertThat(words.source!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
            }

            @Test
            @DisplayName("stop words; single")
            fun wordList_single() {
                val words = parse<FTStopWords>("x contains text 'test' using stop words ('lorem'))")[0]
                assertThat(words.source, `is`(nullValue()))
            }

            @Test
            @DisplayName("stop words; multiple")
            fun wordList_multiple() {
                val words = parse<FTStopWords>("x contains text 'test' using stop words ('lorem', 'ipsum', 'dolor'))")[0]
                assertThat(words.source, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery and XPath Full Text 3.0 (3.8) Extension Selections")
    internal inner class ExtensionSelections {
        @Nested
        @DisplayName("Full Text 3.0 EBNF (107) Pragma")
        internal inner class Pragma {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("() contains text (# test #)")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("pragma"))
                assertThat(qname.element!!.getUsageType(), `is`(XstUsageType.Pragma))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }
    }
}
