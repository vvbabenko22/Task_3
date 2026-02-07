package utils;

public class TestConfiguration {
    private static BrowserType browserType = BrowserType.YANDEX_BROWSER; // По умолчанию используем Chrome
    public static BrowserType getBrowserType() {
        return browserType;
    }
    public static void setBrowserType(BrowserType type) {
        browserType = type;
    }
}