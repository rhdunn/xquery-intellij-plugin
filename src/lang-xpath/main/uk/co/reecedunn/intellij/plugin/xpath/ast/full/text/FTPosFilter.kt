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
package uk.co.reecedunn.intellij.plugin.xpath.ast.full.text

import com.intellij.psi.PsiElement

/**
 * An XQuery Full Text 1.0 `FTPosFilter` node in the XQuery AST.
 *
 * Because the child nodes of an `FTPosFilter` are only referenced
 * from the `FTPosFilter` node in the grammar, the `FTPosFilter`
 * nodes are stored as instances of the child nodes instead of as
 * distinct nodes themselves.
 */
interface FTPosFilter : PsiElement