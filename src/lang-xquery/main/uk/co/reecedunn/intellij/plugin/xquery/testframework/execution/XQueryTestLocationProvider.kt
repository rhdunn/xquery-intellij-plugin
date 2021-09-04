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
package uk.co.reecedunn.intellij.plugin.xquery.testframework.execution

import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.io.URLUtil.SCHEME_SEPARATOR
import uk.co.reecedunn.intellij.plugin.processor.test.TestLocationProvider
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_parse
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryFunctionProvider

object XQueryTestLocationProvider : SMTestLocator, TestLocationProvider {
    override fun getLocation(
        protocol: String,
        path: String,
        project: Project,
        scope: GlobalSearchScope
    ): MutableList<Location<PsiElement>> {
        val parts = path.split('#')
        val module = getModule(project, parts[0])
        return when (parts.size) {
            0, 1 -> module?.let { mutableListOf<Location<PsiElement>>(PsiLocation(it)) } ?: mutableListOf()
            else -> getFunctions(module, qname_parse(parts[1], mapOf())).mapNotNullTo(mutableListOf()) {
                @Suppress("UNCHECKED_CAST")
                PsiLocation(it.functionName?.element) as Location<PsiElement>
            }
        }
    }

    private fun getModule(project: Project, path: String): PsiFile? {
        val location = XpmModuleLocationPath.create(project, path, XdmModuleType.MODULE) ?: return null
        return XpmModuleLoaderSettings.getInstance(project).resolve(location, null) as? PsiFile
    }

    private fun getFunctions(module: PsiFile?, qname: XsQNameValue): Sequence<XpmFunctionDeclaration> {
        if (module == null) return emptySequence()
        return XQueryFunctionProvider.declaredFunctions(module).filter {
            it.functionName?.localName?.data == qname.localName?.data
        }
    }

    override fun locationHint(testsuite: String): String = "$PROTOCOL$SCHEME_SEPARATOR$testsuite"

    override fun locationHint(test: String, testsuite: String): String = "$PROTOCOL$SCHEME_SEPARATOR$testsuite#$test"

    private const val PROTOCOL = "xquery"
}
