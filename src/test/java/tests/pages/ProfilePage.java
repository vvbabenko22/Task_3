package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ProfilePage {

    private final WebDriver driver;

    // Локатор кнопки "Выход"
    private final By locatorExitButton = By.xpath("//button[normalize-space(text())='Выход']");

    // Метод для выхода из Личного кабинета
    public void logOut() {
        waitUntilVisible(locatorExitButton);
        driver.findElement(locatorExitButton).click();
    }

    // Вспомогательный метод для ожидания видимости элемента
    private void waitUntilVisible(By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
    }
}