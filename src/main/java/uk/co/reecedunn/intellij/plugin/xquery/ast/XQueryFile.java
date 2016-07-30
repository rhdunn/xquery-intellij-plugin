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
package uk.co.reecedunn.intellij.plugin.xquery.ast;

import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

/**
 * An XQuery file.
 *
 * This interface is only implemented in the PSI tree as part of the XQuery
 * file implementation. It is included here to keep the interfaces for
 * navigating the parse trees together.
 */
public interface XQueryFile {
    /**
     * Gets the XQuery version of the file.
     *
     * If the file does not specify an XQuery version, this returns the XQuery
     * version set in the IntelliJ XQuery settings page.
     *
     * @return The detected XQuery version.
     */
    XQueryVersion getXQueryVersion();
}
