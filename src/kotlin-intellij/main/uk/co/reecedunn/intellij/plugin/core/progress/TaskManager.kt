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
package uk.co.reecedunn.intellij.plugin.core.progress

import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.util.containers.ContainerUtil
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater

interface TaskProgressListener<Item> {
    fun started(context: Item)

    fun stopped(context: Item)
}

class TaskManager<Item> {
    private val active = ContainerUtil.createLockFreeCopyOnWriteList<Item>()
    private val listeners = ContainerUtil.createLockFreeCopyOnWriteList<TaskProgressListener<Item>>()

    fun addListener(listener: TaskProgressListener<Item>): Boolean = listeners.add(listener)

    fun removeListener(listener: TaskProgressListener<Item>): Boolean = listeners.remove(listener)

    fun isActive(context: Item): Boolean = active.contains(context)

    @Suppress("MemberVisibilityCanBePrivate")
    fun backgroundable(title: String, project: Project?, context: Item, task: (ProgressIndicator) -> Unit): Boolean {
        if (isActive(context)) return false
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, title) {
            override fun run(indicator: ProgressIndicator) {
                active.add(context)
                try {
                    invokeLater(ModalityState.any()) {
                        listeners.forEach { it.started(context) }
                    }
                    task(indicator)
                } finally {
                    active.remove(context)
                    invokeLater(ModalityState.any()) {
                        listeners.forEach { it.stopped(context) }
                    }
                }
            }
        })
        return true
    }

    fun backgroundable(title: String, context: Item, task: (ProgressIndicator) -> Unit): Boolean {
        return backgroundable(title, null, context, task)
    }
}
