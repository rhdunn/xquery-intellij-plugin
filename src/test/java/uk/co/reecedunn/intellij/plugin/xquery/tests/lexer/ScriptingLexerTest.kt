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
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_START_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@DisplayName("XQuery Scripting Extensions 1.0 - Lexer")
class ScriptingLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(XQueryLexer(), 0x50000000, 0, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
        lexer.addState(XQueryLexer(), 0x60000000, 0, STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
        return lexer
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (24) VarDecl")
    fun testVarDecl_Scripting10() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "unassignable", XQueryTokenType.K_UNASSIGNABLE)
        matchSingleToken(lexer, "assignable", XQueryTokenType.K_ASSIGNABLE)
        matchSingleToken(lexer, "variable", XQueryTokenType.K_VARIABLE)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, ":=", XQueryTokenType.ASSIGN_EQUAL)
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (26) FunctionDecl")
    fun testFunctionDecl_Scripting10() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL)

        matchSingleToken(lexer, "sequential", XQueryTokenType.K_SEQUENTIAL)
        matchSingleToken(lexer, "simple", XQueryTokenType.K_SIMPLE)
        matchSingleToken(lexer, "updating", XQueryTokenType.K_UPDATING)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (32) ApplyExpr")
    fun testApplyExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (33) ConcatExpr")
    fun testConcatExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (153) BlockExpr")
    fun testBlockExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "block", XQueryTokenType.K_BLOCK)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (154) Block")
    fun testBlock() {
        val lexer = createLexer()

        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (155) BlockDecls")
    fun testBlockDecls() {
        val lexer = createLexer()

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (156) BlockVarDecl")
    fun testBlockVarDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, ":=", XQueryTokenType.ASSIGN_EQUAL)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (158) AssignmentExpr")
    fun testAssignmentExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, ":=", XQueryTokenType.ASSIGN_EQUAL)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (159) ExitExpr")
    fun testExitExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "exit", XQueryTokenType.K_EXIT)
        matchSingleToken(lexer, "returning", XQueryTokenType.K_RETURNING)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (160) WhileExpr")
    fun testWhileExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "while", XQueryTokenType.K_WHILE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }
}
