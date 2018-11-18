/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lexer

import com.intellij.lexer.Lexer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_START_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@DisplayName("XQuery Update Facility 3.0 - Lexer")
class UpdateFacilityLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(XQueryLexer(), 0x50000000, 0, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
        lexer.addState(XQueryLexer(), 0x60000000, 0, STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
        return lexer
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (26) FunctionDecl")
    fun testFunctionDecl_UpdateFacility10() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "updating", XQueryTokenType.K_UPDATING)
        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (141) RevalidationDecl")
    fun testRevalidationDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "revalidation", XQueryTokenType.K_REVALIDATION)
        matchSingleToken(lexer, "strict", XQueryTokenType.K_STRICT)
        matchSingleToken(lexer, "lax", XQueryTokenType.K_LAX)
        matchSingleToken(lexer, "skip", XQueryTokenType.K_SKIP)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (142) InsertExprTargetChoice")
    fun testInsertExprTargetChoice() {
        val lexer = createLexer()

        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
        matchSingleToken(lexer, "first", XQueryTokenType.K_FIRST)
        matchSingleToken(lexer, "last", XQueryTokenType.K_LAST)
        matchSingleToken(lexer, "into", XQueryTokenType.K_INTO)
        matchSingleToken(lexer, "after", XQueryTokenType.K_AFTER)
        matchSingleToken(lexer, "before", XQueryTokenType.K_BEFORE)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (143) InsertExpr")
    fun testInsertExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "insert", XQueryTokenType.K_INSERT)
        matchSingleToken(lexer, "node", XQueryTokenType.K_NODE)
        matchSingleToken(lexer, "nodes", XQueryTokenType.K_NODES)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (144) DeleteExpr")
    fun testDeleteExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "delete", XQueryTokenType.K_DELETE)
        matchSingleToken(lexer, "node", XQueryTokenType.K_NODE)
        matchSingleToken(lexer, "nodes", XQueryTokenType.K_NODES)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (145) ReplaceExpr")
    fun testReplaceExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "replace", XQueryTokenType.K_REPLACE)
        matchSingleToken(lexer, "value", XQueryTokenType.K_VALUE)
        matchSingleToken(lexer, "of", XPathTokenType.K_OF)
        matchSingleToken(lexer, "node", XQueryTokenType.K_NODE)
        matchSingleToken(lexer, "with", XQueryTokenType.K_WITH)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (146) RenameExpr")
    fun testRenameExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "rename", XQueryTokenType.K_RENAME)
        matchSingleToken(lexer, "node", XQueryTokenType.K_NODE)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (150) TransformExpr ; XQuery Update Facility 3.0 EBNF (208) CopyModifyExpr")
    fun testTransformExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "copy", XQueryTokenType.K_COPY)
        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, ":=", XQueryTokenType.ASSIGN_EQUAL)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, "modify", XQueryTokenType.K_MODIFY)
        matchSingleToken(lexer, "return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery Update Facility 3.0 EBNF (27) CompatibilityAnnotation")
    fun testCompatibilityAnnotation() {
        val lexer = createLexer()

        matchSingleToken(lexer, "updating", XQueryTokenType.K_UPDATING)
    }

    @Test
    @DisplayName("XQuery Update Facility 3.0 EBNF (97) TransformWithExpr")
    fun testTransformWithExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "transform", XQueryTokenType.K_TRANSFORM)
        matchSingleToken(lexer, "with", XQueryTokenType.K_WITH)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery Update Facility 3.0 EBNF (207) UpdatingFunctionCall")
    fun testUpdatingFunctionCall() {
        val lexer = createLexer()

        matchSingleToken(lexer, "invoke", XQueryTokenType.K_INVOKE)
        matchSingleToken(lexer, "updating", XQueryTokenType.K_UPDATING)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }
}
