/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.application

import com.intellij.mock.MockApplicationEx
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.util.Condition
import javax.swing.SwingUtilities

class MockApplicationEx(disposable: Disposable) : MockApplicationEx(disposable) {
    override fun invokeLater(runnable: Runnable, expired: Condition<*>) {
        SwingUtilities.invokeLater(runnable)
    }

    override fun invokeLater(runnable: Runnable, state: ModalityState, expired: Condition<*>) {
        SwingUtilities.invokeLater(runnable)
    }

    override fun invokeLater(runnable: Runnable) {
        SwingUtilities.invokeLater(runnable)
    }

    override fun invokeLater(runnable: Runnable, state: ModalityState) {
        SwingUtilities.invokeLater(runnable)
    }
}
