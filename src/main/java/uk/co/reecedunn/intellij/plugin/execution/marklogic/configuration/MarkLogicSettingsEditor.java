/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.execution.marklogic.configuration;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI;

import javax.swing.*;

public class MarkLogicSettingsEditor extends SettingsEditor<MarkLogicRunConfiguration> {
    private SettingsUI<MarkLogicRunConfiguration> mPanel;

    @Override
    protected void resetEditorFrom(@NotNull MarkLogicRunConfiguration configuration) {
        mPanel.reset(configuration);
    }

    @Override
    protected void applyEditorTo(@NotNull MarkLogicRunConfiguration configuration) throws ConfigurationException {
        mPanel.apply(configuration);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        mPanel = new MarkLogicSettingsUI();
        return mPanel.getPanel();
    }
}
