package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class DriverFactory {

    // Создаём драйвер согласно браузеру

    public static WebDriver createDriver() {
        switch (TestConfiguration.getBrowserType()) {
            case CHROME:
                return createChromeDriver();
            case YANDEX_BROWSER:
                return createYandexDriver();
            default:
                throw new IllegalArgumentException("Unsupported browser type!");
        }
    }

    private static WebDriver createChromeDriver() {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver"); // укажите путь к вашему chromedriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        return new ChromeDriver(options);
    }

    private static WebDriver createYandexDriver() {
        System.setProperty("webdriver.chrome.driver", "/path/to/yanderexdriver"); // укажите путь к вашему yanderexdriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        return new ChromeDriver(options);
    }
}