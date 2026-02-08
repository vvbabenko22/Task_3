package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AuthorizationPage {

    private final WebDriver driver;

    // Локаторы элементов
    public static final By EmailInput = By.xpath(".//label[text()='Email']/../input");
    public static final By PasswordInput = By.xpath(".//label[text()='Пароль']/../input");
    public static final By LoginButton = By.xpath(".//button[text()='Войти']");
    public static final By RegisterLink = By.xpath("(.//*[@class='Auth_link__1fOlj'])[1]");
    public static final By ForgotPasswordLink = By.xpath("(.//*[@class='Auth_link__1fOlj'])[2]");

    public AuthorizationPage(WebDriver driver) {
        this.driver = driver;
    }

    // Прокручивает элемент в центр экрана
    protected void scrollToElement(By by) {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Методы для работы с формой авторизации
    // Ввод Email
    public void enterEmail(String email) {
        scrollToElement(EmailInput);
        driver.findElement(EmailInput).clear();
        driver.findElement(EmailInput).sendKeys(email);
    }

    // Ввод Пароля
    public void enterPassword(String password) {
        scrollToElement(PasswordInput);
        driver.findElement(PasswordInput).clear();
        driver.findElement(PasswordInput).sendKeys(password);
    }

    // Клик на кнопку Вход
    public void login() {
        scrollToElement(LoginButton);
        driver.findElement(LoginButton).click();
    }

    // Клик по ссылке Восстановить пароль
    public void forgotPassword() {
        scrollToElement(ForgotPasswordLink);
        driver.findElement(ForgotPasswordLink).click();
    }

    // Переход на форму авторизации после восстановления пароля
    public void rememberPassword() {
        scrollToElement(RecoverPasswordPage.RememberPassword);
        driver.findElement(RecoverPasswordPage.RememberPassword).click();
    }

    // Заполнить и отправить данные авторизации
    public void submitLoginData(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        login();
    }

    // Переход на форму регистрации
    public void clickRegisterLink() {

    }

    public void clickLoginLinkBack() {
    }
}