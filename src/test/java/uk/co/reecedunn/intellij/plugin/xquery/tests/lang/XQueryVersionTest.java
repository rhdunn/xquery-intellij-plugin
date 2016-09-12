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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryVersionTest extends TestCase {
    public void testToString() {
        assertThat(XQueryVersion.VERSION_0_9_MARKLOGIC.toString(), is("0.9-ml"));
        assertThat(XQueryVersion.VERSION_1_0.toString(), is("1.0"));
        assertThat(XQueryVersion.VERSION_1_0_MARKLOGIC.toString(), is("1.0-ml"));
        assertThat(XQueryVersion.VERSION_3_0.toString(), is("3.0"));
        assertThat(XQueryVersion.VERSION_3_1.toString(), is("3.1"));
    }

    public void testParse() {
        assertThat(XQueryVersion.parse(null), is(nullValue()));

        assertThat(XQueryVersion.parse("0.9-ml"), is(XQueryVersion.VERSION_0_9_MARKLOGIC));
        assertThat(XQueryVersion.parse("1.0"), is(XQueryVersion.VERSION_1_0));
        assertThat(XQueryVersion.parse("1.0-ml"), is(XQueryVersion.VERSION_1_0_MARKLOGIC));
        assertThat(XQueryVersion.parse("3.0"), is(XQueryVersion.VERSION_3_0));
        assertThat(XQueryVersion.parse("3.1"), is(XQueryVersion.VERSION_3_1));

        assertThat(XQueryVersion.parse("0.9"), is(nullValue()));
        assertThat(XQueryVersion.parse("1.0-und"), is(nullValue()));
        assertThat(XQueryVersion.parse("1.1"), is(nullValue()));
        assertThat(XQueryVersion.parse("2.0"), is(nullValue()));
    }
}
