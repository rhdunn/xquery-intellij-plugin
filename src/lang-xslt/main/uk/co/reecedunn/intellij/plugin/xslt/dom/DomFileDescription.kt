/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.dom

import com.intellij.util.xml.DomFileDescription
import uk.co.reecedunn.intellij.plugin.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.ast.XslPackage
import uk.co.reecedunn.intellij.plugin.xslt.ast.XslStylesheet

object XslStylesheetDomFileDescription :
    DomFileDescription<XslStylesheet>(XslStylesheet::class.java, "stylesheet", XSLT.NAMESPACE)

object XslTransformDomFileDescription :
    DomFileDescription<XslStylesheet>(XslStylesheet::class.java, "transform", XSLT.NAMESPACE)

object XslPackageDomFileDescription :
    DomFileDescription<XslPackage>(XslPackage::class.java, "package", XSLT.NAMESPACE)
