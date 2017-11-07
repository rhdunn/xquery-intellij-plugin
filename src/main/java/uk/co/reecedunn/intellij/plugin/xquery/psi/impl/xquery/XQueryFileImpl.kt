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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.core.extensions.descendants
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.filetypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.lang.Specification
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

class XQueryFileImpl(provider: FileViewProvider) : PsiFileBase(provider, XQuery), XQueryFile {
    override fun getFileType(): FileType {
        return XQueryFileType.INSTANCE
    }

    override val XQueryVersion get(): XQueryVersionRef =
        modules.firstOrNull()?.XQueryVersion ?: XQueryVersionRef(null, null)

    override val modules get(): Sequence<XQueryModule> =
        children().filterIsInstance<XQueryModule>()

    override fun toString(): String =
        "XQueryFile(" + containingFile.name + ")"
}
