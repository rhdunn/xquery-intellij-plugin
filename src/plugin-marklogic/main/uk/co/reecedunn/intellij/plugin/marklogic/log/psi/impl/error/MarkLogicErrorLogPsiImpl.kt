/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.log.psi.impl.error

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import uk.co.reecedunn.intellij.plugin.marklogic.log.fileTypes.MarkLogicErrorLogFileType
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.MarkLogicErrorLog

class MarkLogicErrorLogPsiImpl(provider: FileViewProvider) :
    PsiFileBase(provider, MarkLogicErrorLog),
    uk.co.reecedunn.intellij.plugin.marklogic.log.ast.error.MarkLogicErrorLog {
    // region Object

    override fun toString(): String = "MarkLogicErrorLog(" + containingFile.name + ")"

    // endregion
    // region PsiFile

    override fun getFileType(): FileType = MarkLogicErrorLogFileType

    // endregion
}
