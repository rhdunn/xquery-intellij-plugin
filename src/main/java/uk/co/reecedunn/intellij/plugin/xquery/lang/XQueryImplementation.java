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

public enum XQueryImplementation {
    W3C("W3C", XQueryBundle.message("xquery.implementation.description.w3c"), "https://www.w3.org/XML/Query/", XQueryVersion.XQUERY_3_0),
    MARKLOGIC("MarkLogic", XQueryBundle.message("xquery.implementation.description.ml"), "https://docs.marklogic.com", XQueryVersion.XQUERY_1_0_MARKLOGIC);

    static {
        W3C.mVersions = new XQueryVersion[] { XQueryVersion.XQUERY_1_0, XQueryVersion.XQUERY_3_0, XQueryVersion.XQUERY_3_1 };
        MARKLOGIC.mVersions = new XQueryVersion[] { XQueryVersion.XQUERY_0_9_MARKLOGIC, XQueryVersion.XQUERY_1_0, XQueryVersion.XQUERY_1_0_MARKLOGIC };
    }

    private final String mID;
    private final String mName;
    private final String mReference;
    private final XQueryVersion mDefaultVersion;
    private XQueryVersion[] mVersions;

    XQueryImplementation(@NotNull String id, @NotNull String name, @NotNull String reference, @NotNull XQueryVersion defaultVersion) {
        mID = id;
        mName = name;
        mReference = reference;
        mDefaultVersion = defaultVersion;
    }

    @NotNull
    public String getName() {
        return mName;
    }

    @NotNull
    public String getReference() {
        return mReference;
    }

    @Nullable
    public static XQueryImplementation parse(@Nullable String value) {
        if ("W3C".equals(value)) return W3C;
        if ("MarkLogic".equals(value)) return MARKLOGIC;
        return null;
    }

    @NotNull
    @Override
    public String toString() {
        return mID;
    }

    @NotNull
    public XQueryVersion getDefaultVersion() {
        return mDefaultVersion;
    }

    @NotNull
    public XQueryVersion[] getVersions() {
        return mVersions;
    }
}
