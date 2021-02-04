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
package uk.co.reecedunn.intellij.plugin.xquery.intellij.execution.testframework

import com.intellij.execution.Location
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.io.URLUtil.SCHEME_SEPARATOR
import uk.co.reecedunn.intellij.plugin.processor.test.TestLocationProvider

object XQueryTestLocationProvider : SMTestLocator, TestLocationProvider {
    override fun getLocation(
        protocol: String,
        path: String,
        project: Project,
        scope: GlobalSearchScope
    ): MutableList<Location<PsiElement>> {
        return mutableListOf()
    }

    override fun locationHint(test: String, testsuite: String): String  {
        return when {
            testsuite.endsWith("/$test") -> "$PROTOCOL$SCHEME_SEPARATOR$testsuite"
            else -> "$PROTOCOL$SCHEME_SEPARATOR$testsuite#$test"
        }
    }

    private const val PROTOCOL = "xquery"
}
