package com.tomhanetz.bazel_for_human_beings;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
==================== Test output for //main/tests:testim:
DEBUG: C:/users/tom/_bazel_tom/b7obspoi/external/bazel_tools/tools/cpp/lib_cc_configure.bzl:115:5:
Auto-Configuration Warning: 'BAZEL_VC' is not set, start looking for the latest Visual C++ installed.
DEBUG: C:/users/tom/_bazel_tom/b7obspoi/external/bazel_tools/tools/cpp/lib_cc_configure.bzl:115:5:
Auto-Configuration Warning: Looking for VS%VERSION%COMNTOOLS environment variables, eg. VS140COMNTOOLS
DEBUG: C:/users/tom/_bazel_tom/b7obspoi/external/bazel_tools/tools/cpp/lib_cc_configure.bzl:115:5:
Auto-Configuration Warning: Visual C++ build tools found at C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\
INFO: Analysed target //main/tests:testim (0 packages loaded).
INFO: Found 1 test target...
FAIL: //main/tests:testim (see C:/users/tom/_bazel_tom/b7obspoi/execroot/__main__/bazel-out/x64_windows-fastbuild/testlogs/main/tests/testim/test.log)
INFO: From Testing //main/tests:testim:
==================== Test output for //main/tests:testim:
FF
======================================================================
FAIL: test_fact (__main__.TestFactorial)
----------------------------------------------------------------------
Traceback (most recent call last):
  File "\\?\C:\Users\tom\AppData\Local\Temp\Bazel.runfiles_7dezidl1\runfiles\__main__\main\tests\testim.py", line 18, in test_fact
    self.assertEqual(res, 31)
AssertionError: 32 != 31

======================================================================
FAIL: test_fact_Again (__main__.TestFactorial)
----------------------------------------------------------------------
Traceback (most recent call last):
  File "\\?\C:\Users\tom\AppData\Local\Temp\Bazel.runfiles_7dezidl1\runfiles\__main__\main\tests\testim.py", line 26, in test_fact_Again
    self.assertEqual(res, 1025)
AssertionError: 1024 != 1025

----------------------------------------------------------------------
Ran 2 tests in 3.506s

FAILED (failures=2)
se'emek
external/bazel_tools/tools/test/test-setup.sh: line 160: perl: command not found
================================================================================
Target //main/tests:testim up-to-date:
  C:/users/tom/_bazel_tom/b7obspoi/execroot/__main__/bazel-out/x64_windows-fastbuild/bin/main/tests/testim.exe
  C:/users/tom/_bazel_tom/b7obspoi/execroot/__main__/bazel-out/x64_windows-fastbuild/bin/main/tests/testim.zip
INFO: Elapsed time: 6.046s, Critical Path: 5.29s
INFO: 1 process: 1 local.
INFO: Build completed, 1 test FAILED, 2 total actions
//main/tests:testim                                                      FAILED in 5.2s
  C:/users/tom/_bazel_tom/b7obspoi/execroot/__main__/bazel-out/x64_windows-fastbuild/testlogs/main/tests/testim/test.log

INFO: Build completed, 1 test FAILED, 2 total actions

C:\Users\tom\PycharmProjects\testim>




 */

public class BazelTestProcessHandler extends OSProcessHandler {

    private static final Pattern TEST_COUNT_PATTERN = Pattern.compile("Ran (\\d+) tests");
    private static final Pattern FAILED_TEST_PATTERN = Pattern.compile("FAIL: (.+) \\((.+)\\)");
    private static final Pattern IGNORE_FALSE_POSITIVE_PATTERN = Pattern.compile("FAIL: (.+) \\(see (.+)\\)");

    private boolean testSuiteStarted = false;
    private String bazelCommand;

    private String failedTestTraceback = "";
    private String failedTestName = "";
    private String failedTestLastRow = "";
    private boolean isInTraceback = false;
    private boolean finishedTraceback = false;
    private boolean isInFailedTest = false;
    private int testCount = 0;
    private int failedTestCount = 0;

    private final Logger log = Logger.getInstance("Bazel com.tomhanetz.bazel_for_human_beings.BazelTestProcessHandler");

    public BazelTestProcessHandler(@NotNull GeneralCommandLine commandLine, String bazelExecCommand) throws ExecutionException {
        super(commandLine);
        bazelCommand = bazelExecCommand;
    }

    @Override
    public void notifyTextAvailable(@NotNull String text, @NotNull Key outputType) {
        if (!testSuiteStarted) {
            super.notifyTextAvailable(TeamCityHandler.testSuiteStarted(bazelCommand), ProcessOutputTypes.STDOUT);
            testSuiteStarted = true;
        }

        parseCommands(text, outputType);
    }

    @Override
    protected void onOSProcessTerminated(int exitCode) {
        super.notifyTextAvailable(TeamCityHandler.testSuiteFinished(bazelCommand), ProcessOutputTypes.STDOUT);
        super.onOSProcessTerminated(exitCode);
    }

    private void parseCommands(String text, @NotNull Key outputType){
        log.debug("received text: " + text);

        if(text == null){
            return;
        }

        Matcher ignoreFalsePositiveMatcher = IGNORE_FALSE_POSITIVE_PATTERN.matcher(text);
        if(ignoreFalsePositiveMatcher.find()){
            return;
        }

        Matcher failedTestMatcher = FAILED_TEST_PATTERN.matcher(text);
        if(failedTestMatcher.find()){
            failedTestName = failedTestMatcher.group(1);
            super.notifyTextAvailable(TeamCityHandler.testStarted(failedTestName), ProcessOutputTypes.STDOUT);
            isInFailedTest = true;
            return;
        }
        if(isInFailedTest){
            if (text.replaceAll("\n","").replaceAll("\r", "").isEmpty()){
                isInFailedTest = false;
                failedTestCount += 1;
                super.notifyTextAvailable(TeamCityHandler.testFailed(failedTestName, failedTestLastRow, failedTestTraceback), ProcessOutputTypes.STDOUT);
                super.notifyTextAvailable(TeamCityHandler.testFinished(failedTestName, 0), ProcessOutputTypes.STDOUT);
                failedTestTraceback = "";
                return;
            }
            failedTestLastRow = text.replaceAll("\n","").replaceAll("\r", "");
            failedTestTraceback += failedTestLastRow + "|r|n";
            return;
        }

        Matcher testCountMatcher = TEST_COUNT_PATTERN.matcher(text);
        if (testCountMatcher.find()){
            testCount = Integer.parseInt(testCountMatcher.group(1));
            isInTraceback = false;
            finishedTraceback = true;
            return;
        }

        //Get Test Results
        if(text.startsWith(bazelCommand)){
            String[] parts = text.split("in ");
            String runTime = parts[parts.length - 1];
            float testDuration = Float.parseFloat(runTime.split("s")[0]);
            if (testCount - failedTestCount > 0){
                String testName = String.format("%d Successful tests", testCount - failedTestCount);
                super.notifyTextAvailable(TeamCityHandler.testStarted(testName), ProcessOutputTypes.STDOUT);
                super.notifyTextAvailable(TeamCityHandler.testFinished(testName, (int)(testDuration * 1000)), ProcessOutputTypes.STDOUT);
            }
        }
    }
}
