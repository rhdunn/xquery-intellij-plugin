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
package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ex.InspectionManagerEx
import com.intellij.psi.SmartPointerManager
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockSmartPointerManager
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

abstract class InspectionTestCase : ParserTestCase() {
    private val inspectionManager get(): InspectionManager = InspectionManager.getInstance(myProject)

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        registerApplicationService(SmartPointerManager::class.java, MockSmartPointerManager())
        registerApplicationService(InspectionManager::class.java, InspectionManagerEx(myProject))
    }

    fun inspect(file: XQueryModule, inspection: LocalInspectionTool): Array<ProblemDescriptor>? {
        return inspection.checkFile(file, inspectionManager as InspectionManagerEx, false)
    }
}
