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
import com.intellij.ui.ColoredListCellRenderer;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI;
import uk.co.reecedunn.intellij.plugin.xquery.lang.*;

import javax.swing.*;
import java.util.List;

@SuppressWarnings({"RedundantIfStatement", "SameParameterValue"})
public class XQueryPropertiesUI implements SettingsUI<XQueryProjectSettings> {
    private JComboBox<Version> mVersion;
    private JComboBox<Product> mImplementations;
    private JComboBox<Version> mImplementationVersions;
    private JComboBox<Versioned> mDialectForXQuery1_0;
    private JComboBox<Versioned> mDialectForXQuery3_0;
    private JComboBox<Versioned> mDialectForXQuery3_1;

    private JPanel mPanel;

    public XQueryPropertiesUI() {
    }

    @Override
    public JPanel getPanel() {
        return mPanel;
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

        ColoredListCellRenderer<Versioned> VERSIONED_RENDERER = new ColoredListCellRenderer<Versioned>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends Versioned> list, Versioned value, int index, boolean selected, boolean hasFocus) {
                if (value != null) {
                    append(value.getName());
                }
            }
        };
        mDialectForXQuery1_0.setRenderer(VERSIONED_RENDERER);
        mDialectForXQuery3_0.setRenderer(VERSIONED_RENDERER);
        mDialectForXQuery3_1.setRenderer(VERSIONED_RENDERER);

        mImplementationVersions.addActionListener(e -> {
            final Product product = (Product)mImplementations.getSelectedItem();
            final Version productVersion = (Version)mImplementationVersions.getSelectedItem();
            if (product == null || productVersion == null) return;

            populateComboBox(mVersion, XQuery.INSTANCE.versionsFor(product, productVersion), null);
            populateComboBox(mDialectForXQuery1_0, product.flavoursForXQueryVersion(productVersion, "1.0"), null);
            populateComboBox(mDialectForXQuery3_0, product.flavoursForXQueryVersion(productVersion, "3.0"), null);
            populateComboBox(mDialectForXQuery3_1, product.flavoursForXQueryVersion(productVersion, "3.1"), null);
        });

        mImplementations.addActionListener(e -> {
            final Product product = (Product)mImplementations.getSelectedItem();
            if (product == null) return;

            populateComboBox(mImplementationVersions, product.getImplementation().getVersions(), null);
        });

        populateComboBox(mImplementations, ImplementationsKt.getPRODUCTS(), W3C.INSTANCE.getSPECIFICATIONS());
    }

    @SuppressWarnings("unchecked")
    private <E> boolean isModified(JComboBox<E> comboBox, E setting) {
        E value = (E)comboBox.getSelectedItem();
        if (value == null) return setting != null;
        return !value.equals(setting);
    }

    private boolean isModified(JComboBox comboBox, String setting) {
        Object value = comboBox.getSelectedItem();
        if (value == null) return setting != null;
        if (value instanceof Version) return !((Version)value).getId().equals(setting);
        return !((Versioned)value).getId().equals(setting);
    }

    @Override
    public boolean isModified(XQueryProjectSettings settings) {
        if (isModified(mImplementations, settings.getProduct())) return true;
        if (isModified(mImplementationVersions, settings.getProductVersion())) return true;
        if (isModified(mVersion, settings.getXQueryVersion())) return true;
        if (isModified(mDialectForXQuery1_0, settings.getXQuery10Dialect())) return true;
        if (isModified(mDialectForXQuery3_0, settings.getXQuery30Dialect())) return true;
        if (isModified(mDialectForXQuery3_1, settings.getXQuery31Dialect())) return true;
        return false;
    }

    @Override
    public void apply(XQueryProjectSettings settings) {
    }

    @Override
    public void reset(XQueryProjectSettings settings) {
    }
}
