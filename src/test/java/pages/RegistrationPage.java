package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegistrationPage {

    private final WebDriver driver;

    // Локаторы элементов

    public static final By InputName = By.xpath(".//fieldset[1]//input");
    public static final By InputEmail = By.xpath(".//fieldset[2]//input");
    public static final By InputButton = By.xpath(".//fieldset[3]//input");
    public static final By RegisterButton = By.xpath(".//button[text()='Зарегистрироваться']");
    public static final By ErrorPassword = By.xpath(".//fieldset[3]//p");
    public static By LoginLinkBack = By.xpath(".//*[text()='Войти']");

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
        fillField(InputName, name);
        fillField(InputEmail, email);
        fillField(InputButton, password);
    }

    // Регистрация нового пользователя
    public void register(String name, String email, String password) {
        inputRegistrationForm(name, email, password);
        click(RegisterButton);
    }

    // Получение текста ошибки для поля Пароль
    public String getPasswordErrorMessage() {
        return driver.findElement(ErrorPassword).getText();
    }

    // Переход по ссылке Войти
    public void clickAlreadyRegisteredLink() {
        click(LoginLinkBack);
    }
}