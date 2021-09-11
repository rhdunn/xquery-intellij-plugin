/*
 * Copyright (C) 2016-2021 Reece H. Dunn
 * Copyright 2000-2019 JetBrains s.r.o.
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
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.OffsetMap
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.compat.mock.MockFileDocumentManagerImpl
import com.intellij.compat.testFramework.registerExtensionPointBean
import com.intellij.compat.testFramework.registerServiceInstance
import com.intellij.compat.util.registry.initializeRegistryForTests
import com.intellij.ide.startup.impl.StartupManagerImpl
import com.intellij.lang.*
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext
import com.intellij.mock.MockFileTypeManager
import com.intellij.mock.MockLanguageFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.impl.CoreCommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.options.SchemeManagerFactory
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.encoding.EncodingManager
import com.intellij.openapi.vfs.encoding.EncodingManagerImpl
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
abstract class ParsingTestCase<File : PsiFile>(
    private var mFileExt: String?,
    vararg definitions: ParserDefinition
) : IdeaPlatformTestCase() {
    constructor(fileExt: String?, language: Language) : this(fileExt) {
        this.language = language
    }

    private var mFileFactory: PsiFileFactory? = null

    var language: Language? = null
        private set

    private val mDefinitions: Array<out ParserDefinition> = definitions

    @Suppress("UnstableApiUsage")
    override fun registerServicesAndExtensions() {
        initializeRegistryForTests()

        // IntelliJ ParsingTestCase setUp
        val app = ApplicationManager.getApplication()
        app.registerServiceInstance(ProgressManager::class.java, ProgressManagerImpl())

        val psiManager = MockPsiManager(project)
        mFileFactory = PsiFileFactoryImpl(psiManager)
        app.registerServiceInstance(MessageBus::class.java, app.messageBus)
        val editorFactory = MockEditorFactoryEx()
        app.registerServiceInstance(EditorFactory::class.java, editorFactory)
        app.registerServiceInstance(EncodingManager::class.java, EncodingManagerImpl())
        app.registerServiceInstance(CommandProcessor::class.java, CoreCommandProcessor())
        app.registerServiceInstance(
            FileDocumentManager::class.java,
            MockFileDocumentManagerImpl(FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY) {
                editorFactory.createDocument(it)
            }
        )

        app.registerServiceInstance(PsiBuilderFactory::class.java, PsiBuilderFactoryImpl())
        app.registerServiceInstance(DefaultASTFactory::class.java, DefaultASTFactoryImpl())
        app.registerServiceInstance(ReferenceProvidersRegistry::class.java, ReferenceProvidersRegistryImpl())
        project.registerServiceInstance(PsiManager::class.java, psiManager)
        project.registerServiceInstance(
            CachedValuesManager::class.java, CachedValuesManagerImpl(project, PsiCachedValuesFactory(project))
        )
        project.registerServiceInstance(PsiDocumentManager::class.java, MockPsiDocumentManagerEx(project))
        project.registerServiceInstance(PsiFileFactory::class.java, mFileFactory!!)
        project.registerServiceInstance(StartupManager::class.java, StartupManagerImpl(project))

        for (definition in mDefinitions) {
            addExplicitExtension(LanguageParserDefinitions.INSTANCE, definition.fileNodeType.language, definition)
        }

        if (mDefinitions.isNotEmpty()) {
            configureFromParserDefinition(mDefinitions[0], mFileExt)
        }

        if (language != null) {
            val fileType =
                if (language?.id == "XML")
                    loadFileTypeSafe("com.intellij.ide.highlighter.XmlFileType", "XML")
                else
                    MockLanguageFileType(language!!, mFileExt)
            app.registerServiceInstance(FileTypeManager::class.java, MockFileTypeManager(fileType))
        }
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
        app.registerServiceInstance(IndentHelper::class.java, IndentHelperImpl())

        registerCodeSettingsService()
        registerCodeStyleSettingsManager()

        val schemeManagerFactory = MockSchemeManagerFactory()
        app.registerServiceInstance(SchemeManagerFactory::class.java, schemeManagerFactory)
        app.registerServiceInstance(CodeStyleSchemes::class.java, PersistableCodeStyleSchemes(schemeManagerFactory))
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerCodeSettingsService() {
        try {
            val service = Class.forName("com.intellij.psi.codeStyle.CodeStyleSettingsService") as Class<Any>
            val `class` = Class.forName("com.intellij.psi.codeStyle.CodeStyleSettingsServiceImpl") as Class<Any>
            val `object` = `class`.getConstructor().newInstance()
            ApplicationManager.getApplication().registerServiceInstance(service, `object`)
        } catch (e: ClassNotFoundException) {
        }
    }

    private fun registerCodeStyleSettingsManager() {
        val app = ApplicationManager.getApplication()

        app.registerExtensionPointBean(
            CodeStyleSettingsProvider.EXTENSION_POINT_NAME, CodeStyleSettingsProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(
            LanguageCodeStyleSettingsProvider.EP_NAME, LanguageCodeStyleSettingsProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(
            FileCodeStyleProvider.EP_NAME, FileCodeStyleProvider::class.java, pluginDisposable
        )

        app.registerServiceInstance(AppCodeStyleSettingsManager::class.java, AppCodeStyleSettingsManager())
        project.registerServiceInstance(
            ProjectCodeStyleSettingsManager::class.java,
            ProjectCodeStyleSettingsManager(project)
        )

        @Suppress("UnstableApiUsage")
        app.registerExtensionPointBean(
            CodeStyleSettingsModifier.EP_NAME, CodeStyleSettingsModifier::class.java, pluginDisposable
        )
    }

    private fun configureFromParserDefinition(definition: ParserDefinition, extension: String?) {
        language = definition.fileNodeType.language
        mFileExt = extension
        addExplicitExtension(LanguageParserDefinitions.INSTANCE, language!!, definition)
    }

    private fun loadFileTypeSafe(className: String, fileTypeName: String): FileType {
        return try {
            Class.forName(className).getField("INSTANCE").get(null) as FileType
        } catch (ignored: Exception) {
            MockLanguageFileType(PlainTextLanguage.INSTANCE, fileTypeName.toLowerCase())
        }

    }

    // region IntelliJ ParsingTestCase Methods

    protected fun <T> addExplicitExtension(instance: LanguageExtension<T>, language: Language, `object`: T) {
        instance.addExplicitExtension(language, `object`!!)
        Disposer.register(project, com.intellij.openapi.Disposable {
            instance.removeExplicitExtension(language, `object`)
        })
    }

    fun createVirtualFile(@NonNls name: String, text: String): VirtualFile {
        val file = LightVirtualFile(name, language!!, text)
        file.charset = StandardCharsets.UTF_8
        return file
    }

    fun getFileViewProvider(project: Project, file: VirtualFile, physical: Boolean): FileViewProvider {
        val manager = PsiManager.getInstance(project)
        val factory = LanguageFileViewProviders.INSTANCE.forLanguage(language!!)
        var viewProvider: FileViewProvider? = factory?.createFileViewProvider(file, language, manager, physical)
        if (viewProvider == null) viewProvider = SingleRootFileViewProvider(manager, file, physical)
        return viewProvider
    }

    @Suppress("UNCHECKED_CAST")
    fun parseText(text: String): File = createVirtualFile("testcase.xqy", text).toPsiFile(project) as File

    protected inline fun <reified T> parse(text: String): List<T> {
        return parseText(text).walkTree().filterIsInstance<T>().toList()
    }

    protected inline fun <reified T> parse(vararg text: String): List<T> {
        return parseText(text.joinToString("\n")).walkTree().filterIsInstance<T>().toList()
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
