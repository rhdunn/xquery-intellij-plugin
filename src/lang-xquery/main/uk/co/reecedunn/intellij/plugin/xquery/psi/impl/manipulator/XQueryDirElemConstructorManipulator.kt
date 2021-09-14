/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.manipulator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor

class XQueryDirElemConstructorManipulator : AbstractElementManipulator<XQueryDirElemConstructor>() {
    override fun handleContentChange(
        element: XQueryDirElemConstructor,
        range: TextRange,
        newContent: String?
    ): XQueryDirElemConstructor {
        return element
    }

    override fun getRangeInElement(element: XQueryDirElemConstructor): TextRange {
        return super.getRangeInElement(element)
    }
}
