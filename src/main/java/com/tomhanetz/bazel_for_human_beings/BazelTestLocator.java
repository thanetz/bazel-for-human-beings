package com.tomhanetz.bazel_for_human_beings;

import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BazelTestLocator implements SMTestLocator {
    @NotNull
    @Override
    public List<Location> getLocation(@NotNull String s, @NotNull String s1, @NotNull Project project, @NotNull GlobalSearchScope globalSearchScope) {
        System.out.println(String.format("GetLocation: s=%s, s1=%s", s, s1));
        return new ArrayList<>();
    }

    @NotNull
    @Override
    public List<Location> getLocation(@NotNull String protocol, @NotNull String path, @Nullable String metainfo, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        System.out.println(String.format("protocol=%s, path=%s, metinfo=%s", protocol, path, metainfo));
        ArrayList<Location> locations = new ArrayList<>();
        PsiFile[] array = FilenameIndex.getFilesByName(project, path, scope);
        for (PsiFile file : array){
            System.out.println(String.format("found psi file: %s", file.getVirtualFile().getPath()));
        }
        return locations;
    }
}
