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
package uk.co.reecedunn.intellij.plugin.intellij.xdebugger

import com.intellij.lang.Language
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.evaluation.QueryEditorsProvider

class QueryDebugProcess(
    session: XDebugSession,
    language: Language
) : XDebugProcess(session) {
    private val editorsProvider: XDebuggerEditorsProvider = QueryEditorsProvider(language)

    override fun getEditorsProvider(): XDebuggerEditorsProvider = editorsProvider
}
