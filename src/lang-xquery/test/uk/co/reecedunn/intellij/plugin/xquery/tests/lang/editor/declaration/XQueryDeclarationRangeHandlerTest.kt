/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.editor.declaration

import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.lang.editor.declaration.XQueryFunctionDeclRangeHandler
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - Declaration Range Handler")
class XQueryDeclarationRangeHandlerTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryDeclarationRangeHandlerTest")

    @Nested
    @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("external")
        fun external() {
            val decl = parse<XQueryFunctionDecl>(" (::) declare function fn:true() external;")[0]
            val range = XQueryFunctionDeclRangeHandler().getDeclarationRange(decl)
            assertThat(range, `is`(nullValue()))
        }

        @Test
        @DisplayName("missing FunctionBody")
        fun missingBody() {
            val decl = parse<XQueryFunctionDecl>(" (::) declare function fn:true()")[0]
            val range = XQueryFunctionDeclRangeHandler().getDeclarationRange(decl)
            assertThat(range, `is`(nullValue()))
        }

        @Test
        @DisplayName("with FunctionBody")
        fun functionBody() {
            val decl = parse<XQueryFunctionDecl>(" (::) declare function fn:true() {};")[0]
            val range = XQueryFunctionDeclRangeHandler().getDeclarationRange(decl)
            assertThat(range?.startOffset, `is`(6))
            assertThat(range?.endOffset, `is`(33))
        }
    }
}
