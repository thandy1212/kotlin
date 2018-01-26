/*
 * Copyright 2010-2015 JetBrains s.r.o.
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

package org.jetbrains.kotlin.idea.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.ContentFactory
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.idea.internal.KotlinBytecodeToolWindow
import javax.swing.JComponent

sealed class ShowKotlinBackendCodeAction(
    private val toolWindowId: String,
    private val componentFactory: (Project, ToolWindow) -> JComponent
) : AnAction() {
    class JvmBytecode : ShowKotlinBackendCodeAction("Kotlin Bytecode", ::KotlinBytecodeToolWindow)
    //class JSCode : ShowKotlinBackendCodeAction("Kotlin JS Code", ::KotlinJSCodeToolWindow)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val toolWindowManager = ToolWindowManager.getInstance(project)

        var toolWindow = toolWindowManager.getToolWindow(toolWindowId)
        if (toolWindow == null) {
            toolWindow = toolWindowManager.registerToolWindow(toolWindowId, false, ToolWindowAnchor.RIGHT)
            toolWindow.icon = KotlinIcons.SMALL_LOGO_13

            val contentManager = toolWindow.contentManager
            val contentFactory = ContentFactory.SERVICE.getInstance()
            contentManager.addContent(contentFactory.createContent(componentFactory(project, toolWindow), "", false))
        }
        toolWindow.activate(null)
    }

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        e.presentation.isEnabled = e.project != null && file?.fileType == KotlinFileType.INSTANCE
    }
}
