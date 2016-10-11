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
package uk.co.reecedunn.intellij.plugin.xquery.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface XQueryNamespaceProvider {
    /**
     * Gets the URI associated with the namespace prefix.
     *
     * The resolved element type is usually a <code>URILiteral</code>. This is not always the case,
     * though. A <code>CompNamespaceConstructor</code> will resolve this to a <code>URIExpr</code>.
     *
     * If the construct supports the namespace prefix but the URI element is missing, the resolved
     * namespace element will be the construct implementing this interface. This allows the code to
     * resolve prefices to the XQuery construct that provides that namespace in this case, even if
     * it cannot resolve the URI of that prefix.
     *
     * @param prefix The namespace prefix to resolve.
     *
     * @return The URI associated with the namespace prefix, or null if the prefix is not supported.
     */
    @Nullable
    PsiElement resolveNamespace(CharSequence prefix);
}
