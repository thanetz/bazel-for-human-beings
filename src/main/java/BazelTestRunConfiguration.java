import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.CompositeFilter;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.RegexpFilter;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Key;
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
public class BazelTestRunConfiguration extends LocatableConfigurationBase implements PersistentStateComponent<BazelTestRunConfiguration> {

    private String bazelExecutablePath;
    private String name;
    private BazelTestRunConfiguration thisConfiguration;


    public BazelTestRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
        this.name = name;
        this.thisConfiguration = this;
    }

    public BazelTestRunConfiguration(){
        this(ProjectManager.getInstance().getDefaultProject(), new BazelConfigurationFactory(new BazelConfigurationType()), "Temp name");
    }


    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new  CommandLineState(environment) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                String args[] = {BazelApplicationSettings.getInstance().getBazelRunPath(), "test", "--test_output=errors", getBazelExecutablePath()};
                GeneralCommandLine commandLine = new GeneralCommandLine(args);
                commandLine.setCharset(Charset.forName("UTF-8"));
                commandLine.setWorkDirectory(environment.getProject().getBasePath());
                commandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);
                ProcessHandler osProcessHandler = new BazelTestProcessHandler(commandLine, getBazelExecutablePath());
                return osProcessHandler;
            }

            @NotNull
            @Override
            public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
                SMTRunnerConsoleProperties properties = new SMTRunnerConsoleProperties(thisConfiguration, "Bazel Tet Framework", executor){
                    @Nullable
                    @Override
                    public SMTestLocator getTestLocator() {
                        return new BazelTestLocator();
                    }
                };

                ProcessHandler handler = this.startProcess();
                //SMTRunnerConsoleView consoleView2 = new SMTRunnerConsoleView(properties);
                //consoleView2.attachToProcess(handler);
                BaseTestsOutputConsoleView consoleView2 = SMTestRunnerConnectionUtil.createAndAttachConsole("Bazel Tet Framework", handler, properties);
                ExecutionResult result = new DefaultExecutionResult(consoleView2, handler, createActions(consoleView2, handler, executor));

                return result;
            }
        };
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new BazelTestConfigurationSettingsEditor();
    }


    public String getBazelExecutablePath() {
        return bazelExecutablePath;
    }

    public void setBazelExecutablePath(String bazelExecutablePath) {
        this.bazelExecutablePath = bazelExecutablePath;
    }

    @Nullable
    @Override
    public BazelTestRunConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull BazelTestRunConfiguration bazelRunConfiguration) {
        XmlSerializerUtil.copyBean(bazelRunConfiguration, this);
    }
}
