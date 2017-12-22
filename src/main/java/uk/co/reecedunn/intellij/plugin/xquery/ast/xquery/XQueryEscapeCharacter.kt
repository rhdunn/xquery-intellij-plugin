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
 * An XQuery 1.0 `EscapeQuot` or `EscapeApos` node in the XQuery AST.
 *
 * Because the `EscapeQuot` and `EscapeApos` nodes are
 * escaping a single character in the same manner, they are implemented as
 * instances of `EscapeCharacter`. This `EscapeCharacter`
 * construct does not exist in the XQuery grammar, but is used for convenience
 * here in implementing the AST.
 *
 * The entity reference nodes (`PredefinedEntityRef` and
 * `CharRef`) are not implemented through this interface as they are
 * providing different functionality where the entity may map to multiple
 * characters.
 */
interface XQueryEscapeCharacter : PsiElement
