import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.LabeledComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class BazelApplicationSettingsConfigurable implements Configurable {

    private JTextField textField;
    private JTextField runTextField;

    @Nls
    @Override
    public String getDisplayName() {
        return "Bazel 4 Human Beings Configuration";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel jPanel = new JPanel();

        textField = new JTextField(BazelApplicationSettings.getInstance().getBazelQueryPath(), 50);
        textField.setToolTipText("Bazel query path");
        JLabel queryFieldLabel = new JLabel("Bazel Query Path:");
        queryFieldLabel.setLabelFor(textField);

        runTextField = new JTextField(BazelApplicationSettings.getInstance().getBazelRunPath(), 50);
        runTextField.setToolTipText("Bazel run path");
        JLabel runFieldLabel = new JLabel("Bazel Run Path:");
        runFieldLabel.setLabelFor(runTextField);

        jPanel.setLayout(new GridLayout(15, 1));
        jPanel.add(queryFieldLabel);
        jPanel.add(textField);
        jPanel.add(runFieldLabel);
        jPanel.add(runTextField);

        return jPanel;
    }

    @Override
    public boolean isModified() {
        return !textField.getText().equals(BazelApplicationSettings.getInstance().getBazelQueryPath()) ||
                !runTextField.getText().equals(BazelApplicationSettings.getInstance().getBazelRunPath());
    }

    @Override
    public void apply() throws ConfigurationException {
        if(textField.getText().isEmpty() || runTextField.getText().isEmpty()){
            throw new ConfigurationException("Bazel path could not be empty");
        }
        BazelApplicationSettings settings = BazelApplicationSettings.getInstance();
        settings.setBazelQueryPath(textField.getText());
        settings.setBazelRunPath(runTextField.getText());
    }
}
