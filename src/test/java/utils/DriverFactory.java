package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

public class DriverFactory {

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
        File yandexdriverFile = new File("src/main/resources/webdrivers/chromedriver.exe"); // Используем тот же драйвер, что и для Chrome
        System.setProperty("webdriver.chrome.driver", yandexdriverFile.getAbsolutePath()); // Назначение водителя Chrome
        ChromeOptions options = new ChromeOptions();
        options.setBinary(new File("C:/Program Files/Yandex/YandexBrowser/application/browser.exe")); // Укажите путь к Яндекс-браузеру
        options.addArguments("--start-maximized");
        return new ChromeDriver(options);
    }
}