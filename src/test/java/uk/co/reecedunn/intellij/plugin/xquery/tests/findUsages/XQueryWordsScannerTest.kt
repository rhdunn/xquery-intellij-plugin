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
package uk.co.reecedunn.intellij.plugin.xquery.tests.findUsages

import com.intellij.lang.cacheBuilder.WordOccurrence
import com.intellij.openapi.util.Pair
import junit.framework.TestCase
import uk.co.reecedunn.intellij.plugin.xquery.findUsages.XQueryWordsScanner

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

class XQueryWordsScannerTest : TestCase() {
    // region Test Helpers

    private fun scanWords(text: CharSequence): List<Pair<WordOccurrence.Kind, CharSequence>> {
        val scanner = XQueryWordsScanner()
        val occurrences = WordOccurrences()
        scanner.processWords(text, occurrences)
        return occurrences.wordOccurrrences
    }

    private fun match(occurrence: Pair<WordOccurrence.Kind, CharSequence>, kind: WordOccurrence.Kind, text: CharSequence) {
        assertThat(occurrence.getFirst(), `is`(kind))
        assertThat(occurrence.getSecond(), `is`(text))
    }

    // endregion
    // region Code :: IntegerLiteral

    fun testIntegerLiteral() {
        val testCase = "1234 56789"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(2))
        match(occurrences[0], WordOccurrence.Kind.CODE, "1234")
        match(occurrences[1], WordOccurrence.Kind.CODE, "56789")
    }

    // endregion
    // region Code :: DecimalLiteral

    fun testDecimalLiteral() {
        val testCase = "1.25 2.4"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(2))
        match(occurrences[0], WordOccurrence.Kind.CODE, "1.25")
        match(occurrences[1], WordOccurrence.Kind.CODE, "2.4")
    }

    // endregion
    // region Code :: DoubleLiteral

    fun testDoubleLiteral() {
        val testCase = "3e5 2e8"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(2))
        match(occurrences[0], WordOccurrence.Kind.CODE, "3e5")
        match(occurrences[1], WordOccurrence.Kind.CODE, "2e8")
    }

    // endregion
    // region Code :: Keywords

    fun testKeywords() {
        val testCase = "for \$item in \$nodes return \$item"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(6))
        match(occurrences[0], WordOccurrence.Kind.CODE, "for")
        match(occurrences[1], WordOccurrence.Kind.CODE, "item")
        match(occurrences[2], WordOccurrence.Kind.CODE, "in")
        match(occurrences[3], WordOccurrence.Kind.CODE, "nodes")
        match(occurrences[4], WordOccurrence.Kind.CODE, "return")
        match(occurrences[5], WordOccurrence.Kind.CODE, "item")
    }

    // endregion
    // region Code :: NCName

    fun testNCName() {
        val testCase = "Lorem ipsum dolor"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(3))
        match(occurrences[0], WordOccurrence.Kind.CODE, "Lorem")
        match(occurrences[1], WordOccurrence.Kind.CODE, "ipsum")
        match(occurrences[2], WordOccurrence.Kind.CODE, "dolor")
    }

    fun testNCName_SpecialCharacters() {
        val testCase = "a2b a-b a.b a\u00B7b"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(4))
        match(occurrences[0], WordOccurrence.Kind.CODE, "a2b")
        match(occurrences[1], WordOccurrence.Kind.CODE, "a-b")
        match(occurrences[2], WordOccurrence.Kind.CODE, "a.b")
        match(occurrences[3], WordOccurrence.Kind.CODE, "a\u00B7b")
    }

    fun testNCName_SpecialCharactersAtStart() {
        val testCase = "2ab -ab .ab \u00B7ab"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(5))
        match(occurrences[0], WordOccurrence.Kind.CODE, "2")
        match(occurrences[1], WordOccurrence.Kind.CODE, "ab")
        match(occurrences[2], WordOccurrence.Kind.CODE, "ab")
        match(occurrences[3], WordOccurrence.Kind.CODE, "ab")
        match(occurrences[4], WordOccurrence.Kind.CODE, "ab")
    }

    // endregion
    // region Comments :: XQuery Comment

    fun testXQueryComment() {
        val testCase = "(: Lorem ipsum dolor :)"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(3))
        match(occurrences[0], WordOccurrence.Kind.COMMENTS, "Lorem")
        match(occurrences[1], WordOccurrence.Kind.COMMENTS, "ipsum")
        match(occurrences[2], WordOccurrence.Kind.COMMENTS, "dolor")
    }

    // endregion
    // region Comments :: XML Comment

    fun testXmlComment() {
        val testCase = "<!-- Lorem ipsum dolor -->"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(3))
        match(occurrences[0], WordOccurrence.Kind.COMMENTS, "Lorem")
        match(occurrences[1], WordOccurrence.Kind.COMMENTS, "ipsum")
        match(occurrences[2], WordOccurrence.Kind.COMMENTS, "dolor")
    }

    // endregion
    // region Literals :: StringLiteral

    fun testStringLiteral() {
        val testCase = "\"Lorem ipsum dolor\""
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(3))
        match(occurrences[0], WordOccurrence.Kind.LITERALS, "Lorem")
        match(occurrences[1], WordOccurrence.Kind.LITERALS, "ipsum")
        match(occurrences[2], WordOccurrence.Kind.LITERALS, "dolor")
    }

    // endregion
    // region Literals :: DirAttributeValue

    fun testDirAttributeValue() {
        val testCase = "<test value=\"Lorem ipsum dolor\"/>"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(5))
        match(occurrences[0], WordOccurrence.Kind.CODE, "test")
        match(occurrences[1], WordOccurrence.Kind.CODE, "value")
        match(occurrences[2], WordOccurrence.Kind.LITERALS, "Lorem")
        match(occurrences[3], WordOccurrence.Kind.LITERALS, "ipsum")
        match(occurrences[4], WordOccurrence.Kind.LITERALS, "dolor")
    }

    // endregion
    // region Literals :: DirElemContent

    fun testDirElemContent() {
        val testCase = "<test>Lorem ipsum dolor</test>"
        val occurrences = scanWords(testCase)
        assertThat(occurrences.size, `is`(5))
        match(occurrences[0], WordOccurrence.Kind.CODE, "test")
        match(occurrences[1], WordOccurrence.Kind.LITERALS, "Lorem")
        match(occurrences[2], WordOccurrence.Kind.LITERALS, "ipsum")
        match(occurrences[3], WordOccurrence.Kind.LITERALS, "dolor")
        match(occurrences[4], WordOccurrence.Kind.CODE, "test")
    }

    // endregion
}
