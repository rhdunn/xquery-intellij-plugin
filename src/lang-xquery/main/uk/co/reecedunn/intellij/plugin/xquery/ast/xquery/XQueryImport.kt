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
package uk.co.reecedunn.intellij.plugin.xquery.ast.xquery

import com.intellij.psi.PsiElement

/**
 * An XQuery 1.0 `Import` node in the XQuery AST.
 *
 * Because the child nodes of an `Import` are only referenced
 * from the `Import` node in the grammar, the `Import`
 * nodes are stored as instances of the child nodes instead of as distinct
 * nodes themselves.
 *
 * In the case of an invalid `Import` declaration that has an
 * `import` keyword that does not form a `SchemaImport`
 * nor `ModuleImport`, an `Import` node is used directly.
 * This is because there is not enough information to know what type of import
 * declaration this is.
 */
interface XQueryImport : PsiElement
