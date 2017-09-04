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

import com.intellij.openapi.ui.ComboBox;
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI;
import uk.co.reecedunn.intellij.plugin.xquery.lang.*;

import javax.swing.*;
import java.util.List;

@SuppressWarnings({"RedundantIfStatement", "SameParameterValue"})
public class XQueryPropertiesUI implements SettingsUI<XQueryProjectSettings> {
    private JComboBox<XQueryVersion> mVersion;
    private JComboBox<ImplementationItem> mImplementations;
    private JComboBox<ImplementationItem> mImplementationVersions;
    private JComboBox<ImplementationItem> mDialectForXQuery1_0;
    private JComboBox<ImplementationItem> mDialectForXQuery3_0;
    private JComboBox<ImplementationItem> mDialectForXQuery3_1;

    private JPanel mPanel;

    public XQueryPropertiesUI() {
    }

    @Override
    public JPanel getPanel() {
        return mPanel;
    }

    private void populateVersionComboBox(JComboBox<XQueryVersion> control, ImplementationItem source, Versioned filter) {
        if (source == null) {
            return;
        }

        final XQueryVersion selected = (XQueryVersion) control.getSelectedItem();
        boolean found = false;

        control.removeAllItems();
        for (XQueryVersion version : source.getVersions(ImplementationItem.IMPLEMENTATION_DIALECT, filter)) {
            control.addItem(version);
            if (version == selected) {
                control.setSelectedItem(version);
                found = true;
            }
        }
        if (!found) {
            control.setSelectedItem(source.getDefaultVersion(ImplementationItem.IMPLEMENTATION_DIALECT, filter));
        }
    }

    private void populateComboBox(JComboBox<ImplementationItem> control, ImplementationItem source, String filter, XQueryVersion version) {
        if (source == null) {
            return;
        }

        final ImplementationItem selected = (ImplementationItem)control.getSelectedItem();
        boolean found = false;

        control.removeAllItems();
        for (ImplementationItem item : source.getItemsByVersion(filter, XQuery.INSTANCE, version)) {
            control.addItem(item);
            if (selected != null && item.toString().equals(selected.toString())) {
                control.setSelectedItem(item);
                found = true;
            }
        }
        if (!found) {
            control.setSelectedItem(source.getDefaultItemByVersion(filter, XQuery.INSTANCE, version));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void populateComboBox(JComboBox<T> control, List<T> items, T defaultItem) {
        final T selected = (T)control.getSelectedItem();
        boolean found = false;

        control.removeAllItems();
        for (T item : items) {
            control.addItem(item);
            if (selected != null && item.toString().equals(selected.toString())) {
                control.setSelectedItem(item);
                found = true;
            }
        }
        if (!found && !items.isEmpty()) {
            control.setSelectedItem(defaultItem != null ? defaultItem : items.get(0));
        }
    }

    private void createUIComponents() {
        mImplementations = new ComboBox<>();
        mImplementationVersions = new ComboBox<>();
        mVersion = new ComboBox<>();
        mDialectForXQuery1_0 = new ComboBox<>();
        mDialectForXQuery3_0 = new ComboBox<>();
        mDialectForXQuery3_1 = new ComboBox<>();

        mImplementations.setName("Implementation");
        mImplementationVersions.setName("ImplementationVersion");
        mVersion.setName("XQueryVersion");
        mDialectForXQuery1_0.setName("DialectForXQuery1.0");
        mDialectForXQuery3_0.setName("DialectForXQuery3.0");
        mDialectForXQuery3_1.setName("DialectForXQuery3.1");

        mImplementationVersions.addActionListener(e -> {
            final ImplementationItem version = (ImplementationItem)mImplementationVersions.getSelectedItem();
            populateVersionComboBox(mVersion, version, XQuery.INSTANCE);
            populateComboBox(mDialectForXQuery1_0, version, ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.VERSION_1_0);
            populateComboBox(mDialectForXQuery3_0, version, ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.VERSION_3_0);
            populateComboBox(mDialectForXQuery3_1, version, ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.VERSION_3_1);
        });

        mImplementations.addActionListener(e -> {
            final ImplementationItem implementation = (ImplementationItem)mImplementations.getSelectedItem();
            final List<ImplementationItem> items = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION);
            final ImplementationItem defaultItem = implementation.getDefaultItem(ImplementationItem.IMPLEMENTATION_VERSION);
            populateComboBox(mImplementationVersions, items, defaultItem);
        });

        for (ImplementationItem implementation : Implementations.getImplementations()) {
            mImplementations.addItem(implementation);
        }
    }

    @Override
    public boolean isModified(XQueryProjectSettings settings) {
        return false;
    }

    @Override
    public void apply(XQueryProjectSettings settings) {
    }

    @Override
    public void reset(XQueryProjectSettings settings) {
    }
}
