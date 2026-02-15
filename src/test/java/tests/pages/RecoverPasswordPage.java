package tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RecoverPasswordPage {

    private final WebDriver driver;

    // Приватные локаторы элементов
    private final By restorePasswordLabel = By.xpath(".//*[text()='Восстановление пароля']");
    private final By rememberPasswordLink = By.xpath(".//a[text()='Войти']");

    // Геттеры для локаторов
    public By getRememberPasswordLink() {
        return rememberPasswordLink;
    }

    // Инициализация драйвера
    public RecoverPasswordPage(WebDriver driver) {
        this.driver = driver;
    }

    // Ожидание загрузки страницы восстановления пароля
    public void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(restorePasswordLabel));
    }
}