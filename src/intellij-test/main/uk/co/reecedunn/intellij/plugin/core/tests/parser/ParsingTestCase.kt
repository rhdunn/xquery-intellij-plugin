// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
// Copyright 2000-2019 JetBrains s.r.o. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.OffsetMap
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.compat.openapi.startup.StartupManager
import com.intellij.compat.openapi.vfs.encoding.EncodingManagerImpl
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import com.intellij.lang.*
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext
import com.intellij.mock.MockFileDocumentManagerImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.impl.CoreCommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl
import com.intellij.openapi.options.SchemeManagerFactory
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.encoding.EncodingManager
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
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.TreeCopyHandler
import com.intellij.psi.util.CachedValuesManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.MockSchemeManagerFactory
import com.intellij.testFramework.utils.parameterInfo.MockUpdateParameterInfoContext
import com.intellij.util.CachedValuesManagerImpl
import com.intellij.util.messages.MessageBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.annotations.NonNls
import uk.co.reecedunn.intellij.plugin.core.psi.document
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.editor.MockEditorFactoryEx
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parameterInfo.MockCreateParameterInfoContext
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiDocumentManagerEx
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiManager
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import java.nio.charset.StandardCharsets

// NOTE: The IntelliJ ParsingTextCase implementation does not make it easy to
// customise the mock implementation, making it difficult to implement some tests.
@Suppress("SameParameterValue", "ReplaceNotNullAssertionWithElvisReturn")
abstract class ParsingTestCase<File : PsiFile>(val language: Language) : IdeaPlatformTestCase() {
    private var mFileFactory: PsiFileFactory? = null

    override fun registerServicesAndExtensions() {
        Registry.markAsLoaded()

        // IntelliJ ParsingTestCase setUp
        val app = ApplicationManager.getApplication()
        app.registerService(ProgressManager::class.java, ProgressManagerImpl())

        val psiManager = MockPsiManager(project)
        mFileFactory = PsiFileFactoryImpl(psiManager)
        app.registerService(MessageBus::class.java, app.messageBus)
        val editorFactory = MockEditorFactoryEx()
        app.registerService(EditorFactory::class.java, editorFactory)
        app.registerService(EncodingManager::class.java, EncodingManagerImpl(CoroutineScope(Dispatchers.IO)))
        app.registerService(CommandProcessor::class.java, CoreCommandProcessor())
        app.registerService(
            FileDocumentManager::class.java,
            MockFileDocumentManagerImpl(FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY) {
                editorFactory.createDocument(it)
            }
        )

        app.registerService(PsiBuilderFactory::class.java, PsiBuilderFactoryImpl())
        app.registerService(DefaultASTFactory::class.java, DefaultASTFactoryImpl())
        app.registerService(ReferenceProvidersRegistry::class.java, ReferenceProvidersRegistryImpl())
        project.registerService(PsiManager::class.java, psiManager)
        project.registerService(
            CachedValuesManager::class.java, CachedValuesManagerImpl(project, PsiCachedValuesFactory(project))
        )
        project.registerService(PsiDocumentManager::class.java, MockPsiDocumentManagerEx(project))
        project.registerService(PsiFileFactory::class.java, mFileFactory!!)
        project.registerService(StartupManager::class.java, StartupManager(project))
    }

    protected fun registerPsiModification() {
        val app = ApplicationManager.getApplication()

        app.registerExtensionPointBean(
            FileIndentOptionsProvider.EP_NAME, FileIndentOptionsProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(
            FileTypeIndentOptionsProvider.EP_NAME, FileTypeIndentOptionsProvider::class.java, pluginDisposable
        )

        app.registerExtensionPointBean(TreeCopyHandler.EP_NAME, TreeCopyHandler::class.java, pluginDisposable)
        app.registerService(IndentHelper::class.java, IndentHelperImpl())

        registerCodeSettingsService()
        registerCodeStyleSettingsManager()

        val schemeManagerFactory = MockSchemeManagerFactory()
        app.registerService(SchemeManagerFactory::class.java, schemeManagerFactory)
        app.registerService(CodeStyleSchemes::class.java, PersistableCodeStyleSchemes(schemeManagerFactory))
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

        app.registerService(AppCodeStyleSettingsManager::class.java, AppCodeStyleSettingsManager())
        project.registerService(
            ProjectCodeStyleSettingsManager::class.java,
            ProjectCodeStyleSettingsManager(project)
        )

        @Suppress("UnstableApiUsage")
        app.registerExtensionPointBean(
            CodeStyleSettingsModifier.EP_NAME, CodeStyleSettingsModifier::class.java, pluginDisposable
        )
    }

    // region IntelliJ ParsingTestCase Methods

    fun createVirtualFile(@NonNls name: String, text: String): VirtualFile {
        val file = LightVirtualFile(name, language!!, text)
        file.charset = StandardCharsets.UTF_8
        return file
    }

    @Suppress("UNCHECKED_CAST")
    fun parseText(text: String): File = createVirtualFile("testcase.xqy", text).toPsiFile(project) as File

    protected inline fun <reified T> parse(xquery: String): List<T> {
        return parseText(xquery).walkTree().filterIsInstance<T>().toList()
    }

    protected inline fun <reified T> parse(vararg xquery: String): List<T> {
        return parseText(xquery.joinToString("\n")).walkTree().filterIsInstance<T>().toList()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getEditor(file: PsiFile): Editor = EditorFactory.getInstance().createEditor(file.document!!)

    fun completion(text: String, completionPoint: String = "completion-point"): PsiElement {
        return parse<LeafPsiElement>(text).find { it.text == completionPoint }!!
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun handleInsert(text: String, char: Char, lookups: Array<LookupElement>, tailOffset: Int): InsertionContext {
        val file = parseText(text)
        val editor = getEditor(file)
        editor.caretModel.moveToOffset(tailOffset)

        val context = InsertionContext(OffsetMap(editor.document), char, lookups, file, editor, false)
        CommandProcessor.getInstance().executeCommand(null, {
            lookups.forEach { it.handleInsert(context) }
        }, null, null)
        return context
    }

    fun handleInsert(text: String, char: Char, lookup: LookupElement, tailOffset: Int): InsertionContext {
        return handleInsert(text, char, listOf(lookup).toTypedArray(), tailOffset)
    }

    fun createParameterInfoContext(text: String, offset: Int): CreateParameterInfoContext {
        val file = parseText(text)
        val editor = getEditor(file)
        editor.caretModel.moveToOffset(offset)
        return MockCreateParameterInfoContext(editor, file)
    }

    fun updateParameterInfoContext(text: String, offset: Int): UpdateParameterInfoContext {
        val file = parseText(text)
        val editor = getEditor(file)
        editor.caretModel.moveToOffset(offset)
        return MockUpdateParameterInfoContext(editor, file)
    }
}
