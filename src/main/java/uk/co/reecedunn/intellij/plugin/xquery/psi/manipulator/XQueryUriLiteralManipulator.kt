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
package uk.co.reecedunn.intellij.plugin.xquery.psi.manipulator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.util.IncorrectOperationException
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryUriLiteralPsiImpl

class XQueryUriLiteralManipulator : AbstractElementManipulator<XQueryUriLiteralPsiImpl>() {
    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(element: XQueryUriLiteralPsiImpl, range: TextRange, newContent: String): XQueryUriLiteralPsiImpl {
        return element
    }
}
