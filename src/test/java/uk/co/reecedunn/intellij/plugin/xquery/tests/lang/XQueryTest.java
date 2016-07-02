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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang;

import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryTest extends TestCase {
    public void testID() {
        assertThat(XQuery.INSTANCE.getID(), is("XQuery"));
    }

    public void testBaseLanguage() {
        assertThat(XQuery.INSTANCE.getBaseLanguage(), is(nullValue()));
    }

    public void testMimeTypes() {
        assertThat(XQuery.INSTANCE.getMimeTypes().length, is(1));
        assertThat(XQuery.INSTANCE.getMimeTypes()[0], is("application/xquery"));
    }

    public void testCaseSensitivity() {
        assertThat(XQuery.INSTANCE.isCaseSensitive(), is(true));
    }
}
