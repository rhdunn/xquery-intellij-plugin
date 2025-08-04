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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.parser.parse
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("RedundantVisibilityModifier")
@DisplayName("xqDoc - IntelliJ Program Structure Interface (PSI)")
class XQDocPsiTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQDocPsiTest")

    @Nested
    @DisplayName("Comment")
    internal inner class Comment {
        @Test
        @DisplayName("unclosed comment; no text")
        fun unclosed() {
            val psi = parse<XPathComment>("(:")[0]
            assertThat(psi.isXQDoc, `is`(false))
        }

        @Test
        @DisplayName("without xqDoc marker")
        fun noXQDocMarker() {
            val psi = parse<XPathComment>("(: Lorem ipsum. :)")[0]
            assertThat(psi.isXQDoc, `is`(false))
        }

        @Test
        @DisplayName("with xqDoc marker")
        fun xqDocMarker() {
            val psi = parse<XPathComment>("(:~ Lorem ipsum. :)")[0]
            assertThat(psi.isXQDoc, `is`(true))
        }
    }
}
