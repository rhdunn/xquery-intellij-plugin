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
package uk.co.reecedunn.intellij.plugin.xpm.intellij.annotation

import com.intellij.compat.lang.annotation.AnnotationHolder
import com.intellij.compat.testFramework.registerExtension
import com.intellij.compat.testFramework.registerExtensionPointBean
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration

interface XpmSemanticHighlighter {
    fun getHighlighting(element: PsiElement): TextAttributesKey

    fun getElementHighlighting(element: PsiElement): TextAttributesKey

    fun getQNamePrefixHighlighting(element: PsiElement): TextAttributesKey

    fun highlight(element: PsiElement, holder: AnnotationHolder)

    fun highlight(element: PsiElement, textAttributes: TextAttributesKey, holder: AnnotationHolder)

    companion object {
        val EP_NAME: ExtensionPointName<XpmSemanticHighlighterBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.semanticHighlighter"
        )

        val highlighters: Sequence<XpmSemanticHighlighter>
            get() = EP_NAME.extensionList.asSequence().map { it.getInstance() }

        @TestOnly
        @Suppress("UsePropertyAccessSyntax")
        fun register(plugin: PluginDescriptorProvider, factory: XpmSemanticHighlighter, fieldName: String = "INSTANCE") {
            val bean = XpmSemanticHighlighterBean()
            bean.implementationClass = factory.javaClass.name
            bean.fieldName = fieldName
            bean.setPluginDescriptor(plugin.pluginDescriptor)

            val app = ApplicationManager.getApplication()
            app.registerExtensionPointBean(EP_NAME, XpmSemanticHighlighterBean::class.java, plugin.pluginDisposable)
            app.registerExtension(EP_NAME, bean, plugin.pluginDisposable)
        }

        fun isXmlnsPrefix(element: PsiElement): Boolean {
            return (element.parent as? XpmNamespaceDeclaration)?.accepts(XdmNamespaceType.Prefixed) == true
        }
    }
}
