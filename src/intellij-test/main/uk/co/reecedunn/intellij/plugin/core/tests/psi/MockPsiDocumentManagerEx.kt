/*
 * Copyright (C) 2017, 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.impl.PsiDocumentManagerBase

// NOTE: PSI document modifications rely on PsiDocumentManagerBase, which
// MockPsiDocumentManager does not derive from.
class MockPsiDocumentManagerEx(project: Project) : PsiDocumentManagerBase(project) {
    override fun commitAllDocumentsUnderProgress(): Boolean = true
}
