import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
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

import java.io.IOException;
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
    private BazelRunConfiguration thisConfiguration;


    public BazelRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
        this.name = name;
        this.thisConfiguration = this;
    }

    public BazelRunConfiguration(){
        this(ProjectManager.getInstance().getDefaultProject(), new BazelConfigurationFactory(new BazelConfigurationType()), "Temp name");
    }


    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException { // returns py_binary rule //a/b/c:my_exec
        final String bazelAction;

        String bazelTypeResult = null;
        try {
            bazelTypeResult = Utils.runCommand(new String[]{BazelApplicationSettings.getInstance().getBazelRunPath(), "query",
                    "kind(rule, "  + bazelExecutablePath + ")", "--output", "label_kind"}, environment.getProject().getBasePath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String bazelType = bazelTypeResult.split(" ")[0];
        if (Consts.BINARY.contains(bazelType)){
            bazelAction = "run";
        }else if (Consts.LIBRARY.contains(bazelType)){
            bazelAction = "build";
        }else if (Consts.TEST.contains(bazelType)){
            bazelAction = "test";
        }else{
            return null;
        }

        return new  CommandLineState(environment) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                GeneralCommandLine commandLine = new GeneralCommandLine(BazelApplicationSettings.getInstance().getBazelRunPath());
                if (bazelAction.equals("test")) {
                    commandLine.addParameters("test", "--test_output=all", getBazelExecutablePath());
                }else {
                    commandLine.addParameters(bazelAction, getBazelExecutablePath());
                }
                commandLine.setCharset(Charset.forName("UTF-8"));
                commandLine.setWorkDirectory(environment.getProject().getBasePath());
                commandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);
                ProcessHandler osProcessHandler = null;
                if(bazelAction.equals("test")){
                    osProcessHandler = new BazelTestProcessHandler(commandLine, getBazelExecutablePath());
                }else{
                    osProcessHandler = new ColoredProcessHandler(commandLine);
                }
                return osProcessHandler;
            }

            @NotNull
            @Override
            public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
                if(!bazelAction.equals("test")){
                    return super.execute(executor, runner);
                }
                SMTRunnerConsoleProperties properties = new SMTRunnerConsoleProperties(thisConfiguration, "Bazel Tet Framework", executor);

                ProcessHandler handler = this.startProcess();
                BaseTestsOutputConsoleView consoleView2 = SMTestRunnerConnectionUtil.createAndAttachConsole("Bazel Tet Framework", handler, properties);
                ExecutionResult result = new DefaultExecutionResult(consoleView2, handler, createActions(consoleView2, handler, executor));

                return result;
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
