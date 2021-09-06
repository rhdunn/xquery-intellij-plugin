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
package uk.co.reecedunn.intellij.plugin.xpath.lang.refactoring

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement

class XPathRefactoringSupportProvider : RefactoringSupportProvider() {
    /**
     * Determines whether VariableInplaceRenameHandler should be used.
     */
    override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
        return false
    }

    /**
     * Determines whether MemberInplaceRenameHandler should be used.
     */
    override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
        return false
    }

    /**
     * Determines whether SafeDeleteProcessor should be used.
     */
    override fun isSafeDeleteAvailable(element: PsiElement): Boolean {
        return false
    }
}
