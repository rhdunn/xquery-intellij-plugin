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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lexer;

import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryCodePointRange;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class XQueryCodePointRangeTest extends TestCase {
    public void testEmptyBuffer() {
        XQueryCodePointRange range = new XQueryCodePointRange();

        CharSequence sequence = "";
        range.start(sequence, 0, 0);
        assertThat(range.getBufferSequence(), is(sequence));
        assertThat(range.getBufferEnd(), is(0));
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(0));
    }

    public void testMatchingCodePoints() {
        XQueryCodePointRange range = new XQueryCodePointRange();

        range.start("a\u1255\uD392", 0, 3);
        assertThat(range.getBufferEnd(), is(3));

        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(0));
        assertThat(range.getCodePoint(), is((int)'a'));

        range.match();
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(1));
        assertThat(range.getCodePoint(), is(0x1255));

        range.match();
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(2));
        assertThat(range.getCodePoint(), is(0xD392));

        range.match();
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(3));
        assertThat(range.getCodePoint(), is(XQueryCodePointRange.END_OF_BUFFER));

        range.match();
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(3));
        assertThat(range.getCodePoint(), is(XQueryCodePointRange.END_OF_BUFFER));
    }

    public void testMatchingIncompleteSurrogatePairs() {
        XQueryCodePointRange range = new XQueryCodePointRange();

        range.start("\uD802\uD803", 0, 2);
        assertThat(range.getBufferEnd(), is(2));

        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(0));
        assertThat(range.getCodePoint(), is(0xD802));

        range.match();
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(1));
        assertThat(range.getCodePoint(), is(0xD803));

        range.match();
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(2));
        assertThat(range.getCodePoint(), is(XQueryCodePointRange.END_OF_BUFFER));
    }

    public void testMatchingSurrogatePairs() {
        XQueryCodePointRange range = new XQueryCodePointRange();

        range.start("\uD802\uDD07\uD802\uDDA3", 0, 4);
        assertThat(range.getBufferEnd(), is(4));

        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(0));
        assertThat(range.getCodePoint(), is(0x10907));

        range.match();
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(2));
        assertThat(range.getCodePoint(), is(0x109A3));

        range.match();
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(4));
        assertThat(range.getCodePoint(), is(XQueryCodePointRange.END_OF_BUFFER));

        range.match();
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(4));
        assertThat(range.getCodePoint(), is(XQueryCodePointRange.END_OF_BUFFER));

        range.match();
        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(4));
        assertThat(range.getCodePoint(), is(XQueryCodePointRange.END_OF_BUFFER));
    }

    public void testFlush() {
        XQueryCodePointRange range = new XQueryCodePointRange();

        range.start("abcd", 0, 4);
        range.match();
        range.match();

        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(2));
        assertThat(range.getBufferEnd(), is(4));

        range.flush();

        assertThat(range.getStart(), is(2));
        assertThat(range.getEnd(), is(2));
        assertThat(range.getBufferEnd(), is(4));
    }

    public void testSaveRestore() {
        XQueryCodePointRange range = new XQueryCodePointRange();

        range.start("abcd", 0, 4);
        range.match();

        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(1));
        assertThat(range.getBufferEnd(), is(4));

        range.save();

        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(1));
        assertThat(range.getBufferEnd(), is(4));

        range.match();

        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(2));
        assertThat(range.getBufferEnd(), is(4));

        range.restore();

        assertThat(range.getStart(), is(0));
        assertThat(range.getEnd(), is(1));
        assertThat(range.getBufferEnd(), is(4));
    }
}
