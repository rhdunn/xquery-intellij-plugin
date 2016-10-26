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
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.findUsages.XQueryWordsScanner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryWordsScannerTest extends TestCase {
    // region Test Helpers

    private List<WordOccurrence> scanWords(CharSequence text) {
        WordsScanner scanner = new XQueryWordsScanner();
        WordOccurrences occurrences = new WordOccurrences();
        scanner.processWords(text, occurrences);
        return occurrences.getWordOccurrrences();
    }

    private void match(WordOccurrence occurrence, WordOccurrence.Kind kind, CharSequence text, int start, int end) {
        assertThat(occurrence.getKind(), is(kind));
        assertThat(occurrence.getBaseText(), is(text));
        assertThat(occurrence.getStart(), is(start));
        assertThat(occurrence.getEnd(), is(end));
    }

    // endregion
    // region IntegerLiteral

    public void testIntegerLiteral() {
        final String testCase = "1234";
        List<WordOccurrence> occurrences = scanWords(testCase);
        assertThat(occurrences.size(), is(1));
        match(occurrences.get(0), WordOccurrence.Kind.CODE, testCase, 0, 4);
    }

    // endregion
}
