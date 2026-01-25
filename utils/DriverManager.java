package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverManager {

    private static volatile WebDriver webDriver;

    private DriverManager() {}

    // Получаем экземпляр драйвера

    public static synchronized WebDriver getDriver() {
        if (webDriver == null || ((RemoteWebDriver) webDriver).getSessionId() == null) {
            webDriver = DriverFactory.createDriver();
        }
        return webDriver;
    }

    // Закрываем драйвер

    public static void quitDriver() {
        if (webDriver != null) {
            webDriver.quit();
            webDriver = null;
        }
    }
}