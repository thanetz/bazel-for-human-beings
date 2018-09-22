import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import com.intellij.openapi.diagnostic.Logger;

public class Utils {

    private static final Logger log = Logger.getInstance("Utils");

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
            if (outLine.endsWith("[0m")){
                outLine = outLine.substring(0, outLine.length() - 3);
            }
            result.append(outLine);
        }

        String errLine = errInput.readLine();
        if (errLine != null && !errLine.isEmpty() && !errLine.trim().equals("[0m")) {
            StringBuilder error = new StringBuilder(errLine);
            while ((errLine = errInput.readLine()) != null) {
                if (errLine.endsWith("[0m")){
                    errLine = outLine.substring(0, errLine.length() - 3);
                }
                error.append(errLine);
            }
            log.warn("Command Line Error received: " + error.toString());
            throw new IOException(error.toString());
        }

        return result.toString();
    }
}
