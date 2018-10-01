import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BazelTestConfigurationType implements ConfigurationType {


    @Override
    public String getDisplayName() {
        return "Bazel For Human Beings Test";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Run Bazel Test Command from your script";
    }

    @NotNull
    @Override
    public String getId() {
        return "BAZEL_FOR_HUMAN_BEINGS_TEST_CONFIGURATION_TYPE";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.General.Web;
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new BazelTestConfigurationFactory(this)};
    }
}
