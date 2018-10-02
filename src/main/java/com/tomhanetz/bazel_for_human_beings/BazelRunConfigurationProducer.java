package com.tomhanetz.bazel_for_human_beings;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BazelRunConfigurationProducer extends RunConfigurationProducer<BazelRunConfiguration> {


    protected BazelRunConfigurationProducer(BazelConfigurationType configurationType) {
        super(configurationType);
        cache = new HashMap<>();
    }

    private final Logger log = Logger.getInstance("Bazel Producer");

    private Map<String, String> cache;

    private String getBazelPath(ConfigurationContext configurationContext) throws IOException {
        String bazelPath = BazelApplicationSettings.getInstance().getBazelQueryPath();
        if (configurationContext.getLocation() == null || configurationContext.getLocation().getVirtualFile() == null){
            return null;
        }
        String directoryPath = configurationContext.getLocation().getVirtualFile().getParent().getPath();
        String name = configurationContext.getLocation().getVirtualFile().getName();

        if (cache.containsKey(directoryPath+":::"+name)){
            return cache.get(directoryPath+":::"+name);
        }

        String bazelExecutablePaths = Utils.runCommand(new String[]{bazelPath, "query",
                "attr('srcs', '" + name + "', ':*')"}, directoryPath);

        //get only first appearance
        String[] parts = bazelExecutablePaths.split("//");
        if (parts.length == 1){
            return null;
        }
        String bazelExecutablePath = "//" + bazelExecutablePaths.split("//")[1];

        cache.put(directoryPath+":::"+name, bazelExecutablePath);

        return bazelExecutablePath;
    }

    @Override
    protected boolean setupConfigurationFromContext(BazelRunConfiguration bazelRunConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        try {
            // returns //a/b/c:my_exec
            String bazelExecutablePath = getBazelPath(configurationContext);
            bazelRunConfiguration.setName("BAZEL: " + bazelExecutablePath);
            log.info("Successfully received bazel executable path of: " + bazelExecutablePath);

            bazelRunConfiguration.setBazelExecutablePath(bazelExecutablePath);
            return true;
        }catch (Exception exception){
            log.warn("Received an exception: " + exception.getMessage());
            return false;
        }
    }

    @Override
    public boolean isConfigurationFromContext(BazelRunConfiguration bazelRunConfiguration, ConfigurationContext configurationContext) {
        try {
            String bazelPath = getBazelPath(configurationContext);
            String runConfigurationBazelPath = bazelRunConfiguration.getBazelExecutablePath();
            return runConfigurationBazelPath != null && runConfigurationBazelPath.equals(bazelPath);
        }catch (IOException exception){
            log.warn("Received an io exception: " + exception.getMessage());
            return false;
        }
    }
}
