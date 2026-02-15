package tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MainPage {

    private WebDriver driver;

    // Приватные локаторы элементов
    private final By enterAccountButton = By.xpath("//button[contains(@class, 'button_button__33qZ0') and text()='Войти в аккаунт']");
    private final By personalAccountButton = By.xpath("//p[contains(@class, 'AppHeader_header__linkText__3q_va') and normalize-space()='Личный Кабинет']");
    private final By checkoutButton = By.xpath("//button[.='Оформить заказ']");
    private final By bunsTab = By.xpath(".//*[@class='text text_type_main-default' and contains(.,'Булки')]");
    private final By saucesTab = By.xpath(".//*[@class='text text_type_main-default' and contains(.,'Соусы')]");
    private final By fillingsTab = By.xpath(".//*[@class='text text_type_main-default' and contains(.,'Начинки')]");
    private final By logotypeButton = By.xpath(".//*[@class='AppHeader_header__logo__2D0X2']");
    private final By constructorButton = By.xpath(".//*[@href='/' and contains(.,'Конструктор')]");

    // Локатор активности перехода по кнопкам/разделам "Булки", "Соусы", "Начинки" главной страницы
    private final By locatorBunsSaucesToppingsButtons = By.xpath(".//*[@style='display: flex;']");

    // Геттеры для локаторов
    public By getEnterAccountButton() {
        return enterAccountButton;
    }

    public By getPersonalAccountButton() {
        return personalAccountButton;
    }

    public By getCheckoutButtonLocator() {
        return checkoutButton;
    }

    public By getBunsTab() {
        return bunsTab;
    }

    public By getSaucesTab() {
        return saucesTab;
    }

    public By getFillingsTab() {
        return fillingsTab;
    }

    public By getLogotypeButton() {
        return logotypeButton;
    }

    public By getConstructorButton() {
        return constructorButton;
    }

    public By getLocatorBunsSaucesToppingsButtons() {
        return locatorBunsSaucesToppingsButtons;
    }

    // Инициализация драйвера
    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    // Скроллим элемент в зону видимости
    protected void scrollToElement(By by) {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Метод для клика на кнопку Войти в аккаунт
    public void clickEnterAccountButton() {
        scrollToElement(getEnterAccountButton());
        driver.findElement(getEnterAccountButton()).click();
    }

    // Метод для клика на кнопку Личный кабинет
    public void clickPersonalAccountButton() {
        scrollToElement(getPersonalAccountButton());
        driver.findElement(getPersonalAccountButton()).click();
    }

    // Метод для перехода на вкладку Булки
    public void clickBunsTab() {
        scrollToElement(getBunsTab());
        driver.findElement(getBunsTab()).click();
    }

    // Метод для перехода на вкладку Соусы
    public void clickSaucesTab() {
        scrollToElement(getSaucesTab());
        driver.findElement(getSaucesTab()).click();
    }

    // Метод для клика на логотип
    public void clickLogotypeTab() {
        scrollToElement(getLogotypeButton());
        driver.findElement(getLogotypeButton()).click();
    }

    // Метод для клика на вкладку Конструктор
    public void clickConstructor() {
        scrollToElement(getConstructorButton());
        driver.findElement(getConstructorButton()).click();
    }

    // Метод для перехода на вкладку Начинки
    public void clickFillingsTab() {
        scrollToElement(getFillingsTab());
        driver.findElement(getFillingsTab()).click();
    }

    // Метод для проверки активности вкладок Булки/Соусы/Начинки
    public boolean isTabActive() {
        List<WebElement> elements = driver.findElements(getLocatorBunsSaucesToppingsButtons());
        return !elements.isEmpty(); // активная вкладка имеется, если хотя бы один элемент существует
    }

    // Ожидание кнопки "Оформить заказ"
    public void waitForCheckoutButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(checkoutButton));
    }

    // Возвращает кнопку "Оформить заказ"
    public WebElement getCheckoutButton() {
        return driver.findElement(getCheckoutButtonLocator());
    }
}