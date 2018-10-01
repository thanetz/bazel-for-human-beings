package com.tomhanetz.bazel_for_human_beings;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.tomhanetz.bazel_for_human_beings.BazelApplicationSettings",
        storages = {
                @Storage("bazel_4_human_beings_settings.xml")
        }
)
public class BazelApplicationSettings implements PersistentStateComponent<BazelApplicationSettings> {

    private String bazelQueryPath = "/usr/local/bin/bazel";
    private String bazelRunPath = "/usr/local/bin/bazel";

    @Nullable
    @Override
    public BazelApplicationSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull BazelApplicationSettings bazelSettings) {
        XmlSerializerUtil.copyBean(bazelSettings, this);
    }

    public String getBazelQueryPath() {
        return bazelQueryPath;
    }

    public void setBazelQueryPath(String bazelQueryPath) {
        this.bazelQueryPath = bazelQueryPath;
    }

    public static BazelApplicationSettings getInstance() {
        return ServiceManager.getService(BazelApplicationSettings.class);
    }

    public String getBazelRunPath() {
        return bazelRunPath;
    }

    public void setBazelRunPath(String bazelRunPath) {
        this.bazelRunPath = bazelRunPath;
    }
}
