// Copyright (C) 2016, 2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.psi

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Segment
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPsiElementPointer

class MockSmartPsiElementPointer<E : PsiElement>(private val mElement: E, private val mContainingFile: PsiFile) :
    SmartPsiElementPointer<E> {

    override fun getElement(): E = mElement

    override fun getContainingFile(): PsiFile = mContainingFile

    override fun getProject(): Project = mContainingFile.project

    override fun getVirtualFile(): VirtualFile? = null

    override fun getRange(): Segment? = null

    override fun getPsiRange(): Segment? = null
}
