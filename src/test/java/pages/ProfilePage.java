package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProfilePage {

    private final WebDriver driver;

    // Локаторы элементов
    public static final By PROFILE_TEXT = By.xpath(".//*[contains(text(), 'В этом разделе вы можете изменить свои персональные данные')]");
    public static final By NAME_INPUT = By.xpath(".//li[1]//input");
    public static final By EMAIL_INPUT = By.xpath(".//li[2]//input");

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
    }

    // Прокручивает элемент в центр окна браузера
    protected void scrollToElement(By by) {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Возвращает значение атрибута input
    private String getInputValue(By locator) {
        return driver.findElement(locator).getAttribute("value");
    }

    // Метод ожидания появления текста на странице профиля
    public void waitProfilePageLoad() {
        scrollToElement(PROFILE_TEXT);
    }

    // Получить значение поля имени
    public String getNameText() {
        return getInputValue(NAME_INPUT);
    }

    // Получить значение поля электронной почты
    public String getEmailText() {
        return getInputValue(EMAIL_INPUT);
    }
}