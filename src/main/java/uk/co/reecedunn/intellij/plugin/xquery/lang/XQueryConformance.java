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
package uk.co.reecedunn.intellij.plugin.xquery.lang;

import org.jetbrains.annotations.NotNull;

public enum XQueryConformance {
    FULL_AXIS("full-axis"), // XQuery 1.0
    FULL_TEXT("full-text"), // Full Text 1.0 - 3.0
    HIGHER_ORDER_FUNCTION("higher-order-function"), // XQuery 3.0 - 3.1
    MARKLOGIC("marklogic"), // MarkLogic 6.0 - 8.0
    MINIMAL_CONFORMANCE("minimal-conformance"), // XQuery 1.0 - 3.1
    MODULE("module"), // XQuery 1.0 - 3.1
    SAXON("saxon"), // Saxon 9.4 - 9.7
    SCHEMA_IMPORT("schema-import"), // XQuery 1.0; XQuery 3.0 - 3.1 ("Schema Aware")
    SCHEMA_VALIDATION("schema-validation"), // XQuery 1.0; XQuery 3.0 - 3.1 ("Schema Aware")
    SCRIPTING("scripting"), // Scripting 1.0
    SERIALIZATION("serialization"), // XQuery 1.0 - 3.1
    STATIC_TYPING("static-typing"), // XQuery 1.0 - 3.1
    TYPED_DATA("typed-data"), // XQuery 3.0 - 3.1
    UPDATE_FACILITY("update-facility"); // Update Facility 1.0 - 3.0

    private final String mID;

    XQueryConformance(@NotNull String id) {
        mID = id;
    }

    @Override
    public String toString() {
        return mID;
    }
}
