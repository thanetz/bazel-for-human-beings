package com.tomhanetz.bazel_for_human_beings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BazelConfigurationSettingsEditor extends SettingsEditor<BazelRunConfiguration> {

    private JTextField textField;


    @Override
    protected void resetEditorFrom(@NotNull BazelRunConfiguration s) {
        textField.setText(s.getBazelExecutablePath());
    }

    @Override
    protected void applyEditorTo(@NotNull BazelRunConfiguration s) throws ConfigurationException {
        s.setBazelExecutablePath(textField.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel jPanel = new JPanel();
        textField = new JTextField(50);
        textField.setToolTipText("Bazel path");
        JLabel label = new JLabel("Bazel Execution Path:");
        label.setLabelFor(textField);
        jPanel.add(label);
        jPanel.add(textField);
        return jPanel;
    }
}
