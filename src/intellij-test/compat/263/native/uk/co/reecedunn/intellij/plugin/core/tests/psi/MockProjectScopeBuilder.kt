// Copyright (C) 2021, 2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.psi

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScopeBuilder

class MockProjectScopeBuilder : ProjectScopeBuilder() {
    override fun buildEverythingScope(): GlobalSearchScope = GlobalSearchScope.EMPTY_SCOPE

    override fun buildLibrariesScope(): GlobalSearchScope = GlobalSearchScope.EMPTY_SCOPE

    override fun buildLibraryClassesScope(): GlobalSearchScope = GlobalSearchScope.EMPTY_SCOPE

    override fun buildAllScope(): GlobalSearchScope = GlobalSearchScope.EMPTY_SCOPE

    override fun buildProjectScope(): GlobalSearchScope = GlobalSearchScope.EMPTY_SCOPE

    override fun buildContentScope(): GlobalSearchScope = GlobalSearchScope.EMPTY_SCOPE
}
