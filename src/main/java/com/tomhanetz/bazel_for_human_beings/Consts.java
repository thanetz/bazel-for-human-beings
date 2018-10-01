package com.tomhanetz.bazel_for_human_beings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Consts {

    public static final Set<String> BINARY = new HashSet<>(Arrays.asList("py_binary", "java_binary"));

    public static final Set<String> LIBRARY = new HashSet<>(Arrays.asList("py_library", "java_library"));

    public static final Set<String> TEST = new HashSet<>(Arrays.asList("py_test", "java_test"));
}
