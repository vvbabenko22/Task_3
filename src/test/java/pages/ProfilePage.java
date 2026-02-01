package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProfilePage {

    private final WebDriver driver;

    // Локаторы элементов
    public static final By PROFILE_TEXT = By.xpath(".//*[contains(text(), 'В этом разделе вы можете изменить свои персональные данные')]");
    public static final By NAME_INPUT = By.xpath(".//li[1]//input");
    public static final By EMAIL_INPUT = By.xpath(".//li[2]//input");

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
    }

}