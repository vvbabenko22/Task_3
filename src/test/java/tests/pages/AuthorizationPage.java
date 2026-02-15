package tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AuthorizationPage {

    // Инициализация драйвера
    private final WebDriver driver;

    // Приватные локаторы элементов
    private final By emailInput = By.xpath(".//label[text()='Email']/../input");
    private final By passwordInput = By.xpath(".//label[text()='Пароль']/../input");
    private final By loginButton = By.xpath(".//button[text()='Войти']");
    private final By registerLink = By.xpath("(.//*[@class='Auth_link__1fOlj'])[1]");
    private final By forgotPasswordLink = By.xpath("(.//*[@class='Auth_link__1fOlj'])[2]");

    // Геттеры для локаторов
    public By getEmailInput() {
        return emailInput;
    }

    public By getPasswordInput() {
        return passwordInput;
    }

    public By getLoginButton() {
        return loginButton;
    }

    public By getRegisterLink() {
        return registerLink;
    }

    public By getForgotPasswordLink() {
        return forgotPasswordLink;
    }

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
        scrollToElement(getEmailInput());
        driver.findElement(getEmailInput()).clear();
        driver.findElement(getEmailInput()).sendKeys(email);
    }

    // Ввод Пароля
    public void enterPassword(String password) {
        scrollToElement(getPasswordInput());
        driver.findElement(getPasswordInput()).clear();
        driver.findElement(getPasswordInput()).sendKeys(password);
    }

    // Клик на кнопку Вход
    public void login() {
        scrollToElement(getLoginButton());
        driver.findElement(getLoginButton()).click();
    }

    // Клик по ссылке Восстановить пароль
    public void forgotPassword() {
        scrollToElement(getForgotPasswordLink());
        driver.findElement(getForgotPasswordLink()).click();
    }

    // Переход на форму регистрации
    public void clickRegisterLink() {
        scrollToElement(getRegisterLink());
        driver.findElement(getRegisterLink()).click();
    }

    // Заполнить и отправить данные авторизации
    public void submitLoginData(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        login();
    }

    // Переход на форму авторизации после восстановления пароля
    public void rememberPassword() {
        // Создаем временный экземпляр страницы восстановления пароля
        RecoverPasswordPage tempRecoverPasswordPage = new RecoverPasswordPage(driver);

        // Получаем нужный локатор через геттер
        By rememberPasswordLink = tempRecoverPasswordPage.getRememberPasswordLink();

        // Прокручиваем элемент в центр экрана
        scrollToElement(rememberPasswordLink);

        // Кликаем по кнопке
        driver.findElement(rememberPasswordLink).click();
    }
}