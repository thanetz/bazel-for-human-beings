import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class BazelConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_NAME = "Bazel for Human Beings configuration factory";

    protected BazelConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new BazelRunConfiguration(project, this, "Bazel For Human Beings");
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}
