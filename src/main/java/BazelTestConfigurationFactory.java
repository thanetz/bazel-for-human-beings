import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class BazelTestConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_NAME = "Bazel for Human Beings configuration Test factory";

    protected BazelTestConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new BazelTestRunConfiguration(project, this, "Bazel For Human Beings Test");
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}
