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

import java.util.HashMap;
import java.util.Map;

public enum XQueryVersion {
    UNSUPPORTED("unsupported", Double.MAX_VALUE),

    // region Specification Versions

    VERSION_1_0_20070123("1.0-20070123", 1.0, 20070123), // XQuery 1.0 REC (First Edition)
    VERSION_1_0_20101214("1.0-20101214", 1.0, 20101214), // XQuery 1.0 REC (Second Edition)
    VERSION_3_0_20140408("3.0-20140408", 3.0, 20140408), // XQuery 3.0 REC
    VERSION_3_1_20161213("3.1-20161213", 3.1, 20161213), // XQuery 3.1 CR

    // endregion
    // region Versions

    VERSION_0_9_MARKLOGIC("0.9-ml", 0.9), // XQuery Version
    VERSION_1_0("1.0", 1.0), // XQuery Version
    VERSION_1_0_MARKLOGIC("1.0-ml", 1.0), // XQuery Version
    VERSION_3_0("3.0", 3.0), // XQuery Version
    VERSION_3_1("3.1", 3.1), // XQuery Version
    VERSION_6_0("6.0", 6.0), // MarkLogic
    VERSION_7_0("7.0", 7.0), // MarkLogic
    VERSION_8_0("8.0", 8.0), // MarkLogic
    VERSION_8_4("8.4", 8.4), // BaseX
    VERSION_8_5("8.5", 8.5), // BaseX
    VERSION_9_0("9.0", 9.0), // MarkLogic
    VERSION_9_4("9.4", 9.4), // Saxon
    VERSION_9_5("9.5", 9.5), // Saxon
    VERSION_9_6("9.6", 9.6), // Saxon
    VERSION_9_7("9.7", 9.7); // Saxon

    // endregion

    private final String mID;
    private final double mValue;
    private final int mDate;

    XQueryVersion(@NotNull String id, double value, int date) {
        mID = id;
        mValue = value;
        mDate = date;
    }

    XQueryVersion(@NotNull String id, double value) {
        this(id, value, 0);
    }

    @NotNull
    public static XQueryVersion parse(@Nullable CharSequence value) {
        return sVersions.getOrDefault(value, UNSUPPORTED);
    }

    @Override
    public String toString() {
        return mID;
    }

    public double toDouble() {
        return mValue;
    }

    public int getDate() {
        return mDate;
    }

    public boolean supportsVersion(@NotNull XQueryVersion version) {
        return toDouble() >= version.toDouble();
    }

    private static Map<CharSequence, XQueryVersion> sVersions = new HashMap<>();

    static {
        sVersions.put("0.9-ml", VERSION_0_9_MARKLOGIC);
        sVersions.put("1.0", VERSION_1_0);
        sVersions.put("1.0-20070123", VERSION_1_0_20070123);
        sVersions.put("1.0-20101214", VERSION_1_0_20101214);
        sVersions.put("1.0-ml", VERSION_1_0_MARKLOGIC);
        sVersions.put("3.0", VERSION_3_0);
        sVersions.put("3.0-20140408", VERSION_3_0_20140408);
        sVersions.put("3.1", VERSION_3_1);
        sVersions.put("3.1-20161213", VERSION_3_1_20161213);
        sVersions.put("6.0", VERSION_6_0);
        sVersions.put("7.0", VERSION_7_0);
        sVersions.put("8.0", VERSION_8_0);
        sVersions.put("8.4", VERSION_8_4);
        sVersions.put("8.5", VERSION_8_5);
        sVersions.put("9.0", VERSION_9_0);
        sVersions.put("9.4", VERSION_9_4);
        sVersions.put("9.5", VERSION_9_5);
        sVersions.put("9.6", VERSION_9_6);
        sVersions.put("9.7", VERSION_9_7);
    }
}
