/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xquery.lang.*

@Suppress("JoinDeclarationAndAssignment")
class ModelTest {
    // region Version :: Display Name (toString)

    @Test
    fun testSpecification_DisplayName() {
        assertThat(XmlSchema.REC_1_0_20041028.toString(), `is`("XML Schema Definition 1.0"))
        assertThat(XmlSchema.REC_1_1_20120405.toString(), `is`("XML Schema Definition 1.1"))

        assertThat(XQuery.REC_1_0_20070123.toString(), `is`("XQuery 1.0"))
        assertThat(XQuery.MARKLOGIC_0_9.toString(), `is`("XQuery 0.9-ml"))

        assertThat(FullText.REC_1_0_20110317.toString(), `is`("XQuery and XPath Full Text 1.0"))

        assertThat(FunctionsAndOperators.REC_1_0_20070123.toString(), `is`("XQuery and XPath Functions and Operators 1.0"))

        assertThat(Scripting.NOTE_1_0_20140918.toString(), `is`("XQuery Scripting Extension 1.0"))

        assertThat(UpdateFacility.REC_1_0_20110317.toString(), `is`("XQuery Update Facility 1.0"))
    }

    @Test
    fun testProductVersion_DisplayName() {
        assertThat(BaseX.VERSION_8_5.toString(), `is`("BaseX 8.5"))

        assertThat(MarkLogic.VERSION_7_0.toString(), `is`("MarkLogic 7.0"))

        assertThat(Saxon.VERSION_9_7.toString(), `is`("Saxon 9.7"))
    }

    @Test
    fun testNamedVersion_DisplayName() {
        assertThat(W3C.FIRST_EDITION.toString(), `is`("W3C First Edition"))
    }

    // endregion
    // region Versioned :: Supports Dialect

    @Test
    fun testXmlSchema_SupportsDialect() {
        val versioned = XmlSchema
        assertThat(versioned.supportsDialect(XmlSchema), `is`(true))
        assertThat(versioned.supportsDialect(XQuery), `is`(false))
        assertThat(versioned.supportsDialect(FullText), `is`(false))
        assertThat(versioned.supportsDialect(FunctionsAndOperators), `is`(false))
        assertThat(versioned.supportsDialect(Scripting), `is`(false))
        assertThat(versioned.supportsDialect(UpdateFacility), `is`(false))

        assertThat(versioned.supportsDialect(BaseX), `is`(false))
        assertThat(versioned.supportsDialect(MarkLogic), `is`(false))
        assertThat(versioned.supportsDialect(Saxon), `is`(false))
        assertThat(versioned.supportsDialect(W3C), `is`(false))
    }

    @Test
    fun testXQuery_SupportsDialect() {
        val versioned = XQuery
        assertThat(versioned.supportsDialect(XmlSchema), `is`(false))
        assertThat(versioned.supportsDialect(XQuery), `is`(true))
        assertThat(versioned.supportsDialect(FullText), `is`(false))
        assertThat(versioned.supportsDialect(FunctionsAndOperators), `is`(false))
        assertThat(versioned.supportsDialect(Scripting), `is`(false))
        assertThat(versioned.supportsDialect(UpdateFacility), `is`(false))

        assertThat(versioned.supportsDialect(BaseX), `is`(false))
        assertThat(versioned.supportsDialect(MarkLogic), `is`(false))
        assertThat(versioned.supportsDialect(Saxon), `is`(false))
        assertThat(versioned.supportsDialect(W3C), `is`(false))
    }

    @Test
    fun testFullText_SupportsDialect() {
        val versioned = FullText
        assertThat(versioned.supportsDialect(XmlSchema), `is`(false))
        assertThat(versioned.supportsDialect(XQuery), `is`(true))
        assertThat(versioned.supportsDialect(FullText), `is`(true))
        assertThat(versioned.supportsDialect(FunctionsAndOperators), `is`(false))
        assertThat(versioned.supportsDialect(Scripting), `is`(false))
        assertThat(versioned.supportsDialect(UpdateFacility), `is`(false))

        assertThat(versioned.supportsDialect(BaseX), `is`(false))
        assertThat(versioned.supportsDialect(MarkLogic), `is`(false))
        assertThat(versioned.supportsDialect(Saxon), `is`(false))
        assertThat(versioned.supportsDialect(W3C), `is`(false))
    }

    @Test
    fun testFunctionsAndOperators_SupportsDialect() {
        val versioned = FunctionsAndOperators
        assertThat(versioned.supportsDialect(XmlSchema), `is`(false))
        assertThat(versioned.supportsDialect(XQuery), `is`(false))
        assertThat(versioned.supportsDialect(FullText), `is`(false))
        assertThat(versioned.supportsDialect(FunctionsAndOperators), `is`(true))
        assertThat(versioned.supportsDialect(Scripting), `is`(false))
        assertThat(versioned.supportsDialect(UpdateFacility), `is`(false))

        assertThat(versioned.supportsDialect(BaseX), `is`(false))
        assertThat(versioned.supportsDialect(MarkLogic), `is`(false))
        assertThat(versioned.supportsDialect(Saxon), `is`(false))
        assertThat(versioned.supportsDialect(W3C), `is`(false))
    }

    @Test
    fun testScripting_SupportsDialect() {
        val versioned = Scripting
        assertThat(versioned.supportsDialect(XmlSchema), `is`(false))
        assertThat(versioned.supportsDialect(XQuery), `is`(true))
        assertThat(versioned.supportsDialect(FullText), `is`(false))
        assertThat(versioned.supportsDialect(FunctionsAndOperators), `is`(false))
        assertThat(versioned.supportsDialect(Scripting), `is`(true))
        assertThat(versioned.supportsDialect(UpdateFacility), `is`(true))

        assertThat(versioned.supportsDialect(BaseX), `is`(false))
        assertThat(versioned.supportsDialect(MarkLogic), `is`(false))
        assertThat(versioned.supportsDialect(Saxon), `is`(false))
        assertThat(versioned.supportsDialect(W3C), `is`(false))
    }

    @Test
    fun testUpdateFacility_SupportsDialect() {
        val versioned = UpdateFacility
        assertThat(versioned.supportsDialect(XmlSchema), `is`(false))
        assertThat(versioned.supportsDialect(XQuery), `is`(true))
        assertThat(versioned.supportsDialect(FullText), `is`(false))
        assertThat(versioned.supportsDialect(FunctionsAndOperators), `is`(false))
        assertThat(versioned.supportsDialect(Scripting), `is`(false))
        assertThat(versioned.supportsDialect(UpdateFacility), `is`(true))

        assertThat(versioned.supportsDialect(BaseX), `is`(false))
        assertThat(versioned.supportsDialect(MarkLogic), `is`(false))
        assertThat(versioned.supportsDialect(Saxon), `is`(false))
        assertThat(versioned.supportsDialect(W3C), `is`(false))
    }

    @Test
    fun testBaseX_SupportsDialect() {
        val versioned = BaseX
        assertThat(versioned.supportsDialect(XmlSchema), `is`(false))
        assertThat(versioned.supportsDialect(XQuery), `is`(true))
        assertThat(versioned.supportsDialect(FullText), `is`(true))
        assertThat(versioned.supportsDialect(FunctionsAndOperators), `is`(false))
        assertThat(versioned.supportsDialect(Scripting), `is`(false))
        assertThat(versioned.supportsDialect(UpdateFacility), `is`(true))

        assertThat(versioned.supportsDialect(BaseX), `is`(true))
        assertThat(versioned.supportsDialect(MarkLogic), `is`(false))
        assertThat(versioned.supportsDialect(Saxon), `is`(false))
        assertThat(versioned.supportsDialect(W3C), `is`(false))
    }

    @Test
    fun testMarkLogic_SupportsDialect() {
        val versioned = MarkLogic
        assertThat(versioned.supportsDialect(XmlSchema), `is`(false))
        assertThat(versioned.supportsDialect(XQuery), `is`(true))
        assertThat(versioned.supportsDialect(FullText), `is`(false))
        assertThat(versioned.supportsDialect(FunctionsAndOperators), `is`(false))
        assertThat(versioned.supportsDialect(Scripting), `is`(false))
        assertThat(versioned.supportsDialect(UpdateFacility), `is`(false))

        assertThat(versioned.supportsDialect(BaseX), `is`(false))
        assertThat(versioned.supportsDialect(MarkLogic), `is`(true))
        assertThat(versioned.supportsDialect(Saxon), `is`(false))
        assertThat(versioned.supportsDialect(W3C), `is`(false))
    }

    @Test
    fun testSaxon_SupportsDialect() {
        val versioned = Saxon
        assertThat(versioned.supportsDialect(XmlSchema), `is`(false))
        assertThat(versioned.supportsDialect(XQuery), `is`(true))
        assertThat(versioned.supportsDialect(FullText), `is`(false))
        assertThat(versioned.supportsDialect(FunctionsAndOperators), `is`(false))
        assertThat(versioned.supportsDialect(Scripting), `is`(false))
        assertThat(versioned.supportsDialect(UpdateFacility), `is`(true))

        assertThat(versioned.supportsDialect(BaseX), `is`(false))
        assertThat(versioned.supportsDialect(MarkLogic), `is`(false))
        assertThat(versioned.supportsDialect(Saxon), `is`(true))
        assertThat(versioned.supportsDialect(W3C), `is`(false))
    }

    @Test
    fun testW3C_SupportsDialect() {
        val versioned = W3C
        assertThat(versioned.supportsDialect(XmlSchema), `is`(false))
        assertThat(versioned.supportsDialect(XQuery), `is`(false))
        assertThat(versioned.supportsDialect(FullText), `is`(false))
        assertThat(versioned.supportsDialect(FunctionsAndOperators), `is`(false))
        assertThat(versioned.supportsDialect(Scripting), `is`(false))
        assertThat(versioned.supportsDialect(UpdateFacility), `is`(false))

        assertThat(versioned.supportsDialect(BaseX), `is`(false))
        assertThat(versioned.supportsDialect(MarkLogic), `is`(false))
        assertThat(versioned.supportsDialect(Saxon), `is`(false))
        assertThat(versioned.supportsDialect(W3C), `is`(true))
    }

    // endregion
    // region Product :: Display Name (toString)

    @Test
    fun testProduct_DisplayName() {
        assertThat(BaseX.BASEX.toString(), `is`("BaseX"))
        assertThat(MarkLogic.MARKLOGIC.toString(), `is`("MarkLogic"))

        assertThat(Saxon.HE.toString(), `is`("Saxon Home Edition"))
        assertThat(Saxon.PE.toString(), `is`("Saxon Professional Edition"))
        assertThat(Saxon.EE.toString(), `is`("Saxon Enterprise Edition"))
        assertThat(Saxon.EE_T.toString(), `is`("Saxon Enterprise Edition (Transformation package)"))
        assertThat(Saxon.EE_Q.toString(), `is`("Saxon Enterprise Edition (Query package)"))
        assertThat(Saxon.EE_V.toString(), `is`("Saxon Enterprise Edition (Validation package)"))

        assertThat(W3C.SPECIFICATIONS.toString(), `is`("W3C Specifications"))
    }

    // endregion
    // region Product :: XQuery Conformance (Minimal Conformance; Optional Features)

    @Test
    fun testBaseXProduct_OptionalFeatureSupport() {
        val product = BaseX.products[0]
        assertThat(product.id, `is`("basex"))
        assertThat(product.implementation.id, `is`("basex"))

        for (version in BaseX.versions) {
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), `is`(true)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), `is`(true)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), `is`(true))
        }
    }

    @Test
    fun testMarkLogicProduct_OptionalFeatureSupport() {
        val product = MarkLogic.products[0]
        assertThat(product.id, `is`("marklogic"))
        assertThat(product.implementation.id, `is`("marklogic"))

        for (version in MarkLogic.versions) {
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), `is`(true)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), `is`(true)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), `is`(true))
        }
    }

    @Test
    fun testSaxonProduct_HE_OptionalFeatureSupport() {
        val product = Saxon.products[0]
        assertThat(product.id, `is`("HE"))
        assertThat(product.implementation.id, `is`("saxon"))

        for (version in Saxon.versions) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), `is`(true))
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), `is`(false))
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), `is`(false)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), `is`(false)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), `is`(false))
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), `is`(false))
        }
    }

    @Test
    fun testSaxonProduct_PE_OptionalFeatureSupport() {
        val product = Saxon.products[1]
        assertThat(product.id, `is`("PE"))
        assertThat(product.implementation.id, `is`("saxon"))

        for (version in Saxon.versions) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), `is`(true))
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), `is`(true))
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), `is`(false)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), `is`(false)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), `is`(false))
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), `is`(false))
        }
    }

    @Test
    fun testSaxonProduct_EE_OptionalFeatureSupport() {
        val product = Saxon.products[2]
        assertThat(product.id, `is`("EE"))
        assertThat(product.implementation.id, `is`("saxon"))

        for (version in Saxon.versions) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), `is`(true))
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), `is`(true))
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), `is`(true)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), `is`(true)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), `is`(true))
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), `is`(false))
        }
    }

    @Test
    fun testSaxonProduct_EE_T_OptionalFeatureSupport() {
        val product = Saxon.products[3]
        assertThat(product.id, `is`("EE-T"))
        assertThat(product.implementation.id, `is`("saxon"))

        for (version in Saxon.versions) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), `is`(true))
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), `is`(true))
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), `is`(false)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), `is`(false)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), `is`(false))
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), `is`(false))
        }
    }

    @Test
    fun testSaxonProduct_EE_Q_OptionalFeatureSupport() {
        val product = Saxon.products[4]
        assertThat(product.id, `is`("EE-Q"))
        assertThat(product.implementation.id, `is`("saxon"))

        for (version in Saxon.versions) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), `is`(true))
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), `is`(true))
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), `is`(true)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), `is`(true)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), `is`(true))
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), `is`(false))
        }
    }

    @Test
    fun testSaxonProduct_EE_V_OptionalFeatureSupport() {
        val product = Saxon.products[5]
        assertThat(product.id, `is`("EE-V"))
        assertThat(product.implementation.id, `is`("saxon"))

        for (version in Saxon.versions) {
            // XQuery 3.1 (Basic)
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), `is`(true))
            // XQuery 3.1 (Higher-Order Functions)
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), `is`(true))
            // XQuery 3.1 (Schema Aware)
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), `is`(false)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), `is`(false)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), `is`(false))
            // XQuery 3.1 (Static Typing)
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), `is`(false))
        }
    }

    @Test
    fun testW3CProduct_SPECIFICATIONS_OptionalFeatureSupport() {
        val product = W3C.products[0]
        assertThat(product.id, `is`("spec"))
        assertThat(product.implementation.id, `is`("w3c"))

        for (version in Saxon.versions) {
            assertThat(product.supportsFeature(version, XQueryFeature.MINIMAL_CONFORMANCE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.FULL_AXIS), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.HIGHER_ORDER_FUNCTION), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.MODULE), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_IMPORT), `is`(true)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.SCHEMA_VALIDATION), `is`(true)) // Schema Aware
            assertThat(product.supportsFeature(version, XQueryFeature.STATIC_TYPING), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.SERIALIZATION), `is`(true))
            assertThat(product.supportsFeature(version, XQueryFeature.TYPED_DATA), `is`(true))
        }
    }

    // endregion
    // region Product :: Conforms To (Specification, Vendor Extension/Implementation)

    @Test
    fun testBaseX_ConformsTo() {
        for (product in BaseX.products) {
            for (version in BaseX.versions) {
                // region Specification: XML Schema Definition Language (XSD)

                assertThat(product.conformsTo(version, XmlSchema.REC_1_0_20041028), `is`(true))
                assertThat(product.conformsTo(version, XmlSchema.REC_1_1_20120405), `is`(true))

                // endregion
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.REC_1_0_20070123), `is`(false))
                assertThat(product.conformsTo(version, XQuery.REC_1_0_20101214), `is`(false))
                assertThat(product.conformsTo(version, XQuery.REC_3_0_20140408), `is`(true))
                assertThat(product.conformsTo(version, XQuery.CR_3_1_20151217), `is`(version.value <= 8.5))
                assertThat(product.conformsTo(version, XQuery.REC_3_1_20170321), `is`(version.value >= 8.6))
                assertThat(product.conformsTo(version, XQuery.MARKLOGIC_0_9), `is`(false))
                assertThat(product.conformsTo(version, XQuery.MARKLOGIC_1_0), `is`(false))

                // endregion
                // region Specification: XQuery and XPath Full Text

                assertThat(product.conformsTo(version, FullText.REC_1_0_20110317), `is`(true))
                assertThat(product.conformsTo(version, FullText.REC_3_0_20151124), `is`(true))

                // endregion
                // region Specification: XQuery and XPath Functions and Operators

                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_1_0_20070123), `is`(false))
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_1_0_20101214), `is`(false))
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_3_0_20140408), `is`(true))
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_3_1_20170321), `is`(version.value >= 8.6))

                // endregion
                // region Specification: XQuery Scripting Extension

                assertThat(product.conformsTo(version, Scripting.NOTE_1_0_20140918), `is`(false))

                // endregion
                // region Specification: XQuery Update Facility

                assertThat(product.conformsTo(version, UpdateFacility.REC_1_0_20110317), `is`(true))
                assertThat(product.conformsTo(version, UpdateFacility.NOTE_3_0_20170124), `is`(version.value >= 8.5))

                // endregion
                // region Implementation: BaseX

                assertThat(product.conformsTo(version, BaseX.VERSION_8_4), `is`(version.value >= 8.4))
                assertThat(product.conformsTo(version, BaseX.VERSION_8_5), `is`(version.value >= 8.5))
                assertThat(product.conformsTo(version, BaseX.VERSION_8_6), `is`(version.value >= 8.6))

                // endregion
                // region Implementation: MarkLogic

                assertThat(product.conformsTo(version, MarkLogic.VERSION_6_0), `is`(false))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_7_0), `is`(false))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_8_0), `is`(false))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_9_0), `is`(false))

                // endregion
                // region Implementation: Saxon

                assertThat(product.conformsTo(version, Saxon.VERSION_9_5), `is`(false))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_6), `is`(false))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_7), `is`(false))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_8), `is`(false))

                // endregion
                // region Implementation: W3C

                assertThat(product.conformsTo(version, W3C.FIRST_EDITION), `is`(false))
                assertThat(product.conformsTo(version, W3C.SECOND_EDITION), `is`(false))

                // endregion
            }
        }
    }

    @Test
    fun testMarkLogic_ConformsTo() {
        for (product in MarkLogic.products) {
            for (version in MarkLogic.versions) {
                // region Specification: XML Schema Definition Language (XSD)

                assertThat(product.conformsTo(version, XmlSchema.REC_1_0_20041028), `is`(true))
                assertThat(product.conformsTo(version, XmlSchema.REC_1_1_20120405), `is`(version.value >= 9.0))

                // endregion
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.REC_1_0_20070123), `is`(true))
                assertThat(product.conformsTo(version, XQuery.REC_1_0_20101214), `is`(false))
                assertThat(product.conformsTo(version, XQuery.REC_3_0_20140408), `is`(false))
                assertThat(product.conformsTo(version, XQuery.CR_3_1_20151217), `is`(false))
                assertThat(product.conformsTo(version, XQuery.REC_3_1_20170321), `is`(false))
                assertThat(product.conformsTo(version, XQuery.MARKLOGIC_0_9), `is`(true))
                assertThat(product.conformsTo(version, XQuery.MARKLOGIC_1_0), `is`(true))

                // endregion
                // region Specification: XQuery and XPath Full Text

                assertThat(product.conformsTo(version, FullText.REC_1_0_20110317), `is`(false))
                assertThat(product.conformsTo(version, FullText.REC_3_0_20151124), `is`(false))

                // endregion
                // region Specification: XQuery and XPath Functions and Operators

                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_1_0_20070123), `is`(true))
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_1_0_20101214), `is`(false))
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_3_0_20140408), `is`(true))
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_3_1_20170321), `is`(false))

                // endregion
                // region Specification: XQuery Scripting Extension

                assertThat(product.conformsTo(version, Scripting.NOTE_1_0_20140918), `is`(false))

                // endregion
                // region Specification: XQuery Update Facility

                assertThat(product.conformsTo(version, UpdateFacility.REC_1_0_20110317), `is`(false))
                assertThat(product.conformsTo(version, UpdateFacility.NOTE_3_0_20170124), `is`(false))

                // endregion
                // region Implementation: BaseX

                assertThat(product.conformsTo(version, BaseX.VERSION_8_4), `is`(false))
                assertThat(product.conformsTo(version, BaseX.VERSION_8_5), `is`(false))
                assertThat(product.conformsTo(version, BaseX.VERSION_8_6), `is`(false))

                // endregion
                // region Implementation: MarkLogic

                assertThat(product.conformsTo(version, MarkLogic.VERSION_6_0), `is`(version.value >= 6.0))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_7_0), `is`(version.value >= 7.0))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_8_0), `is`(version.value >= 8.0))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_9_0), `is`(version.value >= 9.0))

                // endregion
                // region Implementation: Saxon

                assertThat(product.conformsTo(version, Saxon.VERSION_9_5), `is`(false))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_6), `is`(false))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_7), `is`(false))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_8), `is`(false))

                // endregion
                // region Implementation: W3C

                assertThat(product.conformsTo(version, W3C.FIRST_EDITION), `is`(false))
                assertThat(product.conformsTo(version, W3C.SECOND_EDITION), `is`(false))

                // endregion
            }
        }
    }

    @Test
    fun testSaxon_ConformsTo() {
        for (product in Saxon.products) {
            for (version in Saxon.versions) {
                // region Specification: XML Schema Definition Language (XSD)

                assertThat(product.conformsTo(version, XmlSchema.REC_1_0_20041028), `is`(true))
                assertThat(product.conformsTo(version, XmlSchema.REC_1_1_20120405), `is`(true))

                // endregion
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.REC_1_0_20070123), `is`(true))
                assertThat(product.conformsTo(version, XQuery.REC_1_0_20101214), `is`(false))
                if (product.id == "HE") {
                    assertThat(product.conformsTo(version, XQuery.REC_3_0_20140408), `is`(version.value >= 9.6))
                } else {
                    assertThat(product.conformsTo(version, XQuery.REC_3_0_20140408), `is`(version.value >= 9.5))
                }
                assertThat(product.conformsTo(version, XQuery.CR_3_1_20151217), `is`(version.value == 9.7))
                assertThat(product.conformsTo(version, XQuery.REC_3_1_20170321), `is`(version.value >= 9.8))
                assertThat(product.conformsTo(version, XQuery.MARKLOGIC_0_9), `is`(false))
                assertThat(product.conformsTo(version, XQuery.MARKLOGIC_1_0), `is`(false))

                // endregion
                // region Specification: XQuery and XPath Full Text

                assertThat(product.conformsTo(version, FullText.REC_1_0_20110317), `is`(false))
                assertThat(product.conformsTo(version, FullText.REC_3_0_20151124), `is`(false))

                // endregion
                // region Specification: XQuery and XPath Functions and Operators

                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_1_0_20070123), `is`(true))
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_1_0_20101214), `is`(false))
                if (product.id == "HE") {
                    assertThat(product.conformsTo(version, FunctionsAndOperators.REC_3_0_20140408), `is`(version.value >= 9.6))
                } else {
                    assertThat(product.conformsTo(version, FunctionsAndOperators.REC_3_0_20140408), `is`(version.value >= 9.5))
                }
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_3_1_20170321), `is`(version.value >= 9.8))

                // endregion
                // region Specification: XQuery Scripting Extension

                assertThat(product.conformsTo(version, Scripting.NOTE_1_0_20140918), `is`(false))

                // endregion
                // region Specification: XQuery Update Facility

                if (product.id == "HE" || product.id == "PE") {
                    assertThat(product.conformsTo(version, UpdateFacility.REC_1_0_20110317), `is`(false))
                } else {
                    assertThat(product.conformsTo(version, UpdateFacility.REC_1_0_20110317), `is`(true))
                }
                assertThat(product.conformsTo(version, UpdateFacility.NOTE_3_0_20170124), `is`(false))

                // endregion
                // region Implementation: BaseX

                assertThat(product.conformsTo(version, BaseX.VERSION_8_4), `is`(false))
                assertThat(product.conformsTo(version, BaseX.VERSION_8_5), `is`(false))
                assertThat(product.conformsTo(version, BaseX.VERSION_8_6), `is`(false))

                // endregion
                // region Implementation: MarkLogic

                assertThat(product.conformsTo(version, MarkLogic.VERSION_6_0), `is`(false))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_7_0), `is`(false))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_8_0), `is`(false))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_9_0), `is`(false))

                // endregion
                // region Implementation: Saxon

                assertThat(product.conformsTo(version, Saxon.VERSION_9_5), `is`(version.value >= 9.5))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_6), `is`(version.value >= 9.6))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_7), `is`(version.value >= 9.7))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_8), `is`(version.value >= 9.8))

                // endregion
                // region Implementation: W3C

                assertThat(product.conformsTo(version, W3C.FIRST_EDITION), `is`(false))
                assertThat(product.conformsTo(version, W3C.SECOND_EDITION), `is`(false))

                // endregion
            }
        }
    }

    @Test
    fun testW3C_ConformsTo() {
        for (product in W3C.products) {
            for (version in W3C.versions) {
                // region Specification: XML Schema Definition Language (XSD)

                assertThat(product.conformsTo(version, XmlSchema.REC_1_0_20041028), `is`(true))
                assertThat(product.conformsTo(version, XmlSchema.REC_1_1_20120405), `is`(true))

                // endregion
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.REC_1_0_20070123), `is`(version.value == 1.0))
                assertThat(product.conformsTo(version, XQuery.REC_1_0_20101214), `is`(version.value == 2.0))
                assertThat(product.conformsTo(version, XQuery.REC_3_0_20140408), `is`(version.value == 1.0))
                assertThat(product.conformsTo(version, XQuery.CR_3_1_20151217), `is`(false))
                assertThat(product.conformsTo(version, XQuery.REC_3_1_20170321), `is`(version.value == 1.0))
                assertThat(product.conformsTo(version, XQuery.MARKLOGIC_0_9), `is`(false))
                assertThat(product.conformsTo(version, XQuery.MARKLOGIC_1_0), `is`(false))

                // endregion
                // region Specification: XQuery and XPath Full Text

                assertThat(product.conformsTo(version, FullText.REC_1_0_20110317), `is`(version.value == 1.0))
                assertThat(product.conformsTo(version, FullText.REC_3_0_20151124), `is`(version.value == 1.0))

                // endregion
                // region Specification: XQuery and XPath Functions and Operators

                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_1_0_20070123), `is`(version.value == 1.0))
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_1_0_20101214), `is`(version.value == 2.0))
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_3_0_20140408), `is`(version.value == 1.0))
                assertThat(product.conformsTo(version, FunctionsAndOperators.REC_3_1_20170321), `is`(version.value == 1.0))

                // endregion
                // region Specification: XQuery Scripting Extension

                assertThat(product.conformsTo(version, Scripting.NOTE_1_0_20140918), `is`(version.value == 1.0))

                // endregion
                // region Specification: XQuery Update Facility

                assertThat(product.conformsTo(version, UpdateFacility.REC_1_0_20110317), `is`(version.value == 1.0))
                assertThat(product.conformsTo(version, UpdateFacility.NOTE_3_0_20170124), `is`(version.value == 1.0))

                // endregion
                // region Implementation: BaseX

                assertThat(product.conformsTo(version, BaseX.VERSION_8_4), `is`(false))
                assertThat(product.conformsTo(version, BaseX.VERSION_8_5), `is`(false))
                assertThat(product.conformsTo(version, BaseX.VERSION_8_6), `is`(false))

                // endregion
                // region Implementation: MarkLogic

                assertThat(product.conformsTo(version, MarkLogic.VERSION_6_0), `is`(false))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_7_0), `is`(false))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_8_0), `is`(false))
                assertThat(product.conformsTo(version, MarkLogic.VERSION_9_0), `is`(false))

                // endregion
                // region Implementation: Saxon

                assertThat(product.conformsTo(version, Saxon.VERSION_9_5), `is`(false))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_6), `is`(false))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_7), `is`(false))
                assertThat(product.conformsTo(version, Saxon.VERSION_9_8), `is`(false))

                // endregion
                // region Implementation: W3C

                assertThat(product.conformsTo(version, W3C.FIRST_EDITION), `is`(false))
                assertThat(product.conformsTo(version, W3C.SECOND_EDITION), `is`(false))

                // endregion
            }
        }
    }

    // endregion
    // region Product :: XQuery Versions

    @Test
    fun testBaseX_XQueryVersions() {
        var xquery: List<Version>
        for (product in BaseX.products) {
            for (version in BaseX.versions) {
                xquery = XQuery.versionsFor(product, version)
                assertThat(xquery.size, `is`(2))
                assertThat(xquery[0], `is`<Version>(XQuery.REC_3_0_20140408))
                if (version.value <= 8.5) {
                    assertThat(xquery[1], `is`<Version>(XQuery.CR_3_1_20151217))
                } else {
                    assertThat(xquery[1], `is`<Version>(XQuery.REC_3_1_20170321))
                }
            }
        }
    }

    @Test
    fun testMarkLogic_XQueryVersions() {
        var xquery: List<Version>
        for (product in MarkLogic.products) {
            for (version in MarkLogic.versions) {
                xquery = XQuery.versionsFor(product, version)
                assertThat(xquery.size, `is`(3))
                assertThat(xquery[0], `is`<Version>(XQuery.MARKLOGIC_0_9))
                assertThat(xquery[1], `is`<Version>(XQuery.REC_1_0_20070123))
                assertThat(xquery[2], `is`<Version>(XQuery.MARKLOGIC_1_0))
            }
        }
    }

    @Test
    fun testSaxon_XQueryVersions() {
        var xquery: List<Version>
        for (product in Saxon.products) {
            xquery = XQuery.versionsFor(product, Saxon.VERSION_9_5)
            if (product.id == "HE") {
                assertThat(xquery.size, `is`(1))
                assertThat(xquery[0], `is`<Version>(XQuery.REC_1_0_20070123))
            } else {
                assertThat(xquery.size, `is`(2))
                assertThat(xquery[0], `is`<Version>(XQuery.REC_1_0_20070123))
                assertThat(xquery[1], `is`<Version>(XQuery.REC_3_0_20140408))
            }

            xquery = XQuery.versionsFor(product, Saxon.VERSION_9_6)
            assertThat(xquery.size, `is`(2))
            assertThat(xquery[0], `is`<Version>(XQuery.REC_1_0_20070123))
            assertThat(xquery[1], `is`<Version>(XQuery.REC_3_0_20140408))

            xquery = XQuery.versionsFor(product, Saxon.VERSION_9_7)
            assertThat(xquery.size, `is`(3))
            assertThat(xquery[0], `is`<Version>(XQuery.REC_1_0_20070123))
            assertThat(xquery[1], `is`<Version>(XQuery.REC_3_0_20140408))
            assertThat(xquery[2], `is`<Version>(XQuery.CR_3_1_20151217))

            xquery = XQuery.versionsFor(product, Saxon.VERSION_9_8)
            assertThat(xquery.size, `is`(3))
            assertThat(xquery[0], `is`<Version>(XQuery.REC_1_0_20070123))
            assertThat(xquery[1], `is`<Version>(XQuery.REC_3_0_20140408))
            assertThat(xquery[2], `is`<Version>(XQuery.REC_3_1_20170321))
        }
    }

    @Test
    fun testW3C_XQueryVersions() {
        var xquery: List<Version>
        for (product in W3C.products) {
            xquery = XQuery.versionsFor(product, W3C.FIRST_EDITION)
            assertThat(xquery.size, `is`(3))
            assertThat(xquery[0], `is`<Version>(XQuery.REC_1_0_20070123))
            assertThat(xquery[1], `is`<Version>(XQuery.REC_3_0_20140408))
            assertThat(xquery[2], `is`<Version>(XQuery.REC_3_1_20170321))

            xquery = XQuery.versionsFor(product, W3C.SECOND_EDITION)
            assertThat(xquery.size, `is`(1))
            assertThat(xquery[0], `is`<Version>(XQuery.REC_1_0_20101214))
        }
    }

    // endregion
    // region Product :: Flavours For XQuery Version

    @Test
    fun testBaseX_FlavoursForXQueryVersion() {
        var flavours: List<Versioned>
        for (product in BaseX.products) {
            for (version in BaseX.versions) {
                flavours = product.flavoursForXQueryVersion(version, "1.0")
                assertThat(flavours.size, `is`(0))

                flavours = product.flavoursForXQueryVersion(version, "3.0")
                assertThat(flavours.size, `is`(4))
                assertThat(flavours[0], `is`<Versioned>(BaseX))
                assertThat(flavours[1], `is`<Versioned>(XQuery))
                assertThat(flavours[2], `is`<Versioned>(FullText))
                assertThat(flavours[3], `is`<Versioned>(UpdateFacility))

                flavours = product.flavoursForXQueryVersion(version, "3.1")
                assertThat(flavours.size, `is`(4))
                assertThat(flavours[0], `is`<Versioned>(BaseX))
                assertThat(flavours[1], `is`<Versioned>(XQuery))
                assertThat(flavours[2], `is`<Versioned>(FullText))
                assertThat(flavours[3], `is`<Versioned>(UpdateFacility))

                flavours = product.flavoursForXQueryVersion(version, "0.9-ml")
                assertThat(flavours.size, `is`(0))

                flavours = product.flavoursForXQueryVersion(version, "1.0-ml")
                assertThat(flavours.size, `is`(0))
            }
        }
    }

    @Test
    fun testMarkLogic_FlavoursForXQueryVersion() {
        var flavours: List<Versioned>
        for (product in MarkLogic.products) {
            for (version in MarkLogic.versions) {
                flavours = product.flavoursForXQueryVersion(version, "1.0")
                assertThat(flavours.size, `is`(1))
                assertThat(flavours[0], `is`<Versioned>(XQuery))

                flavours = product.flavoursForXQueryVersion(version, "3.0")
                assertThat(flavours.size, `is`(0))

                flavours = product.flavoursForXQueryVersion(version, "3.1")
                assertThat(flavours.size, `is`(0))

                flavours = product.flavoursForXQueryVersion(version, "0.9-ml")
                assertThat(flavours.size, `is`(1))
                assertThat(flavours[0], `is`<Versioned>(MarkLogic))

                flavours = product.flavoursForXQueryVersion(version, "1.0-ml")
                assertThat(flavours.size, `is`(1))
                assertThat(flavours[0], `is`<Versioned>(MarkLogic))
            }
        }
    }

    @Test
    fun testSaxon_FlavoursForXQueryVersion() {
        var flavours: List<Versioned>
        for (product in Saxon.products) {
            for (version in Saxon.versions) {
                flavours = product.flavoursForXQueryVersion(version, "1.0")
                if (product.id == "HE") {
                    assertThat(flavours.size, `is`(1))
                    assertThat(flavours[0], `is`<Versioned>(XQuery))
                } else if (product.id == "PE") {
                    assertThat(flavours.size, `is`(2))
                    assertThat(flavours[0], `is`<Versioned>(Saxon))
                    assertThat(flavours[1], `is`<Versioned>(XQuery))
                } else {
                    assertThat(flavours.size, `is`(3))
                    assertThat(flavours[0], `is`<Versioned>(Saxon))
                    assertThat(flavours[1], `is`<Versioned>(XQuery))
                    assertThat(flavours[2], `is`<Versioned>(UpdateFacility))
                }

                flavours = product.flavoursForXQueryVersion(version, "3.0")
                if (product.id == "HE") {
                    assertThat(flavours.size, `is`(1))
                    assertThat(flavours[0], `is`<Versioned>(XQuery))
                } else if (product.id == "PE") {
                    assertThat(flavours.size, `is`(2))
                    assertThat(flavours[0], `is`<Versioned>(Saxon))
                    assertThat(flavours[1], `is`<Versioned>(XQuery))
                } else {
                    assertThat(flavours.size, `is`(3))
                    assertThat(flavours[0], `is`<Versioned>(Saxon))
                    assertThat(flavours[1], `is`<Versioned>(XQuery))
                    assertThat(flavours[2], `is`<Versioned>(UpdateFacility))
                }

                flavours = product.flavoursForXQueryVersion(version, "3.1")
                if (product.id == "HE") {
                    assertThat(flavours.size, `is`(1))
                    assertThat(flavours[0], `is`<Versioned>(XQuery))
                } else if (product.id == "PE") {
                    assertThat(flavours.size, `is`(2))
                    assertThat(flavours[0], `is`<Versioned>(Saxon))
                    assertThat(flavours[1], `is`<Versioned>(XQuery))
                } else {
                    assertThat(flavours.size, `is`(3))
                    assertThat(flavours[0], `is`<Versioned>(Saxon))
                    assertThat(flavours[1], `is`<Versioned>(XQuery))
                    assertThat(flavours[2], `is`<Versioned>(UpdateFacility))
                }

                flavours = product.flavoursForXQueryVersion(version, "0.9-ml")
                assertThat(flavours.size, `is`(0))

                flavours = product.flavoursForXQueryVersion(version, "1.0-ml")
                assertThat(flavours.size, `is`(0))
            }
        }
    }

    @Test
    fun testW3C_FlavoursForXQueryVersion() {
        var flavours: List<Versioned>
        for (product in W3C.products) {
            for (version in W3C.versions) {
                flavours = product.flavoursForXQueryVersion(version, "1.0")
                if (version.value == 1.0) {
                    assertThat(flavours.size, `is`(4))
                    assertThat(flavours[0], `is`<Versioned>(XQuery))
                    assertThat(flavours[1], `is`<Versioned>(FullText))
                    assertThat(flavours[2], `is`<Versioned>(UpdateFacility))
                    assertThat(flavours[3], `is`<Versioned>(Scripting))
                } else {
                    assertThat(flavours.size, `is`(1))
                    assertThat(flavours[0], `is`<Versioned>(XQuery))
                }

                flavours = product.flavoursForXQueryVersion(version, "3.0")
                if (version.value == 1.0) {
                    assertThat(flavours.size, `is`(3))
                    assertThat(flavours[0], `is`<Versioned>(XQuery))
                    assertThat(flavours[1], `is`<Versioned>(FullText))
                    assertThat(flavours[2], `is`<Versioned>(UpdateFacility))
                } else {
                    assertThat(flavours.size, `is`(1))
                    assertThat(flavours[0], `is`<Versioned>(XQuery))
                }

                flavours = product.flavoursForXQueryVersion(version, "3.1")
                assertThat(flavours.size, `is`(1))
                assertThat(flavours[0], `is`<Versioned>(XQuery))

                flavours = product.flavoursForXQueryVersion(version, "0.9-ml")
                assertThat(flavours.size, `is`(0))

                flavours = product.flavoursForXQueryVersion(version, "1.0-ml")
                assertThat(flavours.size, `is`(0))
            }
        }
    }

    // endregion
    // region XQuery :: Version For XQuery

    @Test
    fun testBaseX_VersionForXQuery() {
        for (product in BaseX.products) {
            for (version in BaseX.versions) {
                assertThat<Specification>(XQuery.versionForXQuery(product, version, "1.0"),
                        `is`(nullValue()))
                assertThat<Specification>(XQuery.versionForXQuery(product, version, "3.0"),
                        `is`(XQuery.REC_3_0_20140408))
                assertThat<Specification>(XQuery.versionForXQuery(product, version, "3.1"),
                        `is`(if (version.value <= 8.5) XQuery.CR_3_1_20151217 else XQuery.REC_3_1_20170321))

                assertThat<Specification>(XQuery.versionForXQuery(product, version, "0.9-ml"),
                        `is`(nullValue()))
                assertThat<Specification>(XQuery.versionForXQuery(product, version, "1.0-ml"),
                        `is`(nullValue()))

                assertThat<Specification>(XQuery.versionForXQuery(product, version, "3"),
                        `is`(nullValue()))
            }
        }
    }

    @Test
    fun testMarkLogic_VersionsForXQuery() {
        for (product in MarkLogic.products) {
            for (version in MarkLogic.versions) {
                assertThat<Specification>(XQuery.versionForXQuery(product, version, "1.0"),
                        `is`(XQuery.REC_1_0_20070123))
                assertThat<Specification>(XQuery.versionForXQuery(product, version, "3.0"),
                        `is`(nullValue()))
                assertThat<Specification>(XQuery.versionForXQuery(product, version, "3.1"),
                        `is`(nullValue()))

                assertThat<Specification>(XQuery.versionForXQuery(product, version, "0.9-ml"),
                        `is`(XQuery.MARKLOGIC_0_9))
                assertThat<Specification>(XQuery.versionForXQuery(product, version, "1.0-ml"),
                        `is`(XQuery.MARKLOGIC_1_0))

                assertThat<Specification>(XQuery.versionForXQuery(product, version, "3"),
                        `is`(nullValue()))
            }
        }
    }

    // endregion
    // region Item ID

    @Test
    fun testItemId_VendorOnly() {
        var id: VersionedProductId

        id = VersionedProductId("basex")
        assertThat<String>(id.id, `is`("basex"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(BaseX))
        assertThat<Product>(id.product, `is`(nullValue()))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("marklogic")
        assertThat<String>(id.id, `is`("marklogic"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(MarkLogic))
        assertThat<Product>(id.product, `is`(nullValue()))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("saxon")
        assertThat<String>(id.id, `is`("saxon"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(nullValue()))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("w3c")
        assertThat<String>(id.id, `is`("w3c"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(W3C))
        assertThat<Product>(id.product, `is`(nullValue()))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("loremipsum")
        assertThat<String>(id.id, `is`(nullValue()))
        assertThat<Implementation>(id.vendor, `is`(nullValue()))
        assertThat<Product>(id.product, `is`(nullValue()))
        assertThat<Version>(id.productVersion, `is`(nullValue()))
    }

    @Test
    fun testItemId_BaseX_Versions() {
        var id: VersionedProductId

        id = VersionedProductId("basex/v8.4")
        assertThat<String>(id.id, `is`("basex/v8.4"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(BaseX))
        assertThat<Product>(id.product, `is`(BaseX.BASEX))
        assertThat<Version>(id.productVersion, `is`(BaseX.VERSION_8_4))

        id = VersionedProductId("basex/v0.5")
        assertThat<String>(id.id, `is`("basex"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(BaseX))
        assertThat<Product>(id.product, `is`(BaseX.BASEX))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("basex/8.4")
        assertThat<String>(id.id, `is`("basex"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(BaseX))
        assertThat<Product>(id.product, `is`(nullValue()))
        assertThat<Version>(id.productVersion, `is`(nullValue()))
    }

    @Test
    fun testItemId_MarkLogic_Versions() {
        var id: VersionedProductId

        id = VersionedProductId("marklogic/v8.0")
        assertThat<String>(id.id, `is`("marklogic/v8"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(MarkLogic))
        assertThat<Product>(id.product, `is`(MarkLogic.MARKLOGIC))
        assertThat<Version>(id.productVersion, `is`(MarkLogic.VERSION_8_0))

        // Compatibility ID
        id = VersionedProductId("marklogic/v8")
        assertThat<String>(id.id, `is`("marklogic/v8"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(MarkLogic))
        assertThat<Product>(id.product, `is`(MarkLogic.MARKLOGIC))
        assertThat<Version>(id.productVersion, `is`(MarkLogic.VERSION_8_0))

        id = VersionedProductId("marklogic/v0.8")
        assertThat<String>(id.id, `is`("marklogic"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(MarkLogic))
        assertThat<Product>(id.product, `is`(MarkLogic.MARKLOGIC))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("marklogic/8.0")
        assertThat<String>(id.id, `is`("marklogic"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(MarkLogic))
        assertThat<Product>(id.product, `is`(nullValue()))
        assertThat<Version>(id.productVersion, `is`(nullValue()))
    }

    @Test
    fun testItemId_Saxon_Products() {
        var id: VersionedProductId

        id = VersionedProductId("saxon/HE")
        assertThat<String>(id.id, `is`("saxon/HE"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(Saxon.HE))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("saxon/PE")
        assertThat<String>(id.id, `is`("saxon/PE"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(Saxon.PE))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("saxon/EE")
        assertThat<String>(id.id, `is`("saxon/EE"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(Saxon.EE))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("saxon/EE-T")
        assertThat<String>(id.id, `is`("saxon/EE-T"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(Saxon.EE_T))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("saxon/EE-Q")
        assertThat<String>(id.id, `is`("saxon/EE-Q"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(Saxon.EE_Q))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("saxon/EE-V")
        assertThat<String>(id.id, `is`("saxon/EE-V"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(Saxon.EE_V))
        assertThat<Version>(id.productVersion, `is`(nullValue()))

        id = VersionedProductId("saxon/he")
        assertThat<String>(id.id, `is`("saxon"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(nullValue()))
        assertThat<Version>(id.productVersion, `is`(nullValue()))
    }

    @Test
    fun testItemId_Saxon_Versions() {
        var id: VersionedProductId

        id = VersionedProductId("saxon/HE/v9.6")
        assertThat<String>(id.id, `is`("saxon/HE/v9.6"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(Saxon.HE))
        assertThat<Version>(id.productVersion, `is`(Saxon.VERSION_9_6))

        id = VersionedProductId("saxon/EE-T/v9.5")
        assertThat<String>(id.id, `is`("saxon/EE-T/v9.5"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(Saxon.EE_T))
        assertThat<Version>(id.productVersion, `is`(Saxon.VERSION_9_5))

        id = VersionedProductId("saxon/HE/9.6")
        assertThat<String>(id.id, `is`("saxon/HE"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(Saxon))
        assertThat<Product>(id.product, `is`(Saxon.HE))
        assertThat<Version>(id.productVersion, `is`(nullValue()))
    }

    @Test
    fun testItemId_W3C_Products() {
        var id: VersionedProductId

        id = VersionedProductId("w3c/spec")
        assertThat<String>(id.id, `is`("w3c/spec/v1ed"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(W3C))
        assertThat<Product>(id.product, `is`(W3C.SPECIFICATIONS))
        assertThat<Version>(id.productVersion, `is`<Version>(W3C.FIRST_EDITION))

        id = VersionedProductId("w3c/SPEC")
        assertThat<String>(id.id, `is`("w3c"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(W3C))
        assertThat<Product>(id.product, `is`(nullValue()))
        assertThat<Version>(id.productVersion, `is`(nullValue()))
    }

    @Test
    fun testItemId_W3C_Versions() {
        var id: VersionedProductId

        id = VersionedProductId("w3c/spec/v1ed")
        assertThat<String>(id.id, `is`("w3c/spec/v1ed"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(W3C))
        assertThat<Product>(id.product, `is`(W3C.SPECIFICATIONS))
        assertThat<Version>(id.productVersion, `is`<Version>(W3C.FIRST_EDITION))

        id = VersionedProductId("w3c/spec/v2ed")
        assertThat<String>(id.id, `is`("w3c/spec/v2ed"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(W3C))
        assertThat<Product>(id.product, `is`(W3C.SPECIFICATIONS))
        assertThat<Version>(id.productVersion, `is`<Version>(W3C.SECOND_EDITION))

        id = VersionedProductId("w3c/spec/v2.0")
        assertThat<String>(id.id, `is`("w3c/spec/v1ed"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(W3C))
        assertThat<Product>(id.product, `is`(W3C.SPECIFICATIONS))
        assertThat<Version>(id.productVersion, `is`<Version>(W3C.FIRST_EDITION))

        id = VersionedProductId("w3c/spec/2.0")
        assertThat<String>(id.id, `is`("w3c/spec/v1ed"))
        assertThat<Implementation>(id.vendor, `is`<Implementation>(W3C))
        assertThat<Product>(id.product, `is`(W3C.SPECIFICATIONS))
        assertThat<Version>(id.productVersion, `is`<Version>(W3C.FIRST_EDITION))
    }

    // endregion
}
