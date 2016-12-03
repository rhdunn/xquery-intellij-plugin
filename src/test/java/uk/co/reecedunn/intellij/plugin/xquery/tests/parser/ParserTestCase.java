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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageASTFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.ParsingTestCase;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

import java.io.*;

@SuppressWarnings("SameParameterValue")
public abstract class ParserTestCase extends ParsingTestCase {
    public ParserTestCase() {
        super("", ".xqy", new XQueryParserDefinition());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registerApplicationService(XQueryProjectSettings.class, new XQueryProjectSettings());
        addExplicitExtension(LanguageASTFactory.INSTANCE, myLanguage, new XQueryASTFactory());
    }

    // region Parser Test Helpers

    void initializeSettings(XQueryProjectSettings settings) {
    }

    protected XQueryProjectSettings getSettings() {
        return XQueryProjectSettings.getInstance(myProject);
    }

    String loadResource(String resource) {
        ClassLoader loader = ParserTestCase.class.getClassLoader();
        InputStream stream = loader.getResourceAsStream(resource);
        StringWriter writer = new StringWriter();
        try {
            IOUtil.copyCompletely(new InputStreamReader(stream), writer);
        } catch (Exception e) {
            //
        }
        return writer.toString();
    }

    LightVirtualFile createVirtualFile(@NonNls String name, String text) {
        LightVirtualFile file = new LightVirtualFile(name, myLanguage, text);
        file.setCharset(CharsetToolkit.UTF8_CHARSET);
        return file;
    }

    FileViewProvider getFileViewProvider(@NotNull Project project, LightVirtualFile file, boolean physical) {
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

    String prettyPrintASTNode(XQueryFile file) {
        StringBuilder prettyPrinted = new StringBuilder();
        prettyPrintASTNode(prettyPrinted, file.getNode(), 0);
        return prettyPrinted.toString();
    }

    @NotNull
    private XQueryFile parseFile(@NotNull LightVirtualFile file) {
        initializeSettings(getSettings());
        return (XQueryFile)super.createFile(file);
    }

    @NotNull
    protected XQueryFile parseText(@NotNull String text) {
        return parseFile(createVirtualFile("testcase.xqy", text));
    }

    @NotNull
    protected XQueryFile parseResource(String resource) {
        return parseFile(createVirtualFile(resource, loadResource(resource)));
    }

    // endregion
}
