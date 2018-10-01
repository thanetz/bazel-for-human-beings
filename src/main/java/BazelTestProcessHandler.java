import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

/*
==================== Test output for //main/tests:testim:
.F
======================================================================
FAIL: test_fact_Again (__main__.TestFactorial)
----------------------------------------------------------------------
Traceback (most recent call last):
  File "\\?\C:\Users\tom\AppData\Local\Temp\Bazel.runfiles_6wistid1\runfiles\__main__\main\tests\testim.py", line 22, in test_fact_Again
    self.assertEqual(res, 1025)
AssertionError: 1024 != 1025

----------------------------------------------------------------------
Ran 2 tests in 0.001s

FAILED (failures=1)
external/bazel_tools/tools/test/test-setup.sh: line 160: perl: command not found
================================================================================
Target //main/tests:testim up-to-date:
  C:/users/tom/_bazel_tom/b7obspoi/execroot/__main__/bazel-out/x64_windows-fastbuild/bin/main/tests/testim.exe
  C:/users/tom/_bazel_tom/b7obspoi/execroot/__main__/bazel-out/x64_windows-fastbuild/bin/main/tests/testim.zip
INFO: Elapsed time: 1.743s, Critical Path: 1.46s
INFO: 1 process: 1 local.
INFO: Build completed, 1 test FAILED, 2 total actions
//main/tests:testim                                                      FAILED in 1.4s
  C:/users/tom/_bazel_tom/b7obspoi/execroot/__main__/bazel-out/x64_windows-fastbuild/testlogs/main/tests/testim/test.log

 */

public class BazelTestProcessHandler extends OSProcessHandler {
    private boolean testSuiteStarted = false;
    private String testSuiteName = "TestSuite";
    private String testName = "Test";
    private String bazelCommand;

    private String testTraceback = "";
    private float testDuration = 0;
    private boolean isInTraceback = false;
    private boolean finishedTraceback = false;

    private final Logger log = Logger.getInstance("Bazel BazelTestProcessHandler");

    public BazelTestProcessHandler(@NotNull GeneralCommandLine commandLine, String bazelExecCommand) throws ExecutionException {
        super(commandLine);
        bazelCommand = bazelExecCommand;
        String[] parts = bazelExecCommand.split(":");
        if (parts.length == 2){
            testSuiteName = bazelExecCommand.split(":")[0];
            testName = bazelExecCommand.split(":")[1];
        }

    }

    @Override
    public void notifyTextAvailable(@NotNull String text, @NotNull Key outputType) {
        if (!testSuiteStarted) {
            super.notifyTextAvailable(TeamCityHandler.testSuiteStarted(testSuiteName), ProcessOutputTypes.STDOUT);
            super.notifyTextAvailable(TeamCityHandler.testStarted(testName), ProcessOutputTypes.STDOUT);
            testSuiteStarted = true;
        }

        parseCommands(text, outputType);
    }

    @Override
    protected void onOSProcessTerminated(int exitCode) {
        super.notifyTextAvailable(TeamCityHandler.testSuiteFinished(testSuiteName), ProcessOutputTypes.STDOUT);
        super.onOSProcessTerminated(exitCode);
    }

    private void parseCommands(String text, @NotNull Key outputType){
//        if (outputType != ProcessOutputTypes.STDOUT) {
//            super.notifyTextAvailable(text, outputType);
//            return;
//        }
        log.info("received text: " + text);

        if(text == null){
            return;
        }

//        //Get Test Traceback
        if(!finishedTraceback && text.startsWith("===========================")){
            isInTraceback = true;
            return;
        }

        if(isInTraceback){
            if (text.startsWith("Ran ")){
                isInTraceback = false;
                finishedTraceback = true;
                return;
            }
            testTraceback += text.replaceAll("\n","").replaceAll("\r", "") + "|r|n";
            return;
        }

        //Get Test Results
        if(text.startsWith(bazelCommand)){
            String[] parts = text.split("in ");
            String runTime = parts[parts.length - 1];
            testDuration = Float.parseFloat(runTime.split("s")[0]);
            if(text.contains("(cached)")){
                super.notifyTextAvailable("Cached Results", ProcessOutputTypes.STDOUT);
            }
            if (text.contains("FAILED")){
                super.notifyTextAvailable(TeamCityHandler.testFailed(testName, "Test Failed", testTraceback), ProcessOutputTypes.STDOUT);
            }
            super.notifyTextAvailable(TeamCityHandler.testFinished(testName, (int)(testDuration * 1000)), ProcessOutputTypes.STDOUT);
        }
    }
}
