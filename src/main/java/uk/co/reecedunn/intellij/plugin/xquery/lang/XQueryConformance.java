/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
    MINIMAL_CONFORMANCE("xquery"), // XQuery 1.0 - 3.1

    BASEX("basex"), // BaseX 8.4 - 8.5
    MARKLOGIC("marklogic"), // MarkLogic 6.0 - 8.0
    SAXON("saxon"), // Saxon 9.4 - 9.7

    FULL_TEXT("full-text"), // Full Text 1.0 - 3.0
    SCRIPTING("scripting"), // Scripting 1.0
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
