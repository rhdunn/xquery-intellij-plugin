/*
 * Copyright (C) 2019-2021 Reece H. Dunn
 * Copyright 2000-2019 JetBrains s.r.o.
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
package com.intellij.compat.testFramework

import com.intellij.mock.MockApplication
import com.intellij.mock.MockProjectEx
import com.intellij.openapi.project.Project

abstract class PlatformLiteFixture : com.intellij.testFramework.UsefulTestCase() {
    // region Project

    private var mainProject: Project? = null
    val project: Project
        get() = mainProject!!

    // endregion
    // region JUnit

    override fun setUp() {
        MockApplication.setUp(testRootDisposable)
        mainProject = MockProjectEx(testRootDisposable)
    }

    @Throws(Exception::class)
    override fun tearDown() {
        mainProject = null
        try {
            super.tearDown()
        } catch (e: Throwable) {
            // IntelliJ 2020.1 can throw an error in CodeStyleSettingsManager.getCurrentSettings
            // when trying to clean up the registered endpoints. Log that error, but don't make
            // it fail the test.
            LOG.warn(e)
        } finally {
            clearFields(this)
        }
    }

    // endregion
}
