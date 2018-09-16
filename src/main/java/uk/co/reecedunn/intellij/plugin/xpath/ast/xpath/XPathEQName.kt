/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.ast.xpath

import com.intellij.psi.PsiElement

/**
 * An XPath 3.0 and XQuery 3.0 <code>EQName</code> node in the XQuery AST.
 *
 * When the <code>EQName</code> node is specialised (such as with
 * <code>VarName</code>), the <code>EQName</code> node is not stored directly
 * in the AST. Instead, it is exposed as an instance of that specialised node.
 *
 * This may be an instance of an <code>NCName</code>, <code>QName</code> or
 * <code>URIQualifiedName</code>.
 */
interface XPathEQName : PsiElement
