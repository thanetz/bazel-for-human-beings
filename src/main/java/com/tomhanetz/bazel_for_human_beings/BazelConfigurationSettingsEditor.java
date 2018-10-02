package com.tomhanetz.bazel_for_human_beings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class BazelConfigurationSettingsEditor extends SettingsEditor<BazelRunConfiguration> {

    private JTextField textField;
    private JTextField paramsTextField;


    @Override
    protected void resetEditorFrom(@NotNull BazelRunConfiguration s) {

        textField.setText(s.getBazelExecutablePath());
        paramsTextField.setText(s.getParams());
    }

    @Override
    protected void applyEditorTo(@NotNull BazelRunConfiguration s) throws ConfigurationException {
        s.setBazelExecutablePath(textField.getText());
        s.setParams(paramsTextField.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel jPanel = new JPanel();
        textField = new JTextField(50);
        textField.setToolTipText("Bazel path");
        JLabel label = new JLabel("Bazel Execution Path:");
        label.setLabelFor(textField);


        paramsTextField = new JTextField(50);
        paramsTextField.setToolTipText("Bazel run parameters, comma seperated");
        JLabel paramsFieldLabel = new JLabel("Bazel Run Additional Parameters:");
        paramsFieldLabel.setLabelFor(paramsTextField);

        jPanel.setLayout(new GridLayout(15, 1));
        jPanel.add(label);
        jPanel.add(textField);
        jPanel.add(paramsFieldLabel);
        jPanel.add(paramsTextField);
        return jPanel;
    }
}
