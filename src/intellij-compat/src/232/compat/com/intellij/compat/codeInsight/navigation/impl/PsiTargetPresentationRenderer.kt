// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.codeInsight.navigation.impl

import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.Nls
import javax.swing.Icon

@Suppress("UnstableApiUsage")
abstract class PsiTargetPresentationRenderer<T : PsiElement> : PsiElementListCellRenderer<T>() {
    @Nls
    abstract override fun getElementText(element: T): String

    override fun getContainerText(element: T?, name: String?): @NlsSafe String? = getContainerText(element!!)

    abstract fun getContainerText(element: T): String?

    abstract override fun getIcon(element: PsiElement): Icon?
}
