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
package uk.co.reecedunn.intellij.plugin.xquery.tests.findUsages;

import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.openapi.util.Pair;
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.findUsages.XQueryWordsScanner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryWordsScannerTest extends TestCase {
    // region Test Helpers

    private List<Pair<WordOccurrence.Kind, CharSequence>> scanWords(CharSequence text) {
        WordsScanner scanner = new XQueryWordsScanner();
        WordOccurrences occurrences = new WordOccurrences();
        scanner.processWords(text, occurrences);
        return occurrences.getWordOccurrrences();
    }

    private void match(Pair<WordOccurrence.Kind, CharSequence> occurrence, WordOccurrence.Kind kind, CharSequence text) {
        assertThat(occurrence.getFirst(), is(kind));
        assertThat(occurrence.getSecond(), is(text));
    }

    // endregion
    // region Code :: IntegerLiteral

    public void testIntegerLiteral() {
        final String testCase = "1234 56789";
        List<Pair<WordOccurrence.Kind, CharSequence>> occurrences = scanWords(testCase);
        assertThat(occurrences.size(), is(2));
        match(occurrences.get(0), WordOccurrence.Kind.CODE, "1234");
        match(occurrences.get(1), WordOccurrence.Kind.CODE, "56789");
    }

    // endregion
    // region Code :: DecimalLiteral

    public void testDecimalLiteral() {
        final String testCase = "1.25 2.4";
        List<Pair<WordOccurrence.Kind, CharSequence>> occurrences = scanWords(testCase);
        assertThat(occurrences.size(), is(4));
        match(occurrences.get(0), WordOccurrence.Kind.CODE, "1");
        match(occurrences.get(1), WordOccurrence.Kind.CODE, "25");
        match(occurrences.get(2), WordOccurrence.Kind.CODE, "2");
        match(occurrences.get(3), WordOccurrence.Kind.CODE, "4");
    }

    // endregion
    // region Code :: DoubleLiteral

    public void testDoubleLiteral() {
        final String testCase = "3e5 2e8";
        List<Pair<WordOccurrence.Kind, CharSequence>> occurrences = scanWords(testCase);
        assertThat(occurrences.size(), is(2));
        match(occurrences.get(0), WordOccurrence.Kind.CODE, "3e5");
        match(occurrences.get(1), WordOccurrence.Kind.CODE, "2e8");
    }

    // endregion
    // region Code :: Keywords

    public void testKeywords() {
        final String testCase = "for $item in $nodes return $item";
        List<Pair<WordOccurrence.Kind, CharSequence>> occurrences = scanWords(testCase);
        assertThat(occurrences.size(), is(9));
        match(occurrences.get(0), WordOccurrence.Kind.CODE, "for");
        match(occurrences.get(1), WordOccurrence.Kind.CODE, "$");
        match(occurrences.get(2), WordOccurrence.Kind.CODE, "item");
        match(occurrences.get(3), WordOccurrence.Kind.CODE, "in");
        match(occurrences.get(4), WordOccurrence.Kind.CODE, "$");
        match(occurrences.get(5), WordOccurrence.Kind.CODE, "nodes");
        match(occurrences.get(6), WordOccurrence.Kind.CODE, "return");
        match(occurrences.get(7), WordOccurrence.Kind.CODE, "$");
        match(occurrences.get(8), WordOccurrence.Kind.CODE, "item");
    }

    // endregion
    // region Code :: NCName

    public void testNCName() {
        final String testCase = "Lorem ipsum dolor";
        List<Pair<WordOccurrence.Kind, CharSequence>> occurrences = scanWords(testCase);
        assertThat(occurrences.size(), is(3));
        match(occurrences.get(0), WordOccurrence.Kind.CODE, "Lorem");
        match(occurrences.get(1), WordOccurrence.Kind.CODE, "ipsum");
        match(occurrences.get(2), WordOccurrence.Kind.CODE, "dolor");
    }

    // endregion
    // region Comments :: XQuery Comment

    public void testXQueryComment() {
        final String testCase = "(: Lorem ipsum dolor :)";
        List<Pair<WordOccurrence.Kind, CharSequence>> occurrences = scanWords(testCase);
        assertThat(occurrences.size(), is(3));
        match(occurrences.get(0), WordOccurrence.Kind.COMMENTS, "Lorem");
        match(occurrences.get(1), WordOccurrence.Kind.COMMENTS, "ipsum");
        match(occurrences.get(2), WordOccurrence.Kind.COMMENTS, "dolor");
    }

    // endregion
    // region Comments :: XML Comment

    public void testXmlComment() {
        final String testCase = "<!-- Lorem ipsum dolor -->";
        List<Pair<WordOccurrence.Kind, CharSequence>> occurrences = scanWords(testCase);
        assertThat(occurrences.size(), is(3));
        match(occurrences.get(0), WordOccurrence.Kind.COMMENTS, "Lorem");
        match(occurrences.get(1), WordOccurrence.Kind.COMMENTS, "ipsum");
        match(occurrences.get(2), WordOccurrence.Kind.COMMENTS, "dolor");
    }

    // endregion
    // region Literals :: StringLiteral

    public void testStringLiteral() {
        final String testCase = "\"Lorem ipsum dolor\"";
        List<Pair<WordOccurrence.Kind, CharSequence>> occurrences = scanWords(testCase);
        assertThat(occurrences.size(), is(3));
        match(occurrences.get(0), WordOccurrence.Kind.LITERALS, "Lorem");
        match(occurrences.get(1), WordOccurrence.Kind.LITERALS, "ipsum");
        match(occurrences.get(2), WordOccurrence.Kind.LITERALS, "dolor");
    }

    // endregion
    // region Literals :: DirAttributeValue

    public void testDirAttributeValue() {
        final String testCase = "<test value=\"Lorem ipsum dolor\"/>";
        List<Pair<WordOccurrence.Kind, CharSequence>> occurrences = scanWords(testCase);
        assertThat(occurrences.size(), is(5));
        match(occurrences.get(0), WordOccurrence.Kind.CODE, "test");
        match(occurrences.get(1), WordOccurrence.Kind.CODE, "value");
        match(occurrences.get(2), WordOccurrence.Kind.LITERALS, "Lorem");
        match(occurrences.get(3), WordOccurrence.Kind.LITERALS, "ipsum");
        match(occurrences.get(4), WordOccurrence.Kind.LITERALS, "dolor");
    }

    // endregion
}
