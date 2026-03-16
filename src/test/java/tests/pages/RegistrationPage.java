package tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegistrationPage {

    private final WebDriver driver;

    // Приватные локаторы элементов
    private final By inputName = By.xpath(".//fieldset[1]//input");
    private final By inputEmail = By.xpath(".//fieldset[2]//input");
    private final By inputPassword = By.xpath(".//fieldset[3]//input");
    private final By registerButton = By.xpath(".//button[text()='Зарегистрироваться']");
    private final By errorPassword = By.xpath(".//fieldset[3]//p");
    private final By loginLinkBack = By.xpath(".//*[text()='Войти']");

    // Геттеры для локаторов
    public By getLoginLinkBack() {
        return loginLinkBack;
    }

    // Инициализация драйвера
    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
    }

    // Прокручиваем элемент в видимую область
    protected void scrollToElement(By by) {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Кликаем на элемент
    protected void click(By locator) {
        scrollToElement(locator);
        driver.findElement(locator).click();
    }

    // Заполняем поле значением
    protected void fillField(By locator, String value) {
        WebElement field = driver.findElement(locator);
        field.clear();
        field.sendKeys(value);
    }

    // Заполнение формы регистрации
    public void inputRegistrationForm(String name, String email, String password) {
        fillField(inputName, name);
        fillField(inputEmail, email);
        fillField(inputPassword, password);
    }

    // Регистрация нового пользователя
    public void register(String name, String email, String password) {
        inputRegistrationForm(name, email, password);
        click(registerButton);
    }

    // Получение текста ошибки для поля Пароль
    public String getPasswordErrorMessage() {
        return driver.findElement(errorPassword).getText();
    }

    // Переход по ссылке Войти
    public void clickAlreadyRegisteredLink() {
        click(loginLinkBack);
    }
}