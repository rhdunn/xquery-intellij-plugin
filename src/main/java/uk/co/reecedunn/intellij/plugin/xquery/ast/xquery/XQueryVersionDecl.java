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
package uk.co.reecedunn.intellij.plugin.xquery.ast.xquery;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * An XQuery 1.0 and 3.0 <code>VersionDecl</code> node in the XQuery AST.
 *
 * The XQuery 3.0 <code>VersionDecl</code> node supports omitting the
 * <code>version</code> part in order to only specify the encoding. This
 * does not change the <code>VersionDecl</code> representation, so this
 * interface is used for both XQuery 1.0 and XQuery 3.0
 * <code>VersionDecl</code> nodes. Validation is handled by the
 * {@link uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParser}.
 */
public interface XQueryVersionDecl extends PsiElement {
    /**
     * Gets the XQuery version specified in this declaration.
     *
     * The XQuery version may be invalid.
     *
     * @return The XQuery version if provided, or <code>null</code> otherwise.
     */
    @Nullable
    XQueryStringLiteral getVersion();

    /**
     * Gets the character encoding specified in this declaration.
     *
     * The character encoding may be invalid.
     *
     * @return The character encoding if provided, or <code>null</code> otherwise.
     */
    @Nullable XQueryStringLiteral getEncoding();
}
