package com.tomhanetz.bazel_for_human_beings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import com.intellij.openapi.diagnostic.Logger;

public class Utils {

    private static final Logger log = Logger.getInstance("com.tomhanetz.bazel_for_human_beings.Utils");

    public static String colorCodeRegex = "\\[\\d+m";

    public static String runCommand(String[] cmds, String directory) throws IOException {
        File directoryHandle = new File(directory);
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmds, null, directoryHandle);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader errInput = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        StringBuilder result = new StringBuilder();
        String outLine;
        while ((outLine = stdInput.readLine()) != null) {
            outLine = outLine.replaceAll(colorCodeRegex, "");
            result.append(outLine);
        }

        String errLine = errInput.readLine();
        errLine = errLine != null ? errLine.replaceAll(colorCodeRegex, "") : null;
        if (errLine != null && !errLine.trim().isEmpty()) {
            errLine = errLine.replaceAll(colorCodeRegex, "");
            StringBuilder error = new StringBuilder(errLine);
            while ((errLine = errInput.readLine()) != null) {
                errLine = errLine.replaceAll(colorCodeRegex, "");
                error.append(errLine);
            }
            log.warn("Command Line Error received: " + error.toString());
            throw new IOException(error.toString());
        }

        return result.toString();
    }
}
