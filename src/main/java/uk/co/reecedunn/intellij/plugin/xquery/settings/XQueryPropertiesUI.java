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
package uk.co.reecedunn.intellij.plugin.xquery.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementation;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import javax.swing.*;

public class XQueryPropertiesUI {
    private JComboBox<Implementation> mImplementation;
    private JComboBox<XQueryVersion> mVersion;

    private JPanel mPanel;
    private final XQueryProjectSettings mSettings;

    public XQueryPropertiesUI(Project project) {
        mSettings = XQueryProjectSettings.getInstance(project);
    }

    public JPanel getPanel() {
        return mPanel;
    }

    private void createUIComponents() {
        mVersion = new JComboBox<>();

        mImplementation = new JComboBox<>();
        mImplementation.addActionListener(e -> {
            final Implementation implementation = (Implementation)mImplementation.getSelectedItem();

            final XQueryVersion selectedVersion = (XQueryVersion)mVersion.getSelectedItem();
            boolean foundVersion = false;

            mVersion.removeAllItems();
            for (XQueryVersion version : implementation.getVersions()) {
                mVersion.addItem(version);
                if (version == selectedVersion) {
                    mVersion.setSelectedItem(version);
                    foundVersion = true;
                }
            }
            if (!foundVersion) {
                mVersion.setSelectedItem(implementation.getDefaultVersion());
            }
        });

        for (Implementation implementation : Implementation.values()) {
            mImplementation.addItem(implementation);
        }
    }

    public boolean isModified() {
        if (!mImplementation.getSelectedItem().equals(mSettings.getXQueryImplementation())) return true;
        if (!mVersion.getSelectedItem().equals(mSettings.getXQueryVersion())) return true;
        return false;
    }

    public void apply() throws ConfigurationException {
        mSettings.setXQueryImplementation((Implementation)mImplementation.getSelectedItem());
        mSettings.setXQueryVersion((XQueryVersion)mVersion.getSelectedItem());
    }

    public void reset() {
        mImplementation.setSelectedItem(mSettings.getXQueryImplementation());
        mVersion.setSelectedItem(mSettings.getXQueryVersion());
    }
}
