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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryFeature;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryFeatureTest extends TestCase {
    public void testName() {
        assertThat(XQueryFeature.MINIMAL_CONFORMANCE.getName(), is("Minimal Conformance"));

        assertThat(XQueryFeature.SCHEMA_IMPORT.getName(), is("Schema Import Feature"));
        assertThat(XQueryFeature.SCHEMA_VALIDATION.getName(), is("Schema Validation Feature"));
        assertThat(XQueryFeature.STATIC_TYPING.getName(), is("Static Typing Feature"));
        assertThat(XQueryFeature.FULL_AXIS.getName(), is("Full Axis Feature"));
        assertThat(XQueryFeature.MODULE.getName(), is("Module Feature"));
        assertThat(XQueryFeature.SERIALIZATION.getName(), is("Serialization Feature"));

        assertThat(XQueryFeature.SCHEMA_AWARE.getName(), is("Schema Aware Feature"));
        assertThat(XQueryFeature.TYPED_DATA.getName(), is("Typed Data Feature"));
        assertThat(XQueryFeature.HIGHER_ORDER_FUNCTION.getName(), is("Higher Order Function Feature"));

        assertThat(XQueryFeature.FN_PUT.getName(), is("fn:put Feature"));
    }

    public void testToString() {
        assertThat(XQueryFeature.MINIMAL_CONFORMANCE.toString(), is("minimal-conformance"));

        assertThat(XQueryFeature.SCHEMA_IMPORT.toString(), is("schema-import"));
        assertThat(XQueryFeature.SCHEMA_VALIDATION.toString(), is("schema-validation"));
        assertThat(XQueryFeature.STATIC_TYPING.toString(), is("static-typing"));
        assertThat(XQueryFeature.FULL_AXIS.toString(), is("full-axis"));
        assertThat(XQueryFeature.MODULE.toString(), is("module"));
        assertThat(XQueryFeature.SERIALIZATION.toString(), is("serialization"));

        assertThat(XQueryFeature.SCHEMA_AWARE.toString(), is("schema-aware"));
        assertThat(XQueryFeature.TYPED_DATA.toString(), is("typed-data"));
        assertThat(XQueryFeature.HIGHER_ORDER_FUNCTION.toString(), is("higher-order-function"));

        assertThat(XQueryFeature.FN_PUT.toString(), is("fn:put"));
    }

    public void testParse() {
        assertThat(XQueryFeature.parse(null), is(nullValue()));

        assertThat(XQueryFeature.parse("minimal-conformance"), is(XQueryFeature.MINIMAL_CONFORMANCE));

        assertThat(XQueryFeature.parse("schema-import"), is(XQueryFeature.SCHEMA_IMPORT));
        assertThat(XQueryFeature.parse("schema-validation"), is(XQueryFeature.SCHEMA_VALIDATION));
        assertThat(XQueryFeature.parse("static-typing"), is(XQueryFeature.STATIC_TYPING));
        assertThat(XQueryFeature.parse("full-axis"), is(XQueryFeature.FULL_AXIS));
        assertThat(XQueryFeature.parse("module"), is(XQueryFeature.MODULE));
        assertThat(XQueryFeature.parse("serialization"), is(XQueryFeature.SERIALIZATION));

        assertThat(XQueryFeature.parse("schema-aware"), is(XQueryFeature.SCHEMA_AWARE));
        assertThat(XQueryFeature.parse("typed-data"), is(XQueryFeature.TYPED_DATA));
        assertThat(XQueryFeature.parse("higher-order-function"), is(XQueryFeature.HIGHER_ORDER_FUNCTION));

        assertThat(XQueryFeature.parse("fn:put"), is(XQueryFeature.FN_PUT));

        assertThat(XQueryFeature.parse("0.9"), is(nullValue()));
        assertThat(XQueryFeature.parse("1.0"), is(nullValue()));
        assertThat(XQueryFeature.parse("1.0-und"), is(nullValue()));
        assertThat(XQueryFeature.parse("1.1"), is(nullValue()));
        assertThat(XQueryFeature.parse("2.0"), is(nullValue()));
    }
}
