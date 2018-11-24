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
package uk.co.reecedunn.intellij.plugin.core.tests.psi

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Segment
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPsiElementPointer

class MockSmartPsiElementPointer<E : PsiElement>(private val mElement: E, private val mContainingFile: PsiFile):
        SmartPsiElementPointer<E> {

    override fun getElement(): E? = mElement

    override fun getContainingFile(): PsiFile? = mContainingFile

    override fun getProject(): Project = mContainingFile.project

    override fun getVirtualFile(): VirtualFile? = null

    override fun getRange(): Segment? = null

    override fun getPsiRange(): Segment? = null
}
