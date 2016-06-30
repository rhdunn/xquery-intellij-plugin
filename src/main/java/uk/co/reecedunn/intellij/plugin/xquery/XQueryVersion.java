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
package uk.co.reecedunn.intellij.plugin.xquery;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum XQueryVersion {
    XQUERY_0_9_MARKLOGIC("0.9-ml", "MarkLogic 3.2 Compatibility Dialect", "https://docs.marklogic.com/guide/xquery/dialects#id_65735"),
    XQUERY_1_0("1.0", "W3C Recommendation 14 December 2010", "https://www.w3.org/TR/2010/REC-xquery-20101214"),
    XQUERY_1_0_MARKLOGIC("1.0-ml", "MarkLogic Dialect", "https://docs.marklogic.com/guide/xquery/dialects#id_63368"),
    XQUERY_3_0("3.0", "W3C Recommendation 08 April 2014", "https://www.w3.org/TR/2014/REC-xquery-30-20140408"),
    XQUERY_3_1("3.1", "W3C Candidate Recommendation 17 December 2015", "https://www.w3.org/TR/2015/CR-xquery-31-20151217");

    private final String mName;
    private final String mDescription;
    private final String mReference;

    XQueryVersion(@NotNull String name, @NotNull String description, @NotNull String reference) {
        mName = name;
        mDescription = description;
        mReference = reference;
    }

    @NotNull
    public String getName() {
        return mName;
    }

    @NotNull
    public String getDescription() {
        return mDescription;
    }

    @NotNull
    public String getReference() {
        return mReference;
    }

    @Nullable
    public static XQueryVersion parse(@Nullable String value) {
        if ("0.9-ml".equals(value)) return XQUERY_0_9_MARKLOGIC;
        if ("1.0".equals(value)) return XQUERY_1_0;
        if ("1.0-ml".equals(value)) return XQUERY_1_0_MARKLOGIC;
        if ("3.0".equals(value)) return XQUERY_3_0;
        if ("3.1".equals(value)) return XQUERY_3_1;
        return null;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
