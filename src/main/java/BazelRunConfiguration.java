import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;

@State(
        name = "BazelRunSettings",
        storages = {
                @Storage("bazel_4_human_beings_run_settings.xml")
        }
)
public class BazelRunConfiguration extends LocatableConfigurationBase implements PersistentStateComponent<BazelRunConfiguration> {

    private String bazelExecutablePath;
    private String action = "run";
    private String name;


    public BazelRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
        this.name = name;
    }

    public BazelRunConfiguration(){
        this(ProjectManager.getInstance().getDefaultProject(), new BazelConfigurationFactory(new BazelConfigurationType()), "Temp name");
    }


    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new  CommandLineState(environment) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                String args[] = {BazelApplicationSettings.getInstance().getBazelRunPath(), getAction(), getBazelExecutablePath()};
                GeneralCommandLine commandLine = new GeneralCommandLine(args);
                commandLine.setCharset(Charset.forName("UTF-8"));
                commandLine.setWorkDirectory(environment.getProject().getBasePath());
                commandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);
                ProcessHandler osProcessHandler = new ColoredProcessHandler(commandLine);
                return osProcessHandler;
            }
        };
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new BazelConfigurationSettingsEditor();
    }


    public String getBazelExecutablePath() {
        return bazelExecutablePath;
    }

    public void setBazelExecutablePath(String bazelExecutablePath) {
        this.bazelExecutablePath = bazelExecutablePath;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Nullable
    @Override
    public BazelRunConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull BazelRunConfiguration bazelRunConfiguration) {
        XmlSerializerUtil.copyBean(bazelRunConfiguration, this);
    }
}
