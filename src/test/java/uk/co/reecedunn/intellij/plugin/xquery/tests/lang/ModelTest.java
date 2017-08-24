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

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModelTest extends TestCase {
    // region Version :: Display Name (toString)

    public void testSpecification_DisplayName() {
        assertThat(XmlSchema.INSTANCE.getREC_1_0_20041028().toString(), is("XML Schema Definition 1.0"));
        assertThat(XmlSchema.INSTANCE.getREC_1_1_20120405().toString(), is("XML Schema Definition 1.1"));

        assertThat(XQuery.INSTANCE.getREC_1_0_20070123().toString(), is("XQuery 1.0"));
        assertThat(XQuery.INSTANCE.getMARKLOGIC_0_9().toString(), is("XQuery 0.9-ml"));

        assertThat(FullText.INSTANCE.getREC_1_0_20110317().toString(), is("XQuery and XPath Full Text 1.0"));

        assertThat(FunctionsAndOperators.INSTANCE.getREC_1_0_20070123().toString(), is("XQuery and XPath Functions and Operators 1.0"));

        assertThat(Scripting.INSTANCE.getNOTE_1_0_20140918().toString(), is("XQuery Scripting Extension 1.0"));

        assertThat(UpdateFacility.INSTANCE.getREC_1_0_20110317().toString(), is("XQuery Update Facility 1.0"));
    }

    public void testProductVersion_DisplayName() {
        assertThat(BaseX.INSTANCE.getVERSION_8_5().toString(), is("BaseX 8.5"));

        assertThat(MarkLogic.INSTANCE.getVERSION_7_0().toString(), is("MarkLogic 7.0"));

        assertThat(Saxon.INSTANCE.getVERSION_9_7().toString(), is("Saxon 9.7"));
    }

    public void testNamedVersion_DisplayName() {
        assertThat(W3C.INSTANCE.getFIRST_EDITION().toString(), is("W3C First Edition"));
    }

    // endregion
    // region Versioned :: Supports Dialect

    public void testXmlSchema_SupportsDialect() {
        Versioned versioned = XmlSchema.INSTANCE;
        assertThat(versioned.supportsDialect(XmlSchema.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(XQuery.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FullText.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FunctionsAndOperators.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Scripting.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(UpdateFacility.INSTANCE), is(false));

        assertThat(versioned.supportsDialect(BaseX.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(MarkLogic.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Saxon.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(W3C.INSTANCE), is(false));
    }

    public void testXQuery_SupportsDialect() {
        Versioned versioned = XQuery.INSTANCE;
        assertThat(versioned.supportsDialect(XmlSchema.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(XQuery.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(FullText.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FunctionsAndOperators.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Scripting.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(UpdateFacility.INSTANCE), is(false));

        assertThat(versioned.supportsDialect(BaseX.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(MarkLogic.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Saxon.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(W3C.INSTANCE), is(false));
    }

    public void testFullText_SupportsDialect() {
        Versioned versioned = FullText.INSTANCE;
        assertThat(versioned.supportsDialect(XmlSchema.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(XQuery.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(FullText.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(FunctionsAndOperators.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Scripting.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(UpdateFacility.INSTANCE), is(false));

        assertThat(versioned.supportsDialect(BaseX.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(MarkLogic.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Saxon.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(W3C.INSTANCE), is(false));
    }

    public void testFunctionsAndOperators_SupportsDialect() {
        Versioned versioned = FunctionsAndOperators.INSTANCE;
        assertThat(versioned.supportsDialect(XmlSchema.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(XQuery.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FullText.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FunctionsAndOperators.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(Scripting.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(UpdateFacility.INSTANCE), is(false));

        assertThat(versioned.supportsDialect(BaseX.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(MarkLogic.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Saxon.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(W3C.INSTANCE), is(false));
    }

    public void testScripting_SupportsDialect() {
        Versioned versioned = Scripting.INSTANCE;
        assertThat(versioned.supportsDialect(XmlSchema.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(XQuery.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(FullText.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FunctionsAndOperators.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Scripting.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(UpdateFacility.INSTANCE), is(true));

        assertThat(versioned.supportsDialect(BaseX.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(MarkLogic.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Saxon.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(W3C.INSTANCE), is(false));
    }

    public void testUpdateFacility_SupportsDialect() {
        Versioned versioned = UpdateFacility.INSTANCE;
        assertThat(versioned.supportsDialect(XmlSchema.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(XQuery.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(FullText.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FunctionsAndOperators.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Scripting.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(UpdateFacility.INSTANCE), is(true));

        assertThat(versioned.supportsDialect(BaseX.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(MarkLogic.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Saxon.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(W3C.INSTANCE), is(false));
    }

    public void testBaseX_SupportsDialect() {
        Versioned versioned = BaseX.INSTANCE;
        assertThat(versioned.supportsDialect(XmlSchema.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(XQuery.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(FullText.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(FunctionsAndOperators.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Scripting.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(UpdateFacility.INSTANCE), is(true));

        assertThat(versioned.supportsDialect(BaseX.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(MarkLogic.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Saxon.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(W3C.INSTANCE), is(false));
    }

    public void testMarkLogic_SupportsDialect() {
        Versioned versioned = MarkLogic.INSTANCE;
        assertThat(versioned.supportsDialect(XmlSchema.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(XQuery.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(FullText.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FunctionsAndOperators.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Scripting.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(UpdateFacility.INSTANCE), is(false));

        assertThat(versioned.supportsDialect(BaseX.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(MarkLogic.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(Saxon.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(W3C.INSTANCE), is(false));
    }

    public void testSaxon_SupportsDialect() {
        Versioned versioned = Saxon.INSTANCE;
        assertThat(versioned.supportsDialect(XmlSchema.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(XQuery.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(FullText.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FunctionsAndOperators.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Scripting.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(UpdateFacility.INSTANCE), is(true));

        assertThat(versioned.supportsDialect(BaseX.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(MarkLogic.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Saxon.INSTANCE), is(true));
        assertThat(versioned.supportsDialect(W3C.INSTANCE), is(false));
    }

    public void testW3C_SupportsDialect() {
        Versioned versioned = W3C.INSTANCE;
        assertThat(versioned.supportsDialect(XmlSchema.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(XQuery.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FullText.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(FunctionsAndOperators.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Scripting.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(UpdateFacility.INSTANCE), is(false));

        assertThat(versioned.supportsDialect(BaseX.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(MarkLogic.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(Saxon.INSTANCE), is(false));
        assertThat(versioned.supportsDialect(W3C.INSTANCE), is(true));
    }

    // endregion
    // region Product :: XQuery Conformance (Minimal Conformance; Optional Features)

    public void testBaseXProduct_OptionalFeatureSupport() {
        final Product product = BaseX.INSTANCE.getProducts().get(0);
        assertThat(product.getId(), is("basex"));
        assertThat(product.getImplementation().getId(), is("basex"));

        for (Version version : BaseX.INSTANCE.getVersions()) {
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

    public void testMarkLogicProduct_OptionalFeatureSupport() {
        final Product product = MarkLogic.INSTANCE.getProducts().get(0);
        assertThat(product.getId(), is("marklogic"));
        assertThat(product.getImplementation().getId(), is("marklogic"));

        for (Version version : MarkLogic.INSTANCE.getVersions()) {
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

    public void testW3CProduct_SPECIFICATIONS_OptionalFeatureSupport() {
        final Product product = W3C.INSTANCE.getProducts().get(0);
        assertThat(product.getId(), is("spec"));
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
    // region Product :: Conforms To (Specification, Vendor Extension/Implementation)

    public void testBaseX_ConformsTo() {
        for (Product product : BaseX.INSTANCE.getProducts()) {
            for (Version version : BaseX.INSTANCE.getVersions()) {
                // region Specification: XML Schema Definition Language (XSD)

                assertThat(product.conformsTo(version, XmlSchema.INSTANCE.getREC_1_0_20041028()), is(true));
                assertThat(product.conformsTo(version, XmlSchema.INSTANCE.getREC_1_1_20120405()), is(true));

                // endregion
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20070123()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20101214()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_0_20140408()), is(true));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getCR_3_1_20151217()), is(version.getValue() <= 8.5));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_1_20170321()), is(version.getValue() >= 8.6));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_0_9()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_1_0()), is(false));

                // endregion
                // region Specification: XQuery and XPath Full Text

                assertThat(product.conformsTo(version, FullText.INSTANCE.getREC_1_0_20110317()), is(true));
                assertThat(product.conformsTo(version, FullText.INSTANCE.getREC_3_0_20151124()), is(true));

                // endregion
                // region Specification: XQuery and XPath Functions and Operators

                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_1_0_20070123()), is(false));
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_1_0_20101214()), is(false));
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_3_0_20140408()), is(true));
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_3_1_20170321()), is(version.getValue() >= 8.6));

                // endregion
                // region Specification: XQuery Scripting Extension

                assertThat(product.conformsTo(version, Scripting.INSTANCE.getNOTE_1_0_20140918()), is(false));

                // endregion
                // region Specification: XQuery Update Facility

                assertThat(product.conformsTo(version, UpdateFacility.INSTANCE.getREC_1_0_20110317()), is(true));
                assertThat(product.conformsTo(version, UpdateFacility.INSTANCE.getNOTE_3_0_20170124()), is(version.getValue() >= 8.5));

                // endregion
                // region Implementation: BaseX

                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_4()), is(version.getValue() >= 8.4));
                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_5()), is(version.getValue() >= 8.5));
                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_6()), is(version.getValue() >= 8.6));

                // endregion
                // region Implementation: MarkLogic

                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_6_0()), is(false));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_7_0()), is(false));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_8_0()), is(false));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_9_0()), is(false));

                // endregion
                // region Implementation: Saxon

                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_5()), is(false));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_6()), is(false));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_7()), is(false));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_8()), is(false));

                // endregion
                // region Implementation: W3C

                assertThat(product.conformsTo(version, W3C.INSTANCE.getFIRST_EDITION()), is(false));
                assertThat(product.conformsTo(version, W3C.INSTANCE.getSECOND_EDITION()), is(false));

                // endregion
            }
        }
    }

    public void testMarkLogic_ConformsTo() {
        for (Product product : MarkLogic.INSTANCE.getProducts()) {
            for (Version version : MarkLogic.INSTANCE.getVersions()) {
                // region Specification: XML Schema Definition Language (XSD)

                assertThat(product.conformsTo(version, XmlSchema.INSTANCE.getREC_1_0_20041028()), is(true));
                assertThat(product.conformsTo(version, XmlSchema.INSTANCE.getREC_1_1_20120405()), is(version.getValue() >= 9.0));

                // endregion
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20070123()), is(true));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20101214()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_0_20140408()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getCR_3_1_20151217()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_1_20170321()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_0_9()), is(true));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_1_0()), is(true));

                // endregion
                // region Specification: XQuery and XPath Full Text

                assertThat(product.conformsTo(version, FullText.INSTANCE.getREC_1_0_20110317()), is(false));
                assertThat(product.conformsTo(version, FullText.INSTANCE.getREC_3_0_20151124()), is(false));

                // endregion
                // region Specification: XQuery and XPath Functions and Operators

                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_1_0_20070123()), is(true));
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_1_0_20101214()), is(false));
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_3_0_20140408()), is(true));
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_3_1_20170321()), is(false));

                // endregion
                // region Specification: XQuery Scripting Extension

                assertThat(product.conformsTo(version, Scripting.INSTANCE.getNOTE_1_0_20140918()), is(false));

                // endregion
                // region Specification: XQuery Update Facility

                assertThat(product.conformsTo(version, UpdateFacility.INSTANCE.getREC_1_0_20110317()), is(false));
                assertThat(product.conformsTo(version, UpdateFacility.INSTANCE.getNOTE_3_0_20170124()), is(false));

                // endregion
                // region Implementation: BaseX

                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_4()), is(false));
                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_5()), is(false));
                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_6()), is(false));

                // endregion
                // region Implementation: MarkLogic

                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_6_0()), is(version.getValue() >= 6.0));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_7_0()), is(version.getValue() >= 7.0));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_8_0()), is(version.getValue() >= 8.0));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_9_0()), is(version.getValue() >= 9.0));

                // endregion
                // region Implementation: Saxon

                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_5()), is(false));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_6()), is(false));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_7()), is(false));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_8()), is(false));

                // endregion
                // region Implementation: W3C

                assertThat(product.conformsTo(version, W3C.INSTANCE.getFIRST_EDITION()), is(false));
                assertThat(product.conformsTo(version, W3C.INSTANCE.getSECOND_EDITION()), is(false));

                // endregion
            }
        }
    }

    public void testSaxon_ConformsTo() {
        for (Product product : Saxon.INSTANCE.getProducts()) {
            for (Version version : Saxon.INSTANCE.getVersions()) {
                // region Specification: XML Schema Definition Language (XSD)

                assertThat(product.conformsTo(version, XmlSchema.INSTANCE.getREC_1_0_20041028()), is(true));
                assertThat(product.conformsTo(version, XmlSchema.INSTANCE.getREC_1_1_20120405()), is(true));

                // endregion
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20070123()), is(true));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20101214()), is(false));
                if (product.getId().equals("HE")) {
                    assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_0_20140408()), is(version.getValue() >= 9.6));
                } else {
                    assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_0_20140408()), is(version.getValue() >= 9.5));
                }
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getCR_3_1_20151217()), is(version.getValue() == 9.7));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_1_20170321()), is(version.getValue() >= 9.8));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_0_9()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_1_0()), is(false));

                // endregion
                // region Specification: XQuery and XPath Full Text

                assertThat(product.conformsTo(version, FullText.INSTANCE.getREC_1_0_20110317()), is(false));
                assertThat(product.conformsTo(version, FullText.INSTANCE.getREC_3_0_20151124()), is(false));

                // endregion
                // region Specification: XQuery and XPath Functions and Operators

                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_1_0_20070123()), is(true));
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_1_0_20101214()), is(false));
                if (product.getId().equals("HE")) {
                    assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_3_0_20140408()), is(version.getValue() >= 9.6));
                } else {
                    assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_3_0_20140408()), is(version.getValue() >= 9.5));
                }
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_3_1_20170321()), is(version.getValue() >= 9.8));

                // endregion
                // region Specification: XQuery Scripting Extension

                assertThat(product.conformsTo(version, Scripting.INSTANCE.getNOTE_1_0_20140918()), is(false));

                // endregion
                // region Specification: XQuery Update Facility

                if (product.getId().equals("HE") || product.getId().equals("PE")) {
                    assertThat(product.conformsTo(version, UpdateFacility.INSTANCE.getREC_1_0_20110317()), is(false));
                } else {
                    assertThat(product.conformsTo(version, UpdateFacility.INSTANCE.getREC_1_0_20110317()), is(true));
                }
                assertThat(product.conformsTo(version, UpdateFacility.INSTANCE.getNOTE_3_0_20170124()), is(false));

                // endregion
                // region Implementation: BaseX

                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_4()), is(false));
                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_5()), is(false));
                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_6()), is(false));

                // endregion
                // region Implementation: MarkLogic

                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_6_0()), is(false));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_7_0()), is(false));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_8_0()), is(false));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_9_0()), is(false));

                // endregion
                // region Implementation: Saxon

                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_5()), is(version.getValue() >= 9.5));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_6()), is(version.getValue() >= 9.6));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_7()), is(version.getValue() >= 9.7));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_8()), is(version.getValue() >= 9.8));

                // endregion
                // region Implementation: W3C

                assertThat(product.conformsTo(version, W3C.INSTANCE.getFIRST_EDITION()), is(false));
                assertThat(product.conformsTo(version, W3C.INSTANCE.getSECOND_EDITION()), is(false));

                // endregion
            }
        }
    }

    public void testW3C_ConformsTo() {
        for (Product product : W3C.INSTANCE.getProducts()) {
            for (Version version : W3C.INSTANCE.getVersions()) {
                // region Specification: XML Schema Definition Language (XSD)

                assertThat(product.conformsTo(version, XmlSchema.INSTANCE.getREC_1_0_20041028()), is(true));
                assertThat(product.conformsTo(version, XmlSchema.INSTANCE.getREC_1_1_20120405()), is(true));

                // endregion
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20070123()), is(version.getValue() == 1.0));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20101214()), is(version.getValue() == 2.0));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_0_20140408()), is(version.getValue() == 1.0));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getCR_3_1_20151217()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_1_20170321()), is(version.getValue() == 1.0));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_0_9()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_1_0()), is(false));

                // endregion
                // region Specification: XQuery and XPath Full Text

                assertThat(product.conformsTo(version, FullText.INSTANCE.getREC_1_0_20110317()), is(version.getValue() == 1.0));
                assertThat(product.conformsTo(version, FullText.INSTANCE.getREC_3_0_20151124()), is(version.getValue() == 1.0));

                // endregion
                // region Specification: XQuery and XPath Functions and Operators

                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_1_0_20070123()), is(version.getValue() == 1.0));
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_1_0_20101214()), is(version.getValue() == 2.0));
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_3_0_20140408()), is(version.getValue() == 1.0));
                assertThat(product.conformsTo(version, FunctionsAndOperators.INSTANCE.getREC_3_1_20170321()), is(version.getValue() == 1.0));

                // endregion
                // region Specification: XQuery Scripting Extension

                assertThat(product.conformsTo(version, Scripting.INSTANCE.getNOTE_1_0_20140918()), is(version.getValue() == 1.0));

                // endregion
                // region Specification: XQuery Update Facility

                assertThat(product.conformsTo(version, UpdateFacility.INSTANCE.getREC_1_0_20110317()), is(version.getValue() == 1.0));
                assertThat(product.conformsTo(version, UpdateFacility.INSTANCE.getNOTE_3_0_20170124()), is(version.getValue() == 1.0));

                // endregion
                // region Implementation: BaseX

                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_4()), is(false));
                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_5()), is(false));
                assertThat(product.conformsTo(version, BaseX.INSTANCE.getVERSION_8_6()), is(false));

                // endregion
                // region Implementation: MarkLogic

                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_6_0()), is(false));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_7_0()), is(false));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_8_0()), is(false));
                assertThat(product.conformsTo(version, MarkLogic.INSTANCE.getVERSION_9_0()), is(false));

                // endregion
                // region Implementation: Saxon

                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_5()), is(false));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_6()), is(false));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_7()), is(false));
                assertThat(product.conformsTo(version, Saxon.INSTANCE.getVERSION_9_8()), is(false));

                // endregion
                // region Implementation: W3C

                assertThat(product.conformsTo(version, W3C.INSTANCE.getFIRST_EDITION()), is(false));
                assertThat(product.conformsTo(version, W3C.INSTANCE.getSECOND_EDITION()), is(false));

                // endregion
            }
        }
    }

    // endregion
    // region Product :: XQuery Versions

    public void testBaseX_XQueryVersions() {
        List<Version> xquery;
        for (Product product : BaseX.INSTANCE.getProducts()) {
            for (Version version : BaseX.INSTANCE.getVersions()) {
                xquery = XQuery.INSTANCE.versionsFor(product, version);
                assertThat(xquery.size(), is(2));
                assertThat(xquery.get(0), is(XQuery.INSTANCE.getREC_3_0_20140408()));
                if (version.getValue() <= 8.5) {
                    assertThat(xquery.get(1), is(XQuery.INSTANCE.getCR_3_1_20151217()));
                } else {
                    assertThat(xquery.get(1), is(XQuery.INSTANCE.getREC_3_1_20170321()));
                }
            }
        }
    }

    public void testMarkLogic_XQueryVersions() {
        List<Version> xquery;
        for (Product product : MarkLogic.INSTANCE.getProducts()) {
            for (Version version : MarkLogic.INSTANCE.getVersions()) {
                xquery = XQuery.INSTANCE.versionsFor(product, version);
                assertThat(xquery.size(), is(3));
                assertThat(xquery.get(0), is(XQuery.INSTANCE.getMARKLOGIC_0_9()));
                assertThat(xquery.get(1), is(XQuery.INSTANCE.getREC_1_0_20070123()));
                assertThat(xquery.get(2), is(XQuery.INSTANCE.getMARKLOGIC_1_0()));
            }
        }
    }

    public void testSaxon_XQueryVersions() {
        List<Version> xquery;
        for (Product product : Saxon.INSTANCE.getProducts()) {
            xquery = XQuery.INSTANCE.versionsFor(product, Saxon.INSTANCE.getVERSION_9_5());
            if (product.getId().equals("HE")) {
                assertThat(xquery.size(), is(1));
                assertThat(xquery.get(0), is(XQuery.INSTANCE.getREC_1_0_20070123()));
            } else {
                assertThat(xquery.size(), is(2));
                assertThat(xquery.get(0), is(XQuery.INSTANCE.getREC_1_0_20070123()));
                assertThat(xquery.get(1), is(XQuery.INSTANCE.getREC_3_0_20140408()));
            }

            xquery = XQuery.INSTANCE.versionsFor(product, Saxon.INSTANCE.getVERSION_9_6());
            assertThat(xquery.size(), is(2));
            assertThat(xquery.get(0), is(XQuery.INSTANCE.getREC_1_0_20070123()));
            assertThat(xquery.get(1), is(XQuery.INSTANCE.getREC_3_0_20140408()));

            xquery = XQuery.INSTANCE.versionsFor(product, Saxon.INSTANCE.getVERSION_9_7());
            assertThat(xquery.size(), is(3));
            assertThat(xquery.get(0), is(XQuery.INSTANCE.getREC_1_0_20070123()));
            assertThat(xquery.get(1), is(XQuery.INSTANCE.getREC_3_0_20140408()));
            assertThat(xquery.get(2), is(XQuery.INSTANCE.getCR_3_1_20151217()));

            xquery = XQuery.INSTANCE.versionsFor(product, Saxon.INSTANCE.getVERSION_9_8());
            assertThat(xquery.size(), is(3));
            assertThat(xquery.get(0), is(XQuery.INSTANCE.getREC_1_0_20070123()));
            assertThat(xquery.get(1), is(XQuery.INSTANCE.getREC_3_0_20140408()));
            assertThat(xquery.get(2), is(XQuery.INSTANCE.getREC_3_1_20170321()));
        }
    }

    public void testW3C_XQueryVersions() {
        List<Version> xquery;
        for (Product product : W3C.INSTANCE.getProducts()) {
            xquery = XQuery.INSTANCE.versionsFor(product, W3C.INSTANCE.getFIRST_EDITION());
            assertThat(xquery.size(), is(3));
            assertThat(xquery.get(0), is(XQuery.INSTANCE.getREC_1_0_20070123()));
            assertThat(xquery.get(1), is(XQuery.INSTANCE.getREC_3_0_20140408()));
            assertThat(xquery.get(2), is(XQuery.INSTANCE.getREC_3_1_20170321()));

            xquery = XQuery.INSTANCE.versionsFor(product, W3C.INSTANCE.getSECOND_EDITION());
            assertThat(xquery.size(), is(1));
            assertThat(xquery.get(0), is(XQuery.INSTANCE.getREC_1_0_20101214()));
        }
    }

    // endregion
    // region Product :: Flavours For XQuery Version

    public void testBaseX_FlavoursForXQueryVersion() {
        List<Versioned> flavours;
        for (Product product : BaseX.INSTANCE.getProducts()) {
            for (Version version : BaseX.INSTANCE.getVersions()) {
                flavours = product.flavoursForXQueryVersion(version, "1.0");
                assertThat(flavours.size(), is(0));

                flavours = product.flavoursForXQueryVersion(version, "3.0");
                assertThat(flavours.size(), is(4));
                assertThat(flavours.get(0), is(BaseX.INSTANCE));
                assertThat(flavours.get(1), is(XQuery.INSTANCE));
                assertThat(flavours.get(2), is(FullText.INSTANCE));
                assertThat(flavours.get(3), is(UpdateFacility.INSTANCE));

                flavours = product.flavoursForXQueryVersion(version, "3.1");
                assertThat(flavours.size(), is(4));
                assertThat(flavours.get(0), is(BaseX.INSTANCE));
                assertThat(flavours.get(1), is(XQuery.INSTANCE));
                assertThat(flavours.get(2), is(FullText.INSTANCE));
                assertThat(flavours.get(3), is(UpdateFacility.INSTANCE));

                flavours = product.flavoursForXQueryVersion(version, "0.9-ml");
                assertThat(flavours.size(), is(0));

                flavours = product.flavoursForXQueryVersion(version, "1.0-ml");
                assertThat(flavours.size(), is(0));
            }
        }
    }

    public void testMarkLogic_FlavoursForXQueryVersion() {
        List<Versioned> flavours;
        for (Product product : MarkLogic.INSTANCE.getProducts()) {
            for (Version version : MarkLogic.INSTANCE.getVersions()) {
                flavours = product.flavoursForXQueryVersion(version, "1.0");
                assertThat(flavours.size(), is(1));
                assertThat(flavours.get(0), is(XQuery.INSTANCE));

                flavours = product.flavoursForXQueryVersion(version, "3.0");
                assertThat(flavours.size(), is(0));

                flavours = product.flavoursForXQueryVersion(version, "3.1");
                assertThat(flavours.size(), is(0));

                flavours = product.flavoursForXQueryVersion(version, "0.9-ml");
                assertThat(flavours.size(), is(1));
                assertThat(flavours.get(0), is(MarkLogic.INSTANCE));

                flavours = product.flavoursForXQueryVersion(version, "1.0-ml");
                assertThat(flavours.size(), is(1));
                assertThat(flavours.get(0), is(MarkLogic.INSTANCE));
            }
        }
    }

    public void testSaxon_FlavoursForXQueryVersion() {
        List<Versioned> flavours;
        for (Product product : Saxon.INSTANCE.getProducts()) {
            for (Version version : Saxon.INSTANCE.getVersions()) {
                flavours = product.flavoursForXQueryVersion(version, "1.0");
                if (product.getId().equals("HE") || product.getId().equals("PE")) {
                    assertThat(flavours.size(), is(1));
                    assertThat(flavours.get(0), is(XQuery.INSTANCE));
                } else {
                    assertThat(flavours.size(), is(2));
                    assertThat(flavours.get(0), is(XQuery.INSTANCE));
                    assertThat(flavours.get(1), is(UpdateFacility.INSTANCE));
                }

                flavours = product.flavoursForXQueryVersion(version, "3.0");
                if (product.getId().equals("HE") || product.getId().equals("PE")) {
                    assertThat(flavours.size(), is(1));
                    assertThat(flavours.get(0), is(XQuery.INSTANCE));
                } else {
                    assertThat(flavours.size(), is(3));
                    assertThat(flavours.get(0), is(Saxon.INSTANCE));
                    assertThat(flavours.get(1), is(XQuery.INSTANCE));
                    assertThat(flavours.get(2), is(UpdateFacility.INSTANCE));
                }

                flavours = product.flavoursForXQueryVersion(version, "3.1");
                if (product.getId().equals("HE") || product.getId().equals("PE")) {
                    assertThat(flavours.size(), is(1));
                    assertThat(flavours.get(0), is(XQuery.INSTANCE));
                } else {
                    assertThat(flavours.size(), is(3));
                    assertThat(flavours.get(0), is(Saxon.INSTANCE));
                    assertThat(flavours.get(1), is(XQuery.INSTANCE));
                    assertThat(flavours.get(2), is(UpdateFacility.INSTANCE));
                }

                flavours = product.flavoursForXQueryVersion(version, "0.9-ml");
                assertThat(flavours.size(), is(0));

                flavours = product.flavoursForXQueryVersion(version, "1.0-ml");
                assertThat(flavours.size(), is(0));
            }
        }
    }

    public void testW3C_FlavoursForXQueryVersion() {
        List<Versioned> flavours;
        for (Product product : W3C.INSTANCE.getProducts()) {
            for (Version version : W3C.INSTANCE.getVersions()) {
                flavours = product.flavoursForXQueryVersion(version, "1.0");
                if (version.getValue() == 1.0) {
                    assertThat(flavours.size(), is(4));
                    assertThat(flavours.get(0), is(XQuery.INSTANCE));
                    assertThat(flavours.get(1), is(FullText.INSTANCE));
                    assertThat(flavours.get(2), is(UpdateFacility.INSTANCE));
                    assertThat(flavours.get(3), is(Scripting.INSTANCE));
                } else {
                    assertThat(flavours.size(), is(1));
                    assertThat(flavours.get(0), is(XQuery.INSTANCE));
                }

                flavours = product.flavoursForXQueryVersion(version, "3.0");
                if (version.getValue() == 1.0) {
                    assertThat(flavours.size(), is(3));
                    assertThat(flavours.get(0), is(XQuery.INSTANCE));
                    assertThat(flavours.get(1), is(FullText.INSTANCE));
                    assertThat(flavours.get(2), is(UpdateFacility.INSTANCE));
                } else {
                    assertThat(flavours.size(), is(1));
                    assertThat(flavours.get(0), is(XQuery.INSTANCE));
                }

                flavours = product.flavoursForXQueryVersion(version, "3.1");
                assertThat(flavours.size(), is(1));
                assertThat(flavours.get(0), is(XQuery.INSTANCE));

                flavours = product.flavoursForXQueryVersion(version, "0.9-ml");
                assertThat(flavours.size(), is(0));

                flavours = product.flavoursForXQueryVersion(version, "1.0-ml");
                assertThat(flavours.size(), is(0));
            }
        }
    }

    // endregion
    // region Item ID

    public void testItemId_VendorOnly() {
        ItemId id;

        id = new ItemId("basex");
        assertThat(id.getId(), is("basex"));
        assertThat(id.getVendor(), is(BaseX.INSTANCE));
        assertThat(id.getProduct(), is(nullValue()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("marklogic");
        assertThat(id.getId(), is("marklogic"));
        assertThat(id.getVendor(), is(MarkLogic.INSTANCE));
        assertThat(id.getProduct(), is(nullValue()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("saxon");
        assertThat(id.getId(), is("saxon"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(nullValue()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("w3c");
        assertThat(id.getId(), is("w3c"));
        assertThat(id.getVendor(), is(W3C.INSTANCE));
        assertThat(id.getProduct(), is(nullValue()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("loremipsum");
        assertThat(id.getId(), is("loremipsum"));
        assertThat(id.getVendor(), is(nullValue()));
        assertThat(id.getProduct(), is(nullValue()));
        assertThat(id.getVersion(), is(nullValue()));
    }

    public void testItemId_BaseX_Versions() {
        ItemId id;

        id = new ItemId("basex/v8.4");
        assertThat(id.getId(), is("basex/v8.4"));
        assertThat(id.getVendor(), is(BaseX.INSTANCE));
        assertThat(id.getProduct(), is(BaseX.INSTANCE.getBASEX()));
        assertThat(id.getVersion(), is(BaseX.INSTANCE.getVERSION_8_4()));

        id = new ItemId("basex/v0.5");
        assertThat(id.getId(), is("basex/v0.5"));
        assertThat(id.getVendor(), is(BaseX.INSTANCE));
        assertThat(id.getProduct(), is(BaseX.INSTANCE.getBASEX()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("basex/8.4");
        assertThat(id.getId(), is("basex/8.4"));
        assertThat(id.getVendor(), is(BaseX.INSTANCE));
        assertThat(id.getProduct(), is(nullValue()));
        assertThat(id.getVersion(), is(nullValue()));
    }

    public void testItemId_MarkLogic_Versions() {
        ItemId id;

        id = new ItemId("marklogic/v8.0");
        assertThat(id.getId(), is("marklogic/v8.0"));
        assertThat(id.getVendor(), is(MarkLogic.INSTANCE));
        assertThat(id.getProduct(), is(MarkLogic.INSTANCE.getMARKLOGIC()));
        assertThat(id.getVersion(), is(MarkLogic.INSTANCE.getVERSION_8_0()));

        // Compatibility ID
        id = new ItemId("marklogic/v8");
        assertThat(id.getId(), is("marklogic/v8"));
        assertThat(id.getVendor(), is(MarkLogic.INSTANCE));
        assertThat(id.getProduct(), is(MarkLogic.INSTANCE.getMARKLOGIC()));
        assertThat(id.getVersion(), is(MarkLogic.INSTANCE.getVERSION_8_0()));

        id = new ItemId("marklogic/v0.8");
        assertThat(id.getId(), is("marklogic/v0.8"));
        assertThat(id.getVendor(), is(MarkLogic.INSTANCE));
        assertThat(id.getProduct(), is(MarkLogic.INSTANCE.getMARKLOGIC()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("marklogic/8.0");
        assertThat(id.getId(), is("marklogic/8.0"));
        assertThat(id.getVendor(), is(MarkLogic.INSTANCE));
        assertThat(id.getProduct(), is(nullValue()));
        assertThat(id.getVersion(), is(nullValue()));
    }

    public void testItemId_Saxon_Products() {
        ItemId id;

        id = new ItemId("saxon/HE");
        assertThat(id.getId(), is("saxon/HE"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(Saxon.INSTANCE.getHE()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("saxon/PE");
        assertThat(id.getId(), is("saxon/PE"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(Saxon.INSTANCE.getPE()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("saxon/EE");
        assertThat(id.getId(), is("saxon/EE"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(Saxon.INSTANCE.getEE()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("saxon/EE-T");
        assertThat(id.getId(), is("saxon/EE-T"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(Saxon.INSTANCE.getEE_T()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("saxon/EE-Q");
        assertThat(id.getId(), is("saxon/EE-Q"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(Saxon.INSTANCE.getEE_Q()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("saxon/EE-V");
        assertThat(id.getId(), is("saxon/EE-V"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(Saxon.INSTANCE.getEE_V()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("saxon/he");
        assertThat(id.getId(), is("saxon/he"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(nullValue()));
        assertThat(id.getVersion(), is(nullValue()));
    }

    public void testItemId_Saxon_Versions() {
        ItemId id;

        id = new ItemId("saxon/HE/v9.6");
        assertThat(id.getId(), is("saxon/HE/v9.6"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(Saxon.INSTANCE.getHE()));
        assertThat(id.getVersion(), is(Saxon.INSTANCE.getVERSION_9_6()));

        id = new ItemId("saxon/EE-T/v9.5");
        assertThat(id.getId(), is("saxon/EE-T/v9.5"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(Saxon.INSTANCE.getEE_T()));
        assertThat(id.getVersion(), is(Saxon.INSTANCE.getVERSION_9_5()));

        id = new ItemId("saxon/HE/9.6");
        assertThat(id.getId(), is("saxon/HE/9.6"));
        assertThat(id.getVendor(), is(Saxon.INSTANCE));
        assertThat(id.getProduct(), is(Saxon.INSTANCE.getHE()));
        assertThat(id.getVersion(), is(nullValue()));
    }

    public void testItemId_W3C_Products() {
        ItemId id;

        id = new ItemId("w3c/spec");
        assertThat(id.getId(), is("w3c/spec"));
        assertThat(id.getVendor(), is(W3C.INSTANCE));
        assertThat(id.getProduct(), is(W3C.INSTANCE.getSPECIFICATIONS()));
        assertThat(id.getVersion(), is(nullValue()));

        id = new ItemId("w3c/SPEC");
        assertThat(id.getId(), is("w3c/SPEC"));
        assertThat(id.getVendor(), is(W3C.INSTANCE));
        assertThat(id.getProduct(), is(nullValue()));
        assertThat(id.getVersion(), is(nullValue()));
    }

    // endregion
}
