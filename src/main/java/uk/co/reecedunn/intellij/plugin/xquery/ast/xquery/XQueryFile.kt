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
package uk.co.reecedunn.intellij.plugin.xquery.ast.xquery;

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.xquery.lang.Specification
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

data class XQueryVersionRef(val declaration: XQueryStringLiteral?, val version: Specification?) {
    fun getVersionOrDefault(project: Project): Specification {
        if (version == null) {
            val settings: XQueryProjectSettings = XQueryProjectSettings.getInstance(project)
            val product = settings.product
            val productVersion = settings.productVersion
            val xquery = settings.XQueryVersion
            if (product == null || productVersion == null || xquery == null)
                return XQuery.REC_1_0_20070123
            return XQuery.versionForXQuery(product, productVersion, xquery) ?: XQuery.REC_1_0_20070123
        }
        return version
    }
}

/**
 * An XQuery file.
 *
 * This interface is only implemented in the PSI tree as part of the XQuery
 * file implementation. It is included here to keep the interfaces for
 * navigating the parse trees together.
 */
interface XQueryFile : PsiFile {
    val XQueryVersion: XQueryVersionRef

    val modules: Sequence<XQueryModule>
}
