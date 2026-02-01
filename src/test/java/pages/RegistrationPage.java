package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegistrationPage {

    private final WebDriver driver;

    // Локаторы элементов
    public static final By REGISTER_FORM_HEADING = By.xpath(".//*[text()='Регистрация']");
    public static final By INPUT_NAME = By.xpath(".//fieldset[1]//input");
    public static final By INPUT_EMAIL = By.xpath(".//fieldset[2]//input");
    public static final By INPUT_PASSWORD = By.xpath(".//fieldset[3]//input");
    public static final By REGISTER_BUTTON = By.xpath(".//button[text()='Зарегистрироваться']");
    public static final By ERROR_PASSWORD = By.xpath(".//fieldset[3]//p");
    public static By LOGIN_LINK_BACK = By.xpath(".//*[text()='Войти']");

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
    }

    // Прокручивает элемент в видимую область
    protected void scrollToElement(By by) {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Кликает на элемент
    protected void click(By locator) {
        scrollToElement(locator);
        driver.findElement(locator).click();
    }

    // Заполняет поле значением
    protected void fillField(By locator, String value) {
        WebElement field = driver.findElement(locator);
        field.clear();
        field.sendKeys(value);
    }

    // Заполнение формы регистрации
    public void inputRegistrationForm(String name, String email, String password) {
        fillField(INPUT_NAME, name);
        fillField(INPUT_EMAIL, email);
        fillField(INPUT_PASSWORD, password);
    }

    // Регистрация нового пользователя
    public void register(String name, String email, String password) {
        inputRegistrationForm(name, email, password);
        click(REGISTER_BUTTON);
    }

    // Получение текста ошибки для поля Пароль
    public String getPasswordErrorMessage() {
        return driver.findElement(ERROR_PASSWORD).getText();
    }

    // Переход по ссылке Войти
    public void clickAlreadyRegisteredLink() {
        click(LOGIN_LINK_BACK);
    }
}