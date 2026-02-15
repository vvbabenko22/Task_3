package tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProfilePage {

    private final WebDriver driver;

    // Приватный локатор элемента
    private final By exitButtonLocator = By.xpath("//button[normalize-space(text())='Выход']");

    // Геттер для локатора кнопки "Выход"
    public By getExitButtonLocator() {
        return exitButtonLocator;
    }

    // Инициализация драйвера
    public ProfilePage(WebDriver driver) {
        this.driver = driver;
    }

    // Метод для выхода из личного кабинета
    public void logOut() {
        waitUntilVisible(getExitButtonLocator());
        driver.findElement(getExitButtonLocator()).click();
    }

    // Вспомогательный метод для ожидания видимости элемента
    private void waitUntilVisible(By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}