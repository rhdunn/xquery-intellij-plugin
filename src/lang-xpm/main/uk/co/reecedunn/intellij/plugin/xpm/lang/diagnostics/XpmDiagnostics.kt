/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics

import com.intellij.psi.PsiElement

interface XpmDiagnostics {
    fun error(element: PsiElement, code: String, description: String)

    fun warning(element: PsiElement, code: String, description: String)

    companion object {
        // It is a *static error* if an expression is not a valid instance of the grammar.
        const val XPST0003: String = "XPST0003"
    }
}
