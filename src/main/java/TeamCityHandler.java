public class TeamCityHandler {

    //                        ##teamcity[testSuiteStarted name='TomTestSuite2']
//                        ##teamcity[testStarted name='package_or_namespace.ClassName.TestName']
//                        ##teamcity[testFailed name='package_or_namespace.ClassName.TestName' message='The number should be 20000' details='junit.framework.AssertionFailedError: expected:<20000> but was:<10000>|n|r    at junit.framework.Assert.fail(Assert.java:47)|n|r    at junit.framework.Assert.failNotEquals(Assert.java:280)|n|r...']
//                        ##teamcity[testFinished name='package_or_namespace.ClassName.TestName']
//                        ##teamcity[testSuiteFinished name='suiteName']


    public static String testSuiteStarted(String testSuiteName){
        return String.format("##teamcity[testSuiteStarted name='%s']", testSuiteName);
    }

    public static String testStarted(String testName){
        return String.format("##teamcity[testStarted name='%s']", testName);
    }

    public static String testFailed(String testName, String message, String detailes){
        return String.format("##teamcity[testFailed name='%s' message='%s' details='%s']", testName, message, detailes);
    }

    public static String testFinished(String testName, int duration){
        return String.format("##teamcity[testFinished name='%s' duration='%d']", testName, duration);
    }

    public static String testSuiteFinished(String testSuiteName){
        return String.format("##teamcity[testSuiteFinished name='%s']", testSuiteName);
    }
}
