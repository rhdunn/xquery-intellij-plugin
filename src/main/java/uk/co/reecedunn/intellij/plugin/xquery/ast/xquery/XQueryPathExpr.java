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

/**
 * An XQuery 1.0 <code>PathExpr</code> node in the XQuery AST.
 *
 * In XQuery 1.0 this is a <code>ValueExpr</code>. In XQuery 3.0 this is a
 * child of <code>SimpleMapExpr</code>. The XQuery 3.0 layout is used here.
 */
public interface XQueryPathExpr extends PsiElement {
}
