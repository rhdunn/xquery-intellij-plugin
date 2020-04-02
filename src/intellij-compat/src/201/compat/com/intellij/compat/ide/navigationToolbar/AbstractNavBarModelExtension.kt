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
package com.intellij.compat.ide.navigationToolbar

import com.intellij.ide.navigationToolbar.AbstractNavBarModelExtension
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.PsiElement
import com.intellij.util.Processor

abstract class AbstractNavBarModelExtension : AbstractNavBarModelExtension() {
    abstract override fun getPresentableText(`object`: Any?): String?

    open fun getPresentableText(`object`: Any?, forPopup: Boolean): String? = getPresentableText(`object`)

    open fun getLeafElement(dataContext: DataContext): PsiElement? = null

    override fun processChildren(`object`: Any?, rootElement: Any?, processor: Processor<Any?>?): Boolean {
        return true
    }

    open fun normalizeChildren(): Boolean = true
}
