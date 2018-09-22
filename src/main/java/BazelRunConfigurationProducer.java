import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.diagnostic.Logger;

public class BazelRunConfigurationProducer extends RunConfigurationProducer<BazelRunConfiguration> {


    protected BazelRunConfigurationProducer(BazelConfigurationType configurationType) {
        super(configurationType);
    }

    private final Logger log = Logger.getInstance("Bazel Producer");

    private String getBazelPath(ConfigurationContext configurationContext) {
        Location location = configurationContext.getLocation();
        if (location == null){
            return null;
        }
        VirtualFile currentFile = configurationContext.getLocation().getVirtualFile();
        if (currentFile == null){
            return null;
        }
        StringBuilder fullPath = new StringBuilder(":" + currentFile.getNameWithoutExtension());
        boolean foundFullPath = false;
        boolean error = false;
        while (!foundFullPath && !error){
            VirtualFile directory = currentFile.getParent();
            boolean hasWorkspace = false;
            boolean hasBuild = false;
            for (VirtualFile child: directory.getChildren()) {
                if (child.getName().equals("WORKSPACE")){
                    hasWorkspace = true;
                    break;
                }else if(child.getName().equals("BUILD")){
                    hasBuild = true;
                }
            }
            if (hasWorkspace){
                fullPath.insert(0, "/");
                foundFullPath = true;
            }else if(hasBuild){
                fullPath.insert(0, "/" + directory.getName());
            }
            currentFile = directory;
            error = !hasWorkspace && !hasBuild;
        }
        if (error){
            return null;
        }
        return fullPath.toString();
    }

    @Override
    protected boolean setupConfigurationFromContext(BazelRunConfiguration bazelRunConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
//        String bazelPath =  getBazelExecutablePath(configurationContext);
//        if(bazelPath != null){
//            bazelRunConfiguration.setBazelExecutablePath(bazelPath);
//            String[] bazelName = bazelPath.split(":");
//            bazelRunConfiguration.setName("Bazel " + bazelName[bazelName.length - 1]);
//            return true;
//        }
//        return false;
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

            if (Consts.BINARY.contains(bazelType)){
                bazelRunConfiguration.setName("BAZEL run " + " - " + bazelExecutablePath);
                bazelRunConfiguration.setAction("run");
            }else if (Consts.TEST.contains(bazelType)){
                bazelRunConfiguration.setName("BAZEL test " + " - " + bazelExecutablePath);
                bazelRunConfiguration.setAction("test");
            }else if (Consts.LIBRARY.contains(bazelType)){
                bazelRunConfiguration.setName("BAZEL build " + " - " + bazelExecutablePath);
                bazelRunConfiguration.setAction("build");
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
    public boolean isConfigurationFromContext(BazelRunConfiguration bazelRunConfiguration, ConfigurationContext configurationContext) {
        String bazelPath = getBazelPath(configurationContext);
        String runConfigurationBazelPath = bazelRunConfiguration.getBazelExecutablePath();
        return runConfigurationBazelPath != null && runConfigurationBazelPath.equals(bazelPath);
    }
}
