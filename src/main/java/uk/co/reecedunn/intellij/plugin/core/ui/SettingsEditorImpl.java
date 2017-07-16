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
package uk.co.reecedunn.intellij.plugin.core.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SettingsEditorImpl<Configuration> extends SettingsEditor<Configuration> {
    private SettingsUIFactory<Configuration> mSettingsFactory;
    private SettingsUI<Configuration> mSettings;

    public SettingsEditorImpl(SettingsUIFactory<Configuration> factory) {
        mSettingsFactory = factory;
    }

    @Override
    protected void resetEditorFrom(@NotNull Configuration configuration) {
        mSettings.reset(configuration);
    }

    @Override
    protected void applyEditorTo(@NotNull Configuration configuration) throws ConfigurationException {
        mSettings.apply(configuration);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        mSettings = mSettingsFactory.createSettingsUI();
        return mSettings.getPanel();
    }
}
