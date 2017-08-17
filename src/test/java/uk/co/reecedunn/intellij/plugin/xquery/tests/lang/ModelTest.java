/*
 * Copyright (C) 2017 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xquery.lang.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModelTest extends TestCase {
    // region XQuery Conformance / Optional Features

    public void testSaxonProduct_HE_OptionalFeatureSupport() {
        final Product product = Saxon.INSTANCE.getProducts().get(0);
        assertThat(product.getId(), is("HE"));
        assertThat(product.getImplementation().getId(), is("saxon"));

        for (Version version : Saxon.INSTANCE.getVersions()) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), is(true));
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), is(false));
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), is(false)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), is(false)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), is(false));
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), is(false));
        }
    }

    public void testSaxonProduct_PE_OptionalFeatureSupport() {
        final Product product = Saxon.INSTANCE.getProducts().get(1);
        assertThat(product.getId(), is("PE"));
        assertThat(product.getImplementation().getId(), is("saxon"));

        for (Version version : Saxon.INSTANCE.getVersions()) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), is(true));
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), is(true));
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), is(false)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), is(false)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), is(false));
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), is(false));
        }
    }

    public void testSaxonProduct_EE_OptionalFeatureSupport() {
        final Product product = Saxon.INSTANCE.getProducts().get(2);
        assertThat(product.getId(), is("EE"));
        assertThat(product.getImplementation().getId(), is("saxon"));

        for (Version version : Saxon.INSTANCE.getVersions()) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), is(true));
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), is(true));
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), is(true)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), is(true)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), is(true));
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), is(false));
        }
    }

    public void testSaxonProduct_EE_T_OptionalFeatureSupport() {
        final Product product = Saxon.INSTANCE.getProducts().get(3);
        assertThat(product.getId(), is("EE-T"));
        assertThat(product.getImplementation().getId(), is("saxon"));

        for (Version version : Saxon.INSTANCE.getVersions()) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), is(true));
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), is(true));
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), is(false)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), is(false)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), is(false));
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), is(false));
        }
    }

    public void testSaxonProduct_EE_Q_OptionalFeatureSupport() {
        final Product product = Saxon.INSTANCE.getProducts().get(4);
        assertThat(product.getId(), is("EE-Q"));
        assertThat(product.getImplementation().getId(), is("saxon"));

        for (Version version : Saxon.INSTANCE.getVersions()) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), is(true));
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), is(true));
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), is(true)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), is(true)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), is(true));
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), is(false));
        }
    }

    public void testSaxonProduct_EE_V_OptionalFeatureSupport() {
        final Product product = Saxon.INSTANCE.getProducts().get(5);
        assertThat(product.getId(), is("EE-V"));
        assertThat(product.getImplementation().getId(), is("saxon"));

        for (Version version : Saxon.INSTANCE.getVersions()) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), is(true));
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), is(true));
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), is(false)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), is(false)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), is(false));
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), is(false));
        }
    }

    public void testW3CProduct_REC_OptionalFeatureSupport() {
        final Product product = W3C.INSTANCE.getProducts().get(0);
        assertThat(product.getId(), is("rec"));
        assertThat(product.getImplementation().getId(), is("w3c"));

        for (Version version : Saxon.INSTANCE.getVersions()) {
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), is(true)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), is(true)); // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), is(true));
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), is(true));
        }
    }

    // endregion
}
