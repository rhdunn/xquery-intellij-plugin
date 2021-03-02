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
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCaseEx
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@DisplayName("XQuery Update Facility 3.0 - Lexer")
class UpdateFacilityLexerTest : LexerTestCaseEx() {
    override val lexer: Lexer = run {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(
            XQueryLexer(), 0x50000000, 0,
            XQueryLexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
        )
        lexer.addState(
            XQueryLexer(), 0x60000000, 0,
            XQueryLexer.STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG
        )
        lexer
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (26) FunctionDecl")
    fun functionDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("updating", XQueryTokenType.K_UPDATING)
        token("function", XPathTokenType.K_FUNCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("as", XPathTokenType.K_AS)
        token("external", XQueryTokenType.K_EXTERNAL)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (141) RevalidationDecl")
    fun revalidationDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("revalidation", XQueryTokenType.K_REVALIDATION)
        token("strict", XQueryTokenType.K_STRICT)
        token("lax", XQueryTokenType.K_LAX)
        token("skip", XQueryTokenType.K_SKIP)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (142) InsertExprTargetChoice")
    fun insertExprTargetChoice() {
        token("as", XPathTokenType.K_AS)
        token("first", XQueryTokenType.K_FIRST)
        token("last", XQueryTokenType.K_LAST)
        token("into", XQueryTokenType.K_INTO)
        token("after", XQueryTokenType.K_AFTER)
        token("before", XQueryTokenType.K_BEFORE)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (143) InsertExpr")
    fun insertExpr() {
        token("insert", XQueryTokenType.K_INSERT)
        token("node", XPathTokenType.K_NODE)
        token("nodes", XQueryTokenType.K_NODES)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (144) DeleteExpr")
    fun deleteExpr() {
        token("delete", XQueryTokenType.K_DELETE)
        token("node", XPathTokenType.K_NODE)
        token("nodes", XQueryTokenType.K_NODES)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (145) ReplaceExpr")
    fun replaceExpr() {
        token("replace", XQueryTokenType.K_REPLACE)
        token("value", XQueryTokenType.K_VALUE)
        token("of", XPathTokenType.K_OF)
        token("node", XPathTokenType.K_NODE)
        token("with", XPathTokenType.K_WITH)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (146) RenameExpr")
    fun renameExpr() {
        token("rename", XQueryTokenType.K_RENAME)
        token("node", XPathTokenType.K_NODE)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (150) TransformExpr ; XQuery Update Facility 3.0 EBNF (208) CopyModifyExpr")
    fun transformExpr() {
        token("copy", XQueryTokenType.K_COPY)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token(":=", XPathTokenType.ASSIGN_EQUAL)
        token(",", XPathTokenType.COMMA)
        token("modify", XQueryTokenType.K_MODIFY)
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery Update Facility 3.0 EBNF (27) CompatibilityAnnotation")
    fun compatibilityAnnotation() {
        token("updating", XQueryTokenType.K_UPDATING)
    }

    @Test
    @DisplayName("XQuery Update Facility 3.0 EBNF (97) TransformWithExpr")
    fun transformWithExpr() {
        token("transform", XQueryTokenType.K_TRANSFORM)
        token("with", XPathTokenType.K_WITH)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery Update Facility 3.0 EBNF (207) UpdatingFunctionCall")
    fun updatingFunctionCall() {
        token("invoke", XQueryTokenType.K_INVOKE)
        token("updating", XQueryTokenType.K_UPDATING)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }
}
