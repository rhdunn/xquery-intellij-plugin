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
    // region XQuery Conformance / Optional Features

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
    // region Conforms To

    public void testBaseX_ConformsTo() {
        for (Product product : BaseX.INSTANCE.getProducts()) {
            for (Version version : BaseX.INSTANCE.getVersions()) {
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20070123()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20101214()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_0_20140408()), is(true));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getCR_3_1_20151217()), is(version.getValue() <= 8.5));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_1_20170321()), is(version.getValue() >= 8.6));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_0_9()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_1_0()), is(false));

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
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20070123()), is(true));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20101214()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_0_20140408()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getCR_3_1_20151217()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_1_20170321()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_0_9()), is(true));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_1_0()), is(true));

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
                // region Specification: XQuery

                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20070123()), is(version.getValue() == 1.0));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_1_0_20101214()), is(version.getValue() == 2.0));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_0_20140408()), is(version.getValue() == 1.0));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getCR_3_1_20151217()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getREC_3_1_20170321()), is(version.getValue() == 1.0));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_0_9()), is(false));
                assertThat(product.conformsTo(version, XQuery.INSTANCE.getMARKLOGIC_1_0()), is(false));

                // endregion
                // region Specification: XQuery Update Facility

                assertThat(product.conformsTo(version, UpdateFacility.INSTANCE.getREC_1_0_20110317()), is(version.getValue() == 1.0));
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
    // region XQuery Versions

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
}
