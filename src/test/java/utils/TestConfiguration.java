package utils;

public class TestConfiguration {
    private static BrowserType browserType = null;

    public static BrowserType getBrowserType() {
        return browserType;
    }

    public static void setBrowserType(BrowserType type) {
        browserType = type;
    }
}