package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RecoverPasswordPage {

    private final WebDriver driver;

    // Локаторы элементов
    public static final By RESTORE_FORM_HEAD = By.xpath(".//*[text()='Восстановление пароля']");
    public static final By REMEMBER_PASSWORD_LINK = By.xpath(".//a[text()='Войти']");

    public RecoverPasswordPage(WebDriver driver) {
        this.driver = driver;
    }

    // Прокручивает элемент в центр окна браузера
    protected void scrollToElement(By by) {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Клик на элемент
    protected void click(By locator) {
        scrollToElement(locator);
        driver.findElement(locator).click();
    }

    // Ожидание загрузки страницы восстановления пароля
    public void waitForPageLoad() {
        scrollToElement(RESTORE_FORM_HEAD);
    }

    // Нажатие на ссылку "Войти"
    public void clickRememberedPassword() {
        click(REMEMBER_PASSWORD_LINK);
    }
}