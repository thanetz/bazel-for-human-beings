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
