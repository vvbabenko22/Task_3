package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

public class DriverFactory {

    // Создаём драйвер браузеру
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

    // Создание драйвера для Chrome
    private static WebDriver createChromeDriver() {
        File chromedriverFile = new File("src/main/resources/webdrivers/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", chromedriverFile.getAbsolutePath());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        return new ChromeDriver(options);
    }

    // Создание драйвера для Яндекс.Браузера
    private static WebDriver createYandexDriver() {
        File yandexdriverFile = new File("src/main/resources/webdrivers/yandexdriver.exe");
        System.setProperty("webdriver.chrome.driver", yandexdriverFile.getAbsolutePath());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        return new ChromeDriver(options);
    }
}