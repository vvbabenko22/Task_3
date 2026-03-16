package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.io.File;

public class DriverFactory {

    // Драйвер на основе выбранного браузера
    public static WebDriver createDriver() {
        switch (TestConfiguration.getBrowserType()) {
            case CHROME:
                return createChromeDriver();
            case YANDEX_BROWSER:
                return createYandexDriver();
            default:
                throw new IllegalArgumentException("Unsupported browser type: " + TestConfiguration.getBrowserType());
        }
    }

    // Драйвер для Google Chrome
    private static WebDriver createChromeDriver() {
        File driverFile = new File("src/main/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", driverFile.getAbsolutePath());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        return new ChromeDriver(options);
    }

    // Драйвер для Яндекс.Браузера
    private static WebDriver createYandexDriver() {
        File driverFile = new File("src/main/resources/yandexdriver.exe");
        System.setProperty("webdriver.chrome.driver", driverFile.getAbsolutePath());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu"); // Отключение GPU ускорения
        options.addArguments("--disable-software-rasterizer"); // Отключение рендеринга
        options.addArguments("--disable-infobars"); // Скрывает панель уведомлений
        options.addArguments("--disable-extensions"); // Отключает расширения
        return new ChromeDriver(options);
    }
}