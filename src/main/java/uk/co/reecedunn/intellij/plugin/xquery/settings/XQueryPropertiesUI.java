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
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import javax.swing.*;

public class XQueryPropertiesUI {
    private JComboBox<XQueryVersion> mVersion;
    private JComboBox<ImplementationItem> mImplementations;
    private JComboBox<ImplementationItem> mImplementationVersions;
    private JComboBox<ImplementationItem> mDialectForXQuery1_0;
    private JComboBox<ImplementationItem> mDialectForXQuery3_0;
    private JComboBox<ImplementationItem> mDialectForXQuery3_1;

    private JPanel mPanel;
    private final XQueryProjectSettings mSettings;

    public XQueryPropertiesUI(Project project) {
        mSettings = XQueryProjectSettings.getInstance(project);
    }

    public JPanel getPanel() {
        return mPanel;
    }

    private void populateComboBox(JComboBox<ImplementationItem> control, ImplementationItem source, String filter) {
        final ImplementationItem selected = (ImplementationItem)control.getSelectedItem();
        boolean found = false;

        control.removeAllItems();
        for (ImplementationItem item : source.getItems(filter)) {
            control.addItem(item);
            if (item == selected) {
                control.setSelectedItem(item);
                found = true;
            }
        }
        if (!found) {
            control.setSelectedItem(source.getDefaultItem(filter));
        }
    }

    private void populateComboBox(JComboBox<ImplementationItem> control, ImplementationItem source, String filter, XQueryVersion version) {
        if (source == null) {
            return;
        }

        final ImplementationItem selected = (ImplementationItem)control.getSelectedItem();
        boolean found = false;

        control.removeAllItems();
        for (ImplementationItem item : source.getItemsForXQueryVersion(filter, version)) {
            control.addItem(item);
            if (item == selected) {
                control.setSelectedItem(item);
                found = true;
            }
        }
        if (!found) {
            control.setSelectedItem(source.getDefaultItemForXQueryVersion(filter, version));
        }
    }

    private void createUIComponents() {
        mDialectForXQuery1_0 = new JComboBox<>();
        mDialectForXQuery3_0 = new JComboBox<>();
        mDialectForXQuery3_1 = new JComboBox<>();

        mImplementationVersions = new JComboBox<>();
        mImplementationVersions.addActionListener(e -> {
            final ImplementationItem version = (ImplementationItem)mImplementationVersions.getSelectedItem();
            populateComboBox(mDialectForXQuery1_0, version, ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.XQUERY_1_0);
            populateComboBox(mDialectForXQuery3_0, version, ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.XQUERY_3_0);
            populateComboBox(mDialectForXQuery3_1, version, ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.XQUERY_3_1);
        });

        mImplementations = new JComboBox<>();
        mImplementations.addActionListener(e -> {
            final ImplementationItem implementation = (ImplementationItem)mImplementations.getSelectedItem();
            populateComboBox(mImplementationVersions, implementation, ImplementationItem.IMPLEMENTATION_VERSION);
        });

        for (ImplementationItem implementation : Implementations.getImplementations()) {
            mImplementations.addItem(implementation);
        }

        mVersion = new JComboBox<>();
        for (XQueryVersion version : XQueryVersion.values()) {
            mVersion.addItem(version);
        }
    }

    public boolean isModified() {
        if (!mImplementations.getSelectedItem().equals(mSettings.getImplementation())) return true;
        if (!mImplementationVersions.getSelectedItem().equals(mSettings.getImplementationVersion())) return true;
        if (!mVersion.getSelectedItem().equals(mSettings.getXQueryVersion())) return true;
        if (!mDialectForXQuery1_0.getSelectedItem().equals(mSettings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0))) return true;
        if (!mDialectForXQuery3_0.getSelectedItem().equals(mSettings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_0))) return true;
        if (!mDialectForXQuery3_1.getSelectedItem().equals(mSettings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_1))) return true;
        return false;
    }

    public void apply() throws ConfigurationException {
        mSettings.setImplementation((ImplementationItem)mImplementations.getSelectedItem());
        mSettings.setImplementationVersion((ImplementationItem)mImplementationVersions.getSelectedItem());
        mSettings.setXQueryVersion((XQueryVersion)mVersion.getSelectedItem());
        mSettings.setDialectForXQueryVersion(XQueryVersion.XQUERY_1_0, (ImplementationItem)mDialectForXQuery1_0.getSelectedItem());
        mSettings.setDialectForXQueryVersion(XQueryVersion.XQUERY_3_0, (ImplementationItem)mDialectForXQuery3_0.getSelectedItem());
        mSettings.setDialectForXQueryVersion(XQueryVersion.XQUERY_3_1, (ImplementationItem)mDialectForXQuery3_1.getSelectedItem());
    }

    public void reset() {
        mImplementations.setSelectedItem(mSettings.getImplementation());
        mImplementationVersions.setSelectedItem(mSettings.getImplementationVersion());
        mVersion.setSelectedItem(mSettings.getXQueryVersion());
        mDialectForXQuery1_0.setSelectedItem(mSettings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0));
        mDialectForXQuery3_0.setSelectedItem(mSettings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_0));
        mDialectForXQuery3_1.setSelectedItem(mSettings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_1));
    }
}
