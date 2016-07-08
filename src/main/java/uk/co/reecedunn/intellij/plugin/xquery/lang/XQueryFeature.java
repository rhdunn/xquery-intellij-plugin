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
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;

public enum XQueryFeature {
    FN_PUT("fn:put", XQueryBundle.message("xquery.feature.name.fn-put")), // XQuery 3.1 (Optional Features)
    FULL_AXIS("full-axis", XQueryBundle.message("xquery.feature.name.full-axis")), // XQuery 1.0 (Optional Features)
    HIGHER_ORDER_FUNCTION("higher-order-function", XQueryBundle.message("xquery.feature.name.higher-order-function")), // XQuery 3.0 (Optional Features)
    MINIMAL_CONFORMANCE("minimal-conformance", XQueryBundle.message("xquery.feature.name.minimal-conformance")),
    MODULE("module", XQueryBundle.message("xquery.feature.name.module")), // XQuery 1.0 (Optional Features)
    SCHEMA_AWARE("schema-aware", XQueryBundle.message("xquery.feature.name.schema-aware")), // XQuery 3.0 (Optional Features) -- Schema Import + Schema Validation
    SCHEMA_IMPORT("schema-import", XQueryBundle.message("xquery.feature.name.schema-import")), // XQuery 1.0 (Optional Features)
    STATIC_TYPING("static-typing", XQueryBundle.message("xquery.feature.name.static-typing")), // XQuery 1.0 (Optional Features)
    SCHEMA_VALIDATION("schema-validation", XQueryBundle.message("xquery.feature.name.schema-validation")), // XQuery 1.0 (Optional Features)
    SERIALIZATION("serialization", XQueryBundle.message("xquery.feature.name.serialization")), // XQuery 1.0 (Optional Features)
    TYPED_DATA("typed-data", XQueryBundle.message("xquery.feature.name.typed-data")); // XQuery 3.0 (Optional Features)

    private final String mID;
    private final String mName;

    XQueryFeature(@NotNull String id, @NotNull String name) {
        mID = id;
        mName = name;
    }

    @NotNull
    public String getName() {
        return mName;
    }

    @Nullable
    public static XQueryFeature parse(@Nullable String value) {
        if ("fn:put".equals(value)) return FN_PUT;
        if ("full-axis".equals(value)) return FULL_AXIS;
        if ("higher-order-function".equals(value)) return HIGHER_ORDER_FUNCTION;
        if ("minimal-conformance".equals(value)) return MINIMAL_CONFORMANCE;
        if ("module".equals(value)) return MODULE;
        if ("schema-aware".equals(value)) return SCHEMA_AWARE;
        if ("schema-import".equals(value)) return SCHEMA_IMPORT;
        if ("schema-validation".equals(value)) return SCHEMA_VALIDATION;
        if ("serialization".equals(value)) return SERIALIZATION;
        if ("static-typing".equals(value)) return STATIC_TYPING;
        if ("typed-data".equals(value)) return TYPED_DATA;
        return null;
    }

    @NotNull
    @Override
    public String toString() {
        return mID;
    }
}
