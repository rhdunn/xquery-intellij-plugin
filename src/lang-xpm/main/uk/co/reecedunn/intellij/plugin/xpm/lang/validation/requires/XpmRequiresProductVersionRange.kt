/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires

import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.ge
import uk.co.reecedunn.intellij.plugin.xpm.lang.le
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter

class XpmRequiresProductVersionRange(
    private val from: XpmProductVersion,
    private val to: XpmProductVersion
) : XpmRequiresConformanceTo {
    override fun conformanceTo(reporter: XpmSyntaxErrorReporter): Boolean {
        return reporter.product?.let {
            it.product === from.product && it.ge(from) && it.le(to)
        } == true
    }

    override fun toString(): String {
        return "${from.product.presentation.presentableText} ${from.id}-${to.id}"
    }
}
