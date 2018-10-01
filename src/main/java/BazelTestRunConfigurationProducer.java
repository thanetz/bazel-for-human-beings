import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

public class BazelTestRunConfigurationProducer extends RunConfigurationProducer<BazelTestRunConfiguration> {


    protected BazelTestRunConfigurationProducer(BazelTestConfigurationType configurationType) {
        super(configurationType);
    }

    private final Logger log = Logger.getInstance("Bazel Producer");

    @Override
    protected boolean setupConfigurationFromContext(BazelTestRunConfiguration bazelRunConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        String bazelPath = BazelApplicationSettings.getInstance().getBazelQueryPath();
        String directoryPath = configurationContext.getLocation().getVirtualFile().getParent().getPath();
        String name = configurationContext.getLocation().getVirtualFile().getName();
        try {

            // returns //a/b/c:my_exec
            String bazelExecutablePath = Utils.runCommand(new String[]{bazelPath, "query",
                    "attr('srcs', '" + name + "', ':*')"}, directoryPath);
            bazelRunConfiguration.setName("BAZEL: " + bazelExecutablePath);
            log.info("Successfully received bazel executable path of: " + bazelExecutablePath);

            // returns py_binary rule //a/b/c:my_exec
            String bazelTypeResult = Utils.runCommand(new String[]{bazelPath, "query",
                    "kind(rule, "  + bazelExecutablePath + ")", "--output", "label_kind"}, directoryPath);
            String bazelType = bazelTypeResult.split(" ")[0];
            log.info("Successfully received bazel kind of: " + bazelType);

            if (Consts.TEST.contains(bazelType)){
                bazelRunConfiguration.setName("BAZEL test " + " - " + bazelExecutablePath);
            }else{
                return false;
            }

            bazelRunConfiguration.setBazelExecutablePath(bazelExecutablePath);
            return true;
        }catch (Exception exception){
            log.warn("Received an exception: " + exception.getMessage());
            return false;
        }
    }

    @Override
    public boolean isConfigurationFromContext(BazelTestRunConfiguration bazelRunConfiguration, ConfigurationContext configurationContext) {
        return false;
    }
}
