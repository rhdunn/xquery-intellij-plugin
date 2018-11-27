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
package uk.co.reecedunn.intellij.plugin.intellij.lang

import com.intellij.psi.PsiElement

interface VersionConformance {
    /**
     * Gets the list of specifications or product versions that this construct conforms to.
     *
     * This can be one of the following:
     *   -  A specification version (e.g. Update Facility 1.0) -- this requires the XQuery
     *      version matching the specification;
     *   -  A product version for a vendor extension -- this requires any XQuery version;
     *   -  A `MarkLogic` product version for MarkLogic vendor extensions -- this requires
     *      XQuery version `1.0-ml`;
     *   -  `XQuery.MARKLOGIC_0_9` -- this requires XQuery version `0.9-ml`.
     *
     * __NOTE:__ MarkLogic vendor extensions should not use `XQuery.MARKLOGIC_1_0`. They
     * should use one of the `MarkLogic` versions (e.g. `MarkLogic.VERSION_6_0`). This is
     * so that these extensions can report the correct MarkLogic version required for that
     * extension when the `1.0-ml` XQuery version is used.
     */
    val requiresConformance: List<Version>

    /**
     * Gets the element on which to report any conformance errors.
     *
     * @return The element to report errors on.
     */
    val conformanceElement: PsiElement
}

interface VersionConformanceName {
    val conformanceName: String?
}
