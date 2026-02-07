package pages;

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

    // Локаторы элементов
    public static final By ENTER_ACCOUNT_BUTTON = By.xpath("//button[contains(@class, 'button_button__33qZ0') and text()='Войти в аккаунт']");
    public static final By PERSONAL_ACCOUNT_BUTTON = By.xpath("//p[contains(@class, 'AppHeader_header__linkText__3q_va') and normalize-space()='Личный Кабинет']");
    public static final By CHECKOUT_BUTTON = By.xpath("//button[.='Оформить заказ']");
    public static final By BUNS_TAB = By.xpath(".//*[@class='text text_type_main-default' and contains(.,'Булки')]");
    public static final By SAUCES_TAB = By.xpath(".//*[@class='text text_type_main-default' and contains(.,'Соусы')]");
    public static final By FILLINGS_TAB = By.xpath(".//*[@class='text text_type_main-default' and contains(.,'Начинки')]");

    public static final By LogotypeButton = By.xpath(".//*[@class='AppHeader_header__logo__2D0X2']");
    public static final By ConstructorButton = By.xpath(".//*[@href='/' and contains(.,'Конструктор')]");

    // Локатор перехода по кнопкам/разделам "Булки", "Соусы", "Начинки" главной страницы
    private final By locatorBunsSaucesToppingsButtons = By.xpath(".//*[@style='display: flex;']");

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    // Скроллинг элемента в зону видимости
    protected void scrollToElement(By by) {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Метод для клика на кнопку Войти в аккаунт
    public void clickEnterAccountButton() {
        scrollToElement(ENTER_ACCOUNT_BUTTON);
        driver.findElement(ENTER_ACCOUNT_BUTTON).click();
    }

    // Метод для клика на кнопку Личный кабинет
    public void clickPersonalAccountButton() {
        scrollToElement(PERSONAL_ACCOUNT_BUTTON);
        driver.findElement(PERSONAL_ACCOUNT_BUTTON).click();
    }

    // Метод для перехода на вкладку Булки
    public void clickBunsTab() {
        scrollToElement(BUNS_TAB);
        driver.findElement(BUNS_TAB).click();
    }

    // Метод для перехода на вкладку Соусы
    public void clickSaucesTab() {
        scrollToElement(SAUCES_TAB);
        driver.findElement(SAUCES_TAB).click();
    }

    // Метод для клика на логотип
    public void clickLogotypeTab() {
        scrollToElement(LogotypeButton);
        driver.findElement(LogotypeButton).click();
    }

    // Метод для клика на вкладку Конструктор
    public void clickConstructor() {
        scrollToElement(ConstructorButton);
        driver.findElement(ConstructorButton).click();
    }

    // Метод для перехода на вкладку Начинки
    public void clickFillingsTab() {
        scrollToElement(FILLINGS_TAB);
        driver.findElement(FILLINGS_TAB).click();
    }

    // Метод для проверки активности вкладок Булки/Соусы/Начинки
    public By checkLocatorBunsSaucesToppingsButtons() {
        return locatorBunsSaucesToppingsButtons;
    }

    // Метод для проверки активного состояния вкладки
    public boolean isTabActive() {
        List<WebElement> elements = driver.findElements(locatorBunsSaucesToppingsButtons);
        return !elements.isEmpty(); // Активная вкладка имеется, если хотя бы один элемент существует
    }

    // Ожидание кнопки "Оформить заказ"
    public void waitForCheckoutButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CHECKOUT_BUTTON));
    }

    // Возвращает кнопку "Оформить заказ"
    public WebElement getCheckoutButton() {
        return driver.findElement(CHECKOUT_BUTTON);
    }

}