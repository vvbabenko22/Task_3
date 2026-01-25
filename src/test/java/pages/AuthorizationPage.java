package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class AuthorizationPage {

    private final WebDriver driver;

    // Локаторы элементов
    public static final By EMAIL_INPUT = By.xpath(".//label[text()='Email']/../input");
    public static final By PASSWORD_INPUT = By.xpath(".//label[text()='Пароль']/../input");
    public static final By LOGIN_BUTTON = By.xpath(".//button[text()='Войти']");
    public static final By REGISTER_LINK = By.className("Auth_link__1fOlj");
    public static final By FORGOT_PASSWORD_LINK = By.xpath(".//a[text()='Восстановить пароль']");

    public AuthorizationPage(WebDriver driver) {
        this.driver = driver;
    }

    // Прокручивает элемент в центр экрана
    protected void scrollToElement(By by) {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Методы для действий над элементами

    // Ввод Email
    public void enterEmail(String email) {
        scrollToElement(EMAIL_INPUT);
        driver.findElement(EMAIL_INPUT).clear();
        driver.findElement(EMAIL_INPUT).sendKeys(email);
    }

    // Ввод Пароля
    public void enterPassword(String password) {
        scrollToElement(PASSWORD_INPUT);
        driver.findElement(PASSWORD_INPUT).clear();
        driver.findElement(PASSWORD_INPUT).sendKeys(password);
    }

    // Нажатие на кнопку Вход
    public void login() {
        scrollToElement(LOGIN_BUTTON);
        driver.findElement(LOGIN_BUTTON).click();
    }

    // Клик по ссылке Зарегистрироваться
    public void register() {
        scrollToElement(REGISTER_LINK);
        driver.findElement(REGISTER_LINK).click();
    }

    // Клик по ссылке Восстановить пароль
    public void forgotPassword() {
        scrollToElement(FORGOT_PASSWORD_LINK);
        driver.findElement(FORGOT_PASSWORD_LINK).click();
    }

    // Заполнить и отправить данные авторизации
    public void submitLoginData(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        login();
    }
}