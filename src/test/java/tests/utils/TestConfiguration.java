package utils;

public class TestConfiguration {
    private static utils.BrowserType browserType = null;

    public static utils.BrowserType getBrowserType() {
        return browserType;
    }

    public static void setBrowserType(utils.BrowserType type) {
        browserType = type;
    }
}