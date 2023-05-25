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
package uk.co.reecedunn.intellij.plugin.xpath.tests.lang.refactoring

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xpath.lang.refactoring.XPathNamesValidator

@DisplayName("IntelliJ - Custom Language Support - Rename Refactoring - Names Validator")
class XPathNamesValidatorTest {
    private val validator = XPathNamesValidator()

    @Test
    @DisplayName("empty")
    fun empty() {
        assertThat(validator.isKeyword("", null), `is`(false))
        assertThat(validator.isIdentifier("", null), `is`(false))
    }

    @Test
    @DisplayName("keyword")
    fun keyword() {
        assertThat(validator.isKeyword("for", null), `is`(false)) // Is a valid identifier name.
        assertThat(validator.isIdentifier("for", null), `is`(true))
    }

    @Test
    @DisplayName("NCName")
    fun ncname() {
        assertThat(validator.isKeyword("abc2def.GHI-jkl\u0330mno", null), `is`(false))
        assertThat(validator.isIdentifier("abc2def.GHI-jkl\u0330mno", null), `is`(true))
    }

    @Test
    @DisplayName("whitespace before")
    fun whitespaceBefore() {
        assertThat(validator.isKeyword(" \t  test", null), `is`(false))
        assertThat(validator.isIdentifier(" \t  test", null), `is`(true))
    }

    @Test
    @DisplayName("whitespace after")
    fun whitespaceAfter() {
        assertThat(validator.isKeyword("test \t  ", null), `is`(false))
        assertThat(validator.isIdentifier("test \t  ", null), `is`(true))
    }

    @Test
    @DisplayName("invalid NCName start character")
    fun invalidNCNameStartChar() {
        assertThat(validator.isKeyword("123test", null), `is`(false))
        assertThat(validator.isIdentifier("123test", null), `is`(false))
    }

    @Test
    @DisplayName("number (not an identifier)")
    fun number() {
        assertThat(validator.isKeyword("123", null), `is`(false))
        assertThat(validator.isIdentifier("123", null), `is`(false))
    }

    @Test
    @DisplayName("VarRef")
    fun varRef() {
        assertThat(validator.isKeyword("\$test", null), `is`(false))
        assertThat(validator.isIdentifier("\$test", null), `is`(false))
    }

    @Test
    @DisplayName("multiple tokens")
    fun multipleTokens() {
        assertThat(validator.isKeyword("lorem ipsum", null), `is`(false))
        assertThat(validator.isIdentifier("lorem ipsum", null), `is`(false))
    }
}
