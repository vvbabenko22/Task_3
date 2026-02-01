package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class MainPage {

    private WebDriver driver;

    // Локаторы элементов
    public static final By ENTER_ACCOUNT_BUTTON = By.xpath("//button[contains(@class, 'button_button__33qZ0') and text()='Войти в аккаунт']");
    public static final By PERSONAL_ACCOUNT_BUTTON = By.xpath("//p[contains(@class, 'AppHeader_header__linkText__3q_va') and normalize-space()='Личный Кабинет']");
    public static final By CHECKOUT_BUTTON = By.xpath("//button[.='Оформить заказ']");
    public static final By BUNS_TAB = By.xpath("//span[text()='Булки']/ancestor::div[contains(@class,'constructor')]/*[contains(@class,'tab')][1]");
    public static final By SAUCES_TAB = By.xpath("//span[text()='Соусы']/ancestor::div[contains(@class,'constructor')]/*[contains(@class,'tab')][2]");
    public static final By FILLINGS_TAB = By.xpath("//span[text()='Начинки']/ancestor::div[contains(@class,'constructor')]/*[contains(@class,'tab')][3]");

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

    // Метод для перехода на вкладку Начинки
    public void clickFillingsTab() {
        scrollToElement(FILLINGS_TAB);
        driver.findElement(FILLINGS_TAB).click();
    }

    // Получение атрибута класса для проверки активности табов
    public String getClassNameByTab(By tabLocator) {
        return driver.findElement(tabLocator).getAttribute("class");
    }

    // Проверка активного статуса таба
    public boolean isTabActive(By tabLocator) {
        return getClassNameByTab(tabLocator).contains("tab_tab_type_current__2BEPc");
    }

    // Кликнуть на вкладку и ожидать активность
    public void clickTabAndWaitActive(By tabLocator) throws InterruptedException {
        scrollToElement(tabLocator);
        driver.findElement(tabLocator).click();

        long startTime = System.currentTimeMillis();
        long endTime = startTime + 5000; // Таймаут в миллисекундах

        while (!isTabActive(tabLocator)) {
            if (System.currentTimeMillis() > endTime) {
                throw new RuntimeException("Вкладка не активировалась в течение заданного таймаута.");
            }
            Thread.sleep(200);
        }
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