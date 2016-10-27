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
package uk.co.reecedunn.intellij.plugin.xquery.tests.filetypes;

import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.filetypes.ByteSequence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
    }
}
