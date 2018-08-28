/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.ast.plugin

import com.intellij.psi.PsiElement

/**
 * A Saxon 9.8 `TypeDecl` node in the XQuery AST.
 *
 * <pre>
 *    Prolog   ::= ((DefaultNamespaceDecl | Setter | NamespaceDecl | Import) Separator)*
 *                 ((ContextItemDecl | AnnotatedDecl | OptionDecl | TypeDecl) Separator)*
 *    TypeDecl ::= "declare" "type" QName "=" ItemType
 * </pre>
 *
 * Reference: http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/type-aliases
 */
interface PluginTypeDecl : PsiElement