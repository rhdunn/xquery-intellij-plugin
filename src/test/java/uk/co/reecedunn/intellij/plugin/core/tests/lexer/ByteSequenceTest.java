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
package uk.co.reecedunn.intellij.plugin.core.tests.lexer;

import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.core.lexer.ByteSequence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;

@SuppressWarnings("SameParameterValue")
public class ByteSequenceTest extends TestCase {
    public void testConstruction() {
        byte[] data = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39 };
        CharSequence b = new ByteSequence(data);

        assertThat(b.length(), is(10));
        assertThat(b.toString(), is("0123456789"));

        assertThat(b.charAt(0), is('0'));
        assertThat(b.charAt(9), is('9'));
    }

    public void testSubSequence() {
        byte[] data = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39 };
        CharSequence b = new ByteSequence(data);
        CharSequence c = b.subSequence(2, 8);

        assertThat(c.length(), is(6));
        assertThat(c.toString(), is("234567"));

        assertThat(c.charAt(0), is('2'));
        assertThat(c.charAt(5), is('7'));

        assertThat(b.subSequence(10, 10).length(), is(0));
        assertThat(b.subSequence(10, 10).toString(), is(""));
    }

    public void testSubSubSequence() {
        byte[] data = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39 };
        CharSequence b = new ByteSequence(data);
        CharSequence c = b.subSequence(2, 8);
        CharSequence d = c.subSequence(2, 5);

        assertThat(d.length(), is(3));
        assertThat(d.toString(), is("456"));

        assertThat(d.charAt(0), is('4'));
        assertThat(d.charAt(2), is('6'));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testSubSequence_OutOfBounds() {
        byte[] data = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39 };
        CharSequence b = new ByteSequence(data);

        IndexOutOfBoundsException e1 = expectThrows(IndexOutOfBoundsException.class, () -> b.subSequence(-1, 0));
        assertThat(e1.getMessage(), is("-1"));

        IndexOutOfBoundsException e2 = expectThrows(IndexOutOfBoundsException.class, () -> b.subSequence(11, 11));
        assertThat(e2.getMessage(), is("11"));

        IndexOutOfBoundsException e3 = expectThrows(IndexOutOfBoundsException.class, () -> b.subSequence(0, 11));
        assertThat(e3.getMessage(), is("11"));

        IndexOutOfBoundsException e5 = expectThrows(IndexOutOfBoundsException.class, () -> b.subSequence(1, 11));
        assertThat(e5.getMessage(), is("11"));

        IndexOutOfBoundsException e4 = expectThrows(IndexOutOfBoundsException.class, () -> b.subSequence(6, 4));
        assertThat(e4.getMessage(), is("-2"));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testCharAt_OutOfBounds() {
        byte[] data = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39 };
        CharSequence b = new ByteSequence(data);

        IndexOutOfBoundsException e1 = expectThrows(IndexOutOfBoundsException.class, () -> b.charAt(-1));
        assertThat(e1.getMessage(), is("-1"));

        IndexOutOfBoundsException e2 = expectThrows(IndexOutOfBoundsException.class, () -> b.charAt(10));
        assertThat(e2.getMessage(), is("10"));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testCharAt_SubSequence_OutOfBounds() {
        byte[] data = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39 };
        CharSequence b = new ByteSequence(data);
        CharSequence c = b.subSequence(2, 8);

        IndexOutOfBoundsException e1 = expectThrows(IndexOutOfBoundsException.class, () -> c.charAt(-1));
        assertThat(e1.getMessage(), is("-1"));

        IndexOutOfBoundsException e2 = expectThrows(IndexOutOfBoundsException.class, () -> c.charAt(6));
        assertThat(e2.getMessage(), is("6"));
    }
}
