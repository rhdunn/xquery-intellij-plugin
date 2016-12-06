/*
 * Copyright (C) 2016 Reece H. Dunn
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

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.testFramework.LightVirtualFile;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public abstract class ParsingTestCase<File extends PsiFile> extends com.intellij.testFramework.ParsingTestCase {
    private PsiFileFactory myFileFactory;

    public ParsingTestCase(String fileExt, ParserDefinition... definitions) {
        super("", fileExt, definitions);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFileFactory = new PsiFileFactoryImpl(getPsiManager());
    }

    public String loadResource(String resource) {
        ClassLoader loader = ParsingTestCase.class.getClassLoader();
        InputStream stream = loader.getResourceAsStream(resource);
        StringWriter writer = new StringWriter();
        try {
            IOUtil.copyCompletely(new InputStreamReader(stream), writer);
        } catch (Exception e) {
            //
        }
        return writer.toString();
    }

    public LightVirtualFile createVirtualFile(@NonNls String name, String text) {
        LightVirtualFile file = new LightVirtualFile(name, myLanguage, text);
        file.setCharset(CharsetToolkit.UTF8_CHARSET);
        return file;
    }

    @SuppressWarnings("SameParameterValue")
    public FileViewProvider getFileViewProvider(@NotNull Project project, LightVirtualFile file, boolean physical) {
        final PsiManager manager = PsiManager.getInstance(project);
        final FileViewProviderFactory factory = LanguageFileViewProviders.INSTANCE.forLanguage(myLanguage);
        FileViewProvider viewProvider = factory != null ? factory.createFileViewProvider(file, myLanguage, manager, physical) : null;
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
    public File parseFile(@NotNull LightVirtualFile file) {
        return parseFile(file, true, false, false);
    }

    @Nullable
    @SuppressWarnings({"unchecked", "SameParameterValue"})
    public File parseFile(@NotNull LightVirtualFile file, boolean eventSystemEnabled, boolean markAsCopy, boolean noSizeLimit) {
        return (File)myFileFactory.createFileFromText(file.getName(), myLanguage, file.getContent(), eventSystemEnabled, markAsCopy, noSizeLimit, file);
    }

    @Nullable
    public File parseText(@NotNull String text) {
        return parseFile(createVirtualFile("testcase.xqy", text));
    }

    @Nullable
    public File parseResource(String resource) {
        return parseFile(createVirtualFile(resource, loadResource(resource)));
    }
}
