package com.tomhanetz.bazel_for_human_beings;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;
import java.lang.String;

import javax.swing.*;

public class BazelConfigurationType implements ConfigurationType {


    @Override
    public String getDisplayName() {
        return "Bazel For Human Beings";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Run Bazel Command from your script";
    }

    @NotNull
    @Override
    public String getId() {
        return "BAZEL_FOR_HUMAN_BEINGS_CONFIGURATION_TYPE";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.General.Web;
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new BazelConfigurationFactory(this)};
    }
}
