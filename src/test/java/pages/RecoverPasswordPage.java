package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RecoverPasswordPage {

    private final WebDriver driver;

    // Локаторы элементов
    public static final By RESTORE_FORM_HEAD = By.xpath(".//*[text()='Восстановление пароля']");
    public static final By REMEMBER_PASSWORD_LINK = By.xpath(".//a[text()='Войти']");

    public RecoverPasswordPage(WebDriver driver) {
        this.driver = driver;
    }

    // Ожидание загрузки страницы восстановления пароля
    public void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(RESTORE_FORM_HEAD));
    }

}