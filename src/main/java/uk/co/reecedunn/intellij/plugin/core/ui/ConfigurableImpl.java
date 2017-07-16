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

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class ConfigurableImpl<Configuration> implements Configurable, SettingsUIFactory<Configuration> {
    private final Configuration mConfiguration;
    private SettingsUI<Configuration> mSettings;

    public ConfigurableImpl(Configuration configuration) {
        mConfiguration = configuration;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mSettings = createSettingsUI();
        mSettings.reset(mConfiguration);
        return mSettings.getPanel();
    }

    @Override
    public boolean isModified() {
        return mSettings.isModified(mConfiguration);
    }

    @Override
    public void apply() throws ConfigurationException {
        mSettings.apply(mConfiguration);
    }

    @Override
    public void reset() {
        mSettings.reset(mConfiguration);
    }
}
