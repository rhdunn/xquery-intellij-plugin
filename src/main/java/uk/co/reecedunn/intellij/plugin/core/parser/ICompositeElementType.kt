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
package uk.co.reecedunn.intellij.plugin.core.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

class ICompositeElementType(@NonNls debugName: String, psiClass: Class<*>, language: Language):
        IElementType(debugName, language) {

    private var mPsiConstructor: Constructor<*>? = null

    init {
        try {
            mPsiConstructor = psiClass.getConstructor(ASTNode::class.java)
        } catch (e: NoSuchMethodException) {
            throw AssertionError("Cannot find the appropriate constructor for " + psiClass.name)
        }
    }

    fun createPsiElement(node: ASTNode): PsiElement {
        try {
            val arguments = arrayOf<Any>(node)
            return mPsiConstructor!!.newInstance(*arguments) as PsiElement
        } catch (e: InvocationTargetException) {
            val cause = e.cause
            if (cause is ProcessCanceledException) {
                throw cause
            }
            throw AssertionError("Cannot create XQuery PsiElement for " + this, cause)
        } catch (e: Exception) {
            throw AssertionError("Cannot create XQuery PsiElement for " + this, e)
        }
    }
}