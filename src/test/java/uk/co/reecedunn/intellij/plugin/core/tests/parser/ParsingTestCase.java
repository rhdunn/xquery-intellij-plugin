/*
 * Copyright (C) 2016 Reece H. Dunn
 * Copyright 2000-2016 JetBrains s.r.o.
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
package uk.co.reecedunn.intellij.plugin.core.tests.parser;

import com.intellij.ide.startup.impl.StartupManagerImpl;
import com.intellij.lang.*;
import com.intellij.lang.impl.PsiBuilderFactoryImpl;
import com.intellij.mock.*;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.PomModel;
import com.intellij.pom.core.impl.PomModelImpl;
import com.intellij.pom.tree.TreeAspect;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiCachedValuesFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistryImpl;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.PlatformLiteFixture;
import com.intellij.util.CachedValuesManagerImpl;
import com.intellij.util.messages.MessageBus;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.*;
import org.picocontainer.defaults.AbstractComponentAdapter;
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiManager;
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.ResourceVirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

// NOTE: The IntelliJ ParsingTextCase implementation does not make it easy to
// customise the mock implementation, making it difficult to implement some tests.
public abstract class ParsingTestCase<File extends PsiFile> extends PlatformLiteFixture {
    private PsiFileFactory mFileFactory;
    private Language mLanguage;
    private String mFileExt;
    private final ParserDefinition[] mDefinitions;

    public ParsingTestCase(String fileExt, ParserDefinition... definitions) {
        mFileExt = fileExt;
        mDefinitions = definitions;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // IntelliJ ParsingTestCase setUp
        initApplication();
        ComponentAdapter component = getApplication().getPicoContainer().getComponentAdapter(ProgressManager.class.getName());
        if (component == null) {
            getApplication().getPicoContainer().registerComponent(new AbstractComponentAdapter(ProgressManager.class.getName(), Object.class) {
                @Override
                public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
                    return new ProgressManagerImpl();
                }

                @Override
                public void verify(PicoContainer container) throws PicoIntrospectionException {
                }
            });
        }
        Extensions.registerAreaClass("IDEA_PROJECT", null);
        myProject = new MockProjectEx(getTestRootDisposable());
        final PsiManager psiManager = new MockPsiManager(myProject);
        mFileFactory = new PsiFileFactoryImpl(psiManager);
        MutablePicoContainer appContainer = getApplication().getPicoContainer();
        registerComponentInstance(appContainer, MessageBus.class, getApplication().getMessageBus());
        final MockEditorFactory editorFactory = new MockEditorFactory();
        registerComponentInstance(appContainer, EditorFactory.class, editorFactory);
        registerComponentInstance(appContainer, FileDocumentManager.class, new MockFileDocumentManagerImpl(
                                  editorFactory::createDocument, FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY));
        registerComponentInstance(appContainer, PsiDocumentManager.class, new MockPsiDocumentManager());

        registerApplicationService(PsiBuilderFactory.class, new PsiBuilderFactoryImpl());
        registerApplicationService(DefaultASTFactory.class, new DefaultASTFactoryImpl());
        registerApplicationService(ReferenceProvidersRegistry.class, new ReferenceProvidersRegistryImpl());
        myProject.registerService(CachedValuesManager.class, new CachedValuesManagerImpl(myProject, new PsiCachedValuesFactory(psiManager)));
        myProject.registerService(PsiManager.class, psiManager);
        myProject.registerService(PsiFileFactory.class, mFileFactory);
        myProject.registerService(StartupManager.class, new StartupManagerImpl(myProject));
        registerExtensionPoint(FileTypeFactory.FILE_TYPE_FACTORY_EP, FileTypeFactory.class);

        for (ParserDefinition definition : mDefinitions) {
            addExplicitExtension(LanguageParserDefinitions.INSTANCE, definition.getFileNodeType().getLanguage(), definition);
        }
        if (mDefinitions.length > 0) {
            configureFromParserDefinition(mDefinitions[0], mFileExt);
        }

        // That's for reparse routines
        final PomModelImpl pomModel = new PomModelImpl(myProject);
        myProject.registerService(PomModel.class, pomModel);
        new TreeAspect(pomModel);
    }

    // region IntelliJ ParsingTestCase Methods

    private void configureFromParserDefinition(ParserDefinition definition, String extension) {
        mLanguage = definition.getFileNodeType().getLanguage();
        mFileExt = extension;
        addExplicitExtension(LanguageParserDefinitions.INSTANCE, mLanguage, definition);
        registerComponentInstance(getApplication().getPicoContainer(), FileTypeManager.class,
                                  new MockFileTypeManager(new MockLanguageFileType(mLanguage, mFileExt)));
    }

    protected <T> void addExplicitExtension(final LanguageExtension<T> instance, final Language language, final T object) {
        instance.addExplicitExtension(language, object);
        Disposer.register(myProject, () -> instance.removeExplicitExtension(language, object));
    }

    @Override
    protected <T> void registerExtensionPoint(final ExtensionPointName<T> extensionPointName, Class<T> aClass) {
        super.registerExtensionPoint(extensionPointName, aClass);
        Disposer.register(myProject, () -> Extensions.getRootArea().unregisterExtensionPoint(extensionPointName.getName()));
    }

    protected <T> void registerApplicationService(final Class<T> aClass, T object) {
        getApplication().registerService(aClass, object);
        Disposer.register(myProject, () -> getApplication().getPicoContainer().unregisterComponent(aClass.getName()));
    }

    // endregion

    public Language getLanguage() {
        return mLanguage;
    }

    public String streamToString(InputStream stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtil.copyCompletely(new InputStreamReader(stream), writer);
        return writer.toString();
    }

    public String loadResource(String resource) {
        ClassLoader loader = ParsingTestCase.class.getClassLoader();
        try {
            return streamToString(loader.getResourceAsStream(resource));
        } catch (IOException e) {
            return null;
        }
    }

    public VirtualFile createVirtualFile(@NonNls String name, String text) {
        VirtualFile file = new LightVirtualFile(name, getLanguage(), text);
        file.setCharset(CharsetToolkit.UTF8_CHARSET);
        return file;
    }

    @SuppressWarnings("SameParameterValue")
    public FileViewProvider getFileViewProvider(@NotNull Project project, VirtualFile file, boolean physical) {
        final PsiManager manager = PsiManager.getInstance(project);
        final FileViewProviderFactory factory = LanguageFileViewProviders.INSTANCE.forLanguage(getLanguage());
        FileViewProvider viewProvider = factory != null ? factory.createFileViewProvider(file, getLanguage(), manager, physical) : null;
        if (viewProvider == null) viewProvider = new SingleRootFileViewProvider(manager, file, physical);
        return viewProvider;
    }

    private void prettyPrintASTNode(@NotNull StringBuilder prettyPrinted, ASTNode node, int depth) {
        for (int i = 0; i != depth; ++i) {
            prettyPrinted.append("   ");
        }

        if (node instanceof FileElement) {
            prettyPrinted.append("FileElement");
        } else {
            String[] names = node.getPsi().getClass().getName().split("\\.");
            prettyPrinted.append(names[names.length - 1].replace("PsiImpl", "Impl"));
        }

        prettyPrinted.append('[');
        prettyPrinted.append(node.getElementType());
        prettyPrinted.append('(');
        prettyPrinted.append(node.getTextRange().getStartOffset());
        prettyPrinted.append(':');
        prettyPrinted.append(node.getTextRange().getEndOffset());
        prettyPrinted.append(')');
        prettyPrinted.append(']');
        if ((node instanceof LeafElement) || (node instanceof PsiErrorElement)) {
            prettyPrinted.append('(');
            prettyPrinted.append('\'');
            if (node instanceof PsiErrorElement) {
                PsiErrorElement error = (PsiErrorElement)node;
                prettyPrinted.append(error.getErrorDescription());
            } else {
                prettyPrinted.append(node.getText().replace("\n", "\\n"));
            }
            prettyPrinted.append('\'');
            prettyPrinted.append(')');
        }
        prettyPrinted.append('\n');

        for (ASTNode child : node.getChildren(null)) {
            prettyPrintASTNode(prettyPrinted, child, depth + 1);
        }
    }

    public String prettyPrintASTNode(File file) {
        StringBuilder prettyPrinted = new StringBuilder();
        prettyPrintASTNode(prettyPrinted, file.getNode(), 0);
        return prettyPrinted.toString();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public File parseFile(@NotNull VirtualFile file) {
        return (File)PsiManager.getInstance(myProject).findFile(file);
    }

    @Nullable
    public File parseText(@NotNull String text) {
        return parseFile(createVirtualFile("testcase.xqy", text));
    }

    @Nullable
    public File parseResource(String resource) {
        VirtualFile file = new ResourceVirtualFile(resource);
        return parseFile(file);
    }
}
