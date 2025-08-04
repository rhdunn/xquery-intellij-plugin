// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
// Copyright 2000-2019 JetBrains s.r.o. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import com.intellij.compat.openapi.startup.StartupManager
import com.intellij.compat.openapi.vfs.encoding.EncodingManagerImpl
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import com.intellij.lang.*
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.mock.MockFileDocumentManagerImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.impl.CoreCommandProcessor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl
import com.intellij.openapi.options.SchemeManagerFactory
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.encoding.EncodingManager
import com.intellij.pom.PomModel
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.*
import com.intellij.psi.codeStyle.*
import com.intellij.psi.codeStyle.modifier.CodeStyleSettingsModifier
import com.intellij.psi.impl.PsiCachedValuesFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.impl.source.codeStyle.IndentHelper
import com.intellij.psi.impl.source.codeStyle.IndentHelperImpl
import com.intellij.psi.impl.source.codeStyle.PersistableCodeStyleSchemes
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistryImpl
import com.intellij.psi.impl.source.tree.TreeCopyHandler
import com.intellij.psi.util.CachedValuesManager
import com.intellij.testFramework.MockSchemeManagerFactory
import com.intellij.util.CachedValuesManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uk.co.reecedunn.intellij.plugin.core.tests.editor.EditorTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.editor.MockEditorFactoryEx
import uk.co.reecedunn.intellij.plugin.core.tests.injecton.MockInjectedLanguageManager
import uk.co.reecedunn.intellij.plugin.core.tests.pom.core.MockPomModel
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiDocumentManagerEx
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiManager
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase

// NOTE: The IntelliJ ParsingTextCase implementation does not make it easy to
// customise the mock implementation, making it difficult to implement some tests.
@Suppress("SameParameterValue", "ReplaceNotNullAssertionWithElvisReturn")
abstract class ParsingTestCase<File : PsiFile>(
    override val language: Language
) : IdeaPlatformTestCase(), LanguageParserTestCase<File>, EditorTestCase {
    private var mFileFactory: PsiFileFactory? = null

    override fun registerServicesAndExtensions() {
        Registry.markAsLoaded()

        // IntelliJ ParsingTestCase setUp
        val app = ApplicationManager.getApplication()
        app.registerService<ProgressManager>(ProgressManagerImpl())

        val psiManager = MockPsiManager(project)
        mFileFactory = PsiFileFactoryImpl(psiManager)
        app.registerService(app.messageBus)
        val editorFactory = MockEditorFactoryEx()
        app.registerService<EditorFactory>(editorFactory)
        app.registerService<EncodingManager>(EncodingManagerImpl(CoroutineScope(Dispatchers.IO)))
        app.registerService<CommandProcessor>(CoreCommandProcessor())
        app.registerService<FileDocumentManager>(MockFileDocumentManagerImpl(FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY) {
            editorFactory.createDocument(it)
        })

        app.registerService<PsiBuilderFactory>(PsiBuilderFactoryImpl())
        app.registerService<DefaultASTFactory>(DefaultASTFactoryImpl())
        app.registerService<ReferenceProvidersRegistry>(ReferenceProvidersRegistryImpl())

        project.registerService<PsiManager>(psiManager)
        project.registerService<CachedValuesManager>(CachedValuesManagerImpl(project, PsiCachedValuesFactory(project)))
        project.registerService<PsiDocumentManager>(MockPsiDocumentManagerEx(project))
        project.registerService<PsiFileFactory>(mFileFactory!!)
        project.registerService(StartupManager(project))
    }

    protected fun registerPsiModification() {
        val app = ApplicationManager.getApplication()

        project.registerService(TreeAspect())
        project.registerService<PomModel>(MockPomModel(project))

        app.registerExtensionPointBean(
            FileIndentOptionsProvider.EP_NAME, FileIndentOptionsProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(
            FileTypeIndentOptionsProvider.EP_NAME, FileTypeIndentOptionsProvider::class.java, pluginDisposable
        )

        app.registerExtensionPointBean(TreeCopyHandler.EP_NAME, TreeCopyHandler::class.java, pluginDisposable)
        app.registerService<IndentHelper>(IndentHelperImpl())

        registerCodeSettingsService()
        registerCodeStyleSettingsManager()

        project.registerService<InjectedLanguageManager>(MockInjectedLanguageManager())

        val schemeManagerFactory = MockSchemeManagerFactory()
        app.registerService<SchemeManagerFactory>(schemeManagerFactory)
        app.registerService<CodeStyleSchemes>(PersistableCodeStyleSchemes(schemeManagerFactory))
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerCodeSettingsService() {
        try {
            val service = Class.forName("com.intellij.psi.codeStyle.CodeStyleSettingsService") as Class<Any>
            val `class` = Class.forName("com.intellij.psi.codeStyle.CodeStyleSettingsServiceImpl") as Class<Any>
            val `object` = `class`.getConstructor().newInstance()
            ApplicationManager.getApplication().registerService(service, `object`)
        } catch (e: ClassNotFoundException) {
        }
    }

    private fun registerCodeStyleSettingsManager() {
        val app = ApplicationManager.getApplication()

        app.registerExtensionPointBean(
            CodeStyleSettingsProvider.EXTENSION_POINT_NAME, CodeStyleSettingsProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(
            "com.intellij.langCodeStyleSettingsContributor",
            "com.intellij.psi.codeStyle.LanguageCodeStyleSettingsContributor",
            pluginDisposable
        )
        app.registerExtensionPointBean(
            LanguageCodeStyleSettingsProvider.EP_NAME, LanguageCodeStyleSettingsProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(
            FileCodeStyleProvider.EP_NAME, FileCodeStyleProvider::class.java, pluginDisposable
        )

        app.registerService(AppCodeStyleSettingsManager())
        project.registerService(ProjectCodeStyleSettingsManager(project))

        @Suppress("UnstableApiUsage")
        app.registerExtensionPointBean(
            CodeStyleSettingsModifier.EP_NAME, CodeStyleSettingsModifier::class.java, pluginDisposable
        )
    }
}
