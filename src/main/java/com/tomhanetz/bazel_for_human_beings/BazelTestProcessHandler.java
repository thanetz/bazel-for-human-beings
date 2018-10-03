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

INFO: Build completed, 1 test FAILED, 2 total actions=========================== test session starts ==============================
platform darwin -- Python 3.5.4, pytest-3.0.6, py-1.5.3, pluggy-0.4.0
rootdir: /private/var/tmp/_bazel_tomhanetz/d573ed7a900b4645b9d39d6578124456/execroot/armis/bazel-out/darwin-py3-opt/bin/armis/device_detection/user_agent_detector_test.runfiles/armis, inifile:
collected 20 items

armis/device_detection/user_agent_detector_test.py I1001 22:50:46.523694 4320654144 db.py:248] trying to connect to db
I1001 22:50:46.524055 4320654144 db.py:198] Configuring DB with uri: postgresql://postgres@127.0.0.1:57715/test
....W1001 22:50:48.492010 4320654144 user_agent_detector.py:341] OS name mismatch by user agent: 'some user agent', Original old_operating_system, Newly detected: new_operating_system
..FF............

 generated xml file: /private/var/tmp/_bazel_tomhanetz/d573ed7a900b4645b9d39d6578124456/execroot/armis/bazel-out/darwin-py3-opt/testlogs/armis/device_detection/user_agent_detector_test/test.xml
=================================== FAILURES ===================================
______________ TestUserAgentDetector.test_detect_device__no_model ______________

self = <armis.device_detection.user_agent_detector_test.TestUserAgentDetector object at 0x10ffff550>
mock_detector = <armis.device_detection.user_agent_detector.UserAgentDetector object at 0x10ffff8d0>
device = 7 | None | UNKNOWN | UNKNOWN | None
detected_device_output = {'brand': 'brand', 'success': True, 'type': 'computer'}

    def test_detect_device__no_model(self, mock_detector, device, detected_device_output):
        detected_device_output.pop("model")
        # pylint: disable=protected-access
        mock_detector._detect_device.return_value = detected_device_output

        detected_device = mock_detector.detect_device("", device)

>       assert detected_device["model"]+"tom2" == "%s Device" % detected_device_output["brand"]
E       assert 'brand Devicetom2' == 'brand Device'
E         - brand Devicetom2
E         ?             ----
E         + brand Device

armis/device_detection/user_agent_detector_test.py:151: AssertionError
_____________ TestUserAgentDetector.test_detect_device__good_data ______________

self = <armis.device_detection.user_agent_detector_test.TestUserAgentDetector object at 0x10ffecef0>
mock_detector = <armis.device_detection.user_agent_detector.UserAgentDetector object at 0x10ffff898>
device = 8 | None | UNKNOWN | UNKNOWN | None
detected_device_output = {'brand': 'brand', 'model': 'model', 'success': True, 'type': 'computer'}

    def test_detect_device__good_data(self, mock_detector, device, detected_device_output):
        # pylint: disable=protected-access
        mock_detector._detect_device.return_value = detected_device_output

        detected_device = mock_detector.detect_device("", device)

        assert detected_device["manufacturer"] == detected_device_output["brand"]
>       assert detected_device["model"] == detected_device_output["model"] + "tom"
E       assert 'model' == 'modeltom'
E         - model
E         + modeltom
E         ?      +++

armis/device_detection/user_agent_detector_test.py:160: AssertionError
===================== 2 failed, 18 passed in 5.11 seconds ======================
================================================================================
Target //armis/device_detection:user_agent_detector_test up-to-date:
  bazel-bin/armis/device_detection/user_agent_detector_test
INFO: Elapsed time: 7.739s, Critical Path: 7.42s
INFO: 1 process: 1 local.
INFO: Build completed, 1 test FAILED, 2 total actions
//armis/device_detection:user_agent_detector_test                        FAILED in 7.4s
  /private/var/tmp/_bazel_tomhanetz/d573ed7a900b4645b9d39d6578124456/execroot/armis/bazel-out/darwin-py3-opt/testlogs/armis/device_detection/user_agent_detector_test/test.log

INFO: Build completed, 1 test FAILED, 2 total actions


 */

public class BazelTestProcessHandler extends OSProcessHandler {

    private static final Pattern COLOR_CODES = Pattern.compile("\\[\\d+m");
    private static final Pattern ESCAPE_CHARS = Pattern.compile("[^"
            + "\u0009\r\n"
            + "\u0020-\uD7FF"
            + "\uE000-\uFFFD"
            + "\ud800\udc00-\udbff\udfff"
            + "]");
    private static final Pattern TEST_COUNT_PATTERN = Pattern.compile("Ran (\\d+) tests");
    private static final Pattern TEST_COUNT_PATTERN_PYTEST = Pattern.compile("collected (\\d+) items");
    private static final Pattern FAILED_TEST_PATTERN = Pattern.compile("FAIL: (.+) \\((.+)\\)");
    private static final Pattern FAILED_TEST_PATTERN_PYTEST = Pattern.compile("_+ (.+) _+");
    private static final Pattern END_FAILURES_SECTION_PYTEST = Pattern.compile("=+ .+ =+");
    private static final Pattern IGNORE_FALSE_POSITIVE_PATTERN = Pattern.compile("FAIL: (.+) \\(see (.+)\\)");
    private static final Pattern TEST_DURATION_PATTERN = Pattern.compile(".+ Critical Path: (\\d+\\.\\d*)s");

    private boolean testSuiteStarted = false;
    private String bazelCommand;

    private String failedTestTraceback = "";
    private String failedTestName = "";
    private String failedTestLastRow = "";
    private boolean isPyTest = false;
    private boolean isInFailedTest = false;
    private int testCount = 0;
    private int failedTestCount = 0;
    private float testDuration = 0;

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
        log.info("received text: " + text);

        if(text == null){
            return;
        }

        text = text.replaceAll(COLOR_CODES.pattern(), "").replaceAll(ESCAPE_CHARS.pattern(), "");

        Matcher ignoreFalsePositiveMatcher = IGNORE_FALSE_POSITIVE_PATTERN.matcher(text);
        if(ignoreFalsePositiveMatcher.find()){
            return;
        }

        if(failedTestHandler(text)){
            return;
        }


        Matcher testCountMatcher = TEST_COUNT_PATTERN.matcher(text);
        if (testCountMatcher.find()){
            testCount = Integer.parseInt(testCountMatcher.group(1));
            return;
        }

        Matcher pyTestCountMatcher = TEST_COUNT_PATTERN_PYTEST.matcher(text);
        if (pyTestCountMatcher.find()){
            testCount = Integer.parseInt(pyTestCountMatcher.group(1));
            return;
        }

        //Get Test Results
        Matcher testDurationMatcher = TEST_DURATION_PATTERN.matcher(text);
        if(testDurationMatcher.find()){
            testDuration = Float.parseFloat(testDurationMatcher.group(1));
        }

        if(text.startsWith(bazelCommand)){
            if (testCount - failedTestCount > 0){
                String testName = String.format("%d Successful tests", testCount - failedTestCount);
                super.notifyTextAvailable(TeamCityHandler.testStarted(testName)+"\r\n", ProcessOutputTypes.STDOUT);
                super.notifyTextAvailable(TeamCityHandler.testFinished(testName, (int)(testDuration * 1000)) + "\r\n", ProcessOutputTypes.STDOUT);
            }
        }
    }

    private void flushFailedTest(){
        isInFailedTest = false;
        failedTestCount += 1;
        super.notifyTextAvailable(TeamCityHandler.testFailed(failedTestName, failedTestLastRow, failedTestTraceback), ProcessOutputTypes.STDOUT);
        super.notifyTextAvailable(TeamCityHandler.testFinished(failedTestName, 0), ProcessOutputTypes.STDOUT);
        failedTestTraceback = "";
    }

    private boolean failedTestHandler(String text){
        Matcher failedTestMatcher = FAILED_TEST_PATTERN.matcher(text);
        Matcher pyTestFailedTestMatcher = FAILED_TEST_PATTERN_PYTEST.matcher(text);
        if(pyTestFailedTestMatcher.find()){
            isPyTest = true;
            failedTestMatcher = pyTestFailedTestMatcher;
        }
        if(failedTestMatcher.find(0)){
            if (isInFailedTest){
                flushFailedTest();
            }
            failedTestName = failedTestMatcher.group(1);
            super.notifyTextAvailable(TeamCityHandler.testStarted(failedTestName), ProcessOutputTypes.STDOUT);
            isInFailedTest = true;
            return true;
        }
        if(isInFailedTest){
            Matcher endOfFailuresSection = END_FAILURES_SECTION_PYTEST.matcher(text);
            if (!isPyTest && text.replaceAll("\n","").replaceAll("\r", "").isEmpty() || isPyTest && endOfFailuresSection.find()){
                flushFailedTest();
                return true;
            }
            failedTestLastRow = text.replaceAll("\n","").replaceAll("\r", "").replaceAll("'", "|'");
            failedTestTraceback += failedTestLastRow + "|r|n";
            return true;
        }
        return false;
    }
}
